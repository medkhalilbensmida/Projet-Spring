    package tn.fst.spring.projet_spring.config;

    import org.springframework.boot.CommandLineRunner;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import tn.fst.spring.projet_spring.model.auth.*;
    import tn.fst.spring.projet_spring.model.catalog.*;
    import tn.fst.spring.projet_spring.model.order.*;
    import tn.fst.spring.projet_spring.model.logistics.*;
    import tn.fst.spring.projet_spring.repositories.auth.*;
    import tn.fst.spring.projet_spring.repositories.products.*;
    import tn.fst.spring.projet_spring.repositories.order.*;
    import tn.fst.spring.projet_spring.repositories.logistics.*;

    import java.util.*;
    import java.time.LocalDateTime;
    import java.util.Collections;
    import java.math.BigDecimal;

    @Configuration
    public class DataInitializer {

        @Bean
        CommandLineRunner initDatabase(
                UserRepository userRepository,
                RoleRepository roleRepository,
                ProductRepository productRepository,
                CategoryRepository categoryRepository,
                StockRepository stockRepository,
                ShelfRepository shelfRepository,
                PasswordEncoder passwordEncoder,
                ProductPositionRepository productPositionRepository,
                LivreurRepository livreurRepository,
                OrderRepository orderRepository,
                OrderItemRepository orderItemRepository,
                DeliveryRequestRepository deliveryRequestRepository
                ) {

            return args -> {
                // Suppression des données existantes
                deliveryRequestRepository.deleteAll();
                orderItemRepository.deleteAll();
                orderRepository.deleteAll();
                livreurRepository.deleteAll();
                stockRepository.deleteAll();
                productPositionRepository.deleteAll();
                productRepository.deleteAll();
                shelfRepository.deleteAll();
                categoryRepository.deleteAll();
                roleRepository.deleteAll();
                userRepository.deleteAll();

            // 1. Rôles nécessaires (déduits du cahier des charges)
            insertRoleIfNotExist(roleRepository, "ROLE_ADMIN", "Administrateur principal");
            insertRoleIfNotExist(roleRepository, "ROLE_CUSTOMER", "Client consommateur");
            insertRoleIfNotExist(roleRepository, "ROLE_PRODUCT_MANAGER", "Responsable des produits");
            insertRoleIfNotExist(roleRepository, "ROLE_SHELF_MANAGER", "Chef de rayon");
            insertRoleIfNotExist(roleRepository, "ROLE_DELIVERY_MANAGER", "Responsable de livraison");
            insertRoleIfNotExist(roleRepository, "ROLE_EVENT_MANAGER", "Responsable caritatif");

            // 2. Utilisateurs initiaux
            insertUserIfNotExist(userRepository, "admin", "admin@consummi.tn", "admin123", "ROLE_ADMIN", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "customer1", "customer1@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "pmanager", "pm@consummi.tn", "pm123", "ROLE_PRODUCT_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "chefRayon", "shelf@consummi.tn", "shelf123", "ROLE_SHELF_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "livreur", "delivery@consummi.tn", "delivery123", "ROLE_DELIVERY_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "event", "event@consummi.tn", "event123", "ROLE_EVENT_MANAGER", roleRepository, passwordEncoder);

            // 3. Catégories
            insertCategoryIfNotExist(categoryRepository, "Alimentation", "Produits alimentaires tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Artisanat", "Produits artisanaux tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Cosmétique", "Produits cosmétiques naturels tunisiens");

            // 4. Rayons
            insertShelfIfNotExist(shelfRepository,"Rayon frais", "Réfrigéré", 1, 1, 2, 2);
            insertShelfIfNotExist(shelfRepository,"Rayon sec", "Normal" ,1, 2, 2, 2);
            insertShelfIfNotExist(shelfRepository,"Rayon artisanal", "Normal", 1, 3, 2, 2);

            // 5. Produits initiaux
            insertProductIfNotExist("Huile d'olive Tunisienne", "6191234567890", "Huile d'olive extra vierge 1L",25.5, "Alimentation", 100, 10, Long.valueOf(1L),4,6, categoryRepository, shelfRepository,productRepository, stockRepository,productPositionRepository);

            // 6. Livreurs
            Livreur livreur1 = insertLivreurIfNotExist(livreurRepository, "Ahmed Ben Ali", true);
            Livreur livreur2 = insertLivreurIfNotExist(livreurRepository, "Fatma Gharbi", true);

            // 7. Commandes et Livraisons pour tester les primes
            User customer = userRepository.findByEmail("customer1@mail.com")
                                          .orElseThrow(() -> new RuntimeException("Customer 'customer1@mail.com' not found"));
            Product huileOlive = productRepository.findByBarcode("6191234567890")
                                                 .orElseThrow(() -> new RuntimeException("Product 'Huile d'olive Tunisienne' not found"));

            // Commande 1 (Livrée par Ahmed)
            Order order1 = createOrder(orderRepository, orderItemRepository, customer, huileOlive, 2, "ORD001");
            createDeliveryRequest(deliveryRequestRepository, order1, livreur1, DeliveryStatus.DELIVERED, 5.0);

            // Commande 2 (Livrée par Ahmed)
            Order order2 = createOrder(orderRepository, orderItemRepository, customer, huileOlive, 1, "ORD002");
            createDeliveryRequest(deliveryRequestRepository, order2, livreur1, DeliveryStatus.DELIVERED, 5.0);

            // Commande 3 (En cours par Fatma)
            Order order3 = createOrder(orderRepository, orderItemRepository, customer, huileOlive, 3, "ORD003");
            createDeliveryRequest(deliveryRequestRepository, order3, livreur2, DeliveryStatus.IN_TRANSIT, 6.0);

            // Commande 4 (Assignée à Ahmed)
            Order order4 = createOrder(orderRepository, orderItemRepository, customer, huileOlive, 1, "ORD004");
            createDeliveryRequest(deliveryRequestRepository, order4, livreur1, DeliveryStatus.ASSIGNED, 5.0);

            // Commande 5 (Livrée par Fatma)
            Order order5 = createOrder(orderRepository, orderItemRepository, customer, huileOlive, 2, "ORD005");
            createDeliveryRequest(deliveryRequestRepository, order5, livreur2, DeliveryStatus.DELIVERED, 6.0);

            System.out.println("✅ Initialisation terminée (avec commandes et livraisons).");
        };
    }

    private void insertRoleIfNotExist(RoleRepository repo, String name, String description) {
        if (repo.findByName(name).isEmpty()) {
            repo.save(new Role(name, description));
        }
    }

    private void insertUserIfNotExist(UserRepository repo, String username, String email, String pwd, String roleName,
                                      RoleRepository roleRepo, PasswordEncoder encoder) {
        if (repo.findByEmail(email).isEmpty()) {
            Role role = roleRepo.findByName(roleName)
                    .orElseThrow(() -> new IllegalArgumentException("Rôle introuvable : " + roleName));
            repo.save(new User(username, email, encoder.encode(pwd), role));
        }
    }

    private void insertCategoryIfNotExist(CategoryRepository repo, String name, String desc) {
        if (repo.findByName(name).isEmpty()) {
            repo.save(new Category(name, desc));
        }
    }

    private void insertShelfIfNotExist(ShelfRepository repo, String name, String type, int x,int y,int width,int height) {
        if (repo.findByName(name).isEmpty()) {
            Shelf shelf = new Shelf();
            shelf.setName(name);
            shelf.setType(type);
            shelf.setX(x);
            shelf.setY(y);
            shelf.setWidth(width);
            shelf.setHeight(height);
            repo.save(shelf);
        }
    }

    private void insertProductIfNotExist( String name, String barcode,
    String description, double price, String categoryName, int quantity, int minThreshold, Long shelfId,int x, int y,
    CategoryRepository categoryRepository, ShelfRepository shelfRepository,
    ProductRepository productRepository, StockRepository stockRepository,ProductPositionRepository productPositionRepository) {

        if (productRepository.findByBarcode(barcode).isEmpty()) {
            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable : " + categoryName));
            Shelf shelf = shelfRepository.findById(shelfId)
                    .orElseThrow(() -> new IllegalArgumentException("Rayon introuvable : " + shelfId));

            Product product = new Product();
            product.setName(name);
            product.setBarcode(barcode);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);

            product = productRepository.save(product);

            Stock stock = new Stock();
            stock.setProduct(product);
            stock.setQuantity(quantity);
            stock.setMinThreshold(minThreshold);
            stockRepository.save(stock);

        // ✅ Create product position
        ProductPosition position = new ProductPosition();
        position.setProduct(product);
        position.setShelf(shelf);
        position.setX(x); // Set the X coordinate
        position.setY(y); // Set the Y coordinate
        productPositionRepository.save(position);
        }
    }

    private Livreur insertLivreurIfNotExist(LivreurRepository repo, String nom, boolean disponible) {
        Optional<Livreur> existing = repo.findAll().stream().filter(l -> l.getNom().equals(nom)).findFirst();
        return existing.orElseGet(() -> {
            Livreur livreur = new Livreur();
            livreur.setNom(nom);
            livreur.setDisponible(disponible);
            return repo.save(livreur);
        });
    }

    private Order createOrder(OrderRepository orderRepo, OrderItemRepository itemRepo, User customer, Product product, int quantity, String orderNumber) {
        Order order = new Order();
        order.setUser(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setSaleType(SaleType.ONLINE);
        order.setOrderNumber(orderNumber);
        order.setCustomerAddress("Default Address");
        order.setCustomerPhone("00000000");

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());

        order.getItems().add(item);

        order.setTotalAmount(item.getUnitPrice() * quantity);

        Order savedOrder = orderRepo.save(order);
        return savedOrder;
    }

    private void createDeliveryRequest(DeliveryRequestRepository deliveryRepo, Order order, Livreur livreur, DeliveryStatus status, double fees) {
        DeliveryRequest request = new DeliveryRequest();
        request.setOrder(order);
        request.setLivreur(livreur);
        request.setStatus(status);
        request.setDeliveryFee(fees);
        deliveryRepo.save(request);
    }

}