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
import java.util.concurrent.ThreadLocalRandom; // Import for random coordinates

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
            insertRoleIfNotExist(roleRepository, "ROLE_ADMIN", "Administrateur principal");
            insertRoleIfNotExist(roleRepository, "ROLE_CUSTOMER", "Client consommateur");
            insertRoleIfNotExist(roleRepository, "ROLE_PRODUCT_MANAGER", "Responsable des produits");
            insertRoleIfNotExist(roleRepository, "ROLE_SHELF_MANAGER", "Chef de rayon");
            insertRoleIfNotExist(roleRepository, "ROLE_DELIVERY_MANAGER", "Responsable de livraison");
            insertRoleIfNotExist(roleRepository, "ROLE_EVENT_MANAGER", "Responsable caritatif");

            insertUserIfNotExist(userRepository, "Ahmed Mhadhbi", "ahmed.mhadhbi@consummi.tn", "admin123", "ROLE_ADMIN", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Yasmine Ben Slimane", "yasmine.benslimane@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Fadi Abaidi", "fadi.abaidi@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Mohamed Hedi Aissi", "hedi.aissi@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Mohamed Khalil Ben Smida", "khalil.bensmida@consummi.tn", "pm123", "ROLE_PRODUCT_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Sana Chatti", "sana.chatti@consummi.tn", "pm123", "ROLE_PRODUCT_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Rayen Ben Amor", "rayen.shelf@consummi.tn", "shelf123", "ROLE_SHELF_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Marwa Mansour", "marwa.shelf@consummi.tn", "shelf123", "ROLE_SHELF_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Aymen Trabelsi", "aymen.delivery@consummi.tn", "delivery123", "ROLE_DELIVERY_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Latifa Khemiri", "latifa.delivery@consummi.tn", "delivery123", "ROLE_DELIVERY_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Nadia Ferjani", "nadia.event@consummi.tn", "event123", "ROLE_EVENT_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Rami Gharbi", "rami.event@consummi.tn", "event123", "ROLE_EVENT_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "Ahmed edf", "test", "test", "ROLE_ADMIN", roleRepository, passwordEncoder);
            insertCategoryIfNotExist(categoryRepository, "Alimentation", "Produits alimentaires tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Artisanat", "Produits artisanaux tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Cosmétique", "Produits cosmétiques naturels tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Boissons", "Boissons tunisiennes");
            insertCategoryIfNotExist(categoryRepository, "Textile", "Vêtements et tissus tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Epicerie", "Produits d'épicerie fine");
            insertCategoryIfNotExist(categoryRepository, "Cuisine", "Ustensiles de cuisine traditionnelle");
            insertCategoryIfNotExist(categoryRepository, "Décoration", "Articles de décoration");

            insertShelfIfNotExist(shelfRepository, "Rayon sec", "Normal", 0, 0, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon frais", "Réfrigéré", 3, 0, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon artisanal", "Normal", 0, 3, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon boissons", "Normal", 3, 3, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon textile", "Normal", 0, 6, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon épicerie", "Normal", 3, 6, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon décoration", "Fragile", 6, 0, 3, 3);

// 5. Produits initiaux (fortement enrichi)

// Alimentation
            insertProduct("Huile d'olive Tunisienne", "6191234567890", "Huile extra vierge", 25.5, 1.0, "Alimentation", 100, 10, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Dattes Deglet Nour", "6192345678901", "Dattes premium 500g", 18.0, 0.5, "Alimentation", 150, 20, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Miel de thym", "6197890123456", "Miel naturel", 35.0, 0.5, "Alimentation", 40, 8, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Harissa", "6191234509876", "Pâte de piment 250g", 8.5, 0.25, "Alimentation", 75, 15, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Couscous fin", "6191234512345", "Couscous 1kg", 6.0, 1.0, "Alimentation", 120, 30, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Confiture de figue", "6191234523456", "Confiture artisanale", 12.0, 0.3, "Alimentation", 50, 10, "Rayon frais", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

// Artisanat
            insertProduct("Poterie de Nabeul", "6193456789012", "Pot décoratif", 45.0, 2.5, "Artisanat", 30, 5, "Rayon artisanal", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Tapis berbère", "6193456712345", "Tapis laine 2x3m", 320.0, 15.0, "Artisanat", 15, 3, "Rayon textile", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau en cuivre", "6193456723456", "Plateau gravé", 75.0, 1.5, "Artisanat", 25, 5, "Rayon artisanal", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Vase en céramique", "6193456734567", "Vase décoratif", 55.0, 1.8, "Artisanat", 20, 4, "Rayon décoration", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

// Cosmétique
            insertProduct("Savon d'Alep", "6194567890123", "Savon naturel", 12.5, 0.15, "Cosmétique", 80, 15, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Huile d'argan", "6194567812345", "Huile cosmétique", 42.0, 0.2, "Cosmétique", 35, 7, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Gommage au rhassoul", "6194567823456", "Masque argileux", 18.0, 0.25, "Cosmétique", 45, 9, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

// Boissons
            insertProduct("Thé à la menthe", "6195678912345", "Thé vert 250g", 15.0, 0.25, "Boissons", 60, 12, "Rayon boissons", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Jus de grenade", "6195678923456", "Jus 100% naturel", 8.0, 1.0, "Boissons", 90, 18, "Rayon frais", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Café moulu", "6195678934567", "Café tunisien", 22.0, 0.5, "Boissons", 40, 8, "Rayon boissons", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

// Textile
            insertProduct("Chéchia rouge", "6196789123456", "Chéchia traditionnelle", 35.0, 0.1, "Textile", 25, 5, "Rayon textile", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Jebba homme", "6196789234567", "Tenue traditionnelle", 120.0, 0.8, "Textile", 15, 3, "Rayon textile", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

// Epicerie
            insertProduct("Pâtes d'amande", "6197891234567", "Pâtes artisanales", 28.0, 0.4, "Epicerie", 30, 6, "Rayon épicerie", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Fruits secs", "6197892345678", "Mélange premium", 32.0, 0.5, "Epicerie", 45, 9, "Rayon épicerie", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // 6. Insertion des livreurs avec coordonnées
            Livreur livreur1 = insertLivreurIfNotExist(livreurRepository, "Ahmed Ben Ali", false, 36.83 + ThreadLocalRandom.current().nextDouble(0.01, 0.02), 10.15 + ThreadLocalRandom.current().nextDouble(0.01, 0.02));
            Livreur livreur2 = insertLivreurIfNotExist(livreurRepository, "Fatma Gharbi", false, 36.84 + ThreadLocalRandom.current().nextDouble(0.005, 0.015), 10.16 + ThreadLocalRandom.current().nextDouble(0.005, 0.015));
            insertLivreurIfNotExist(livreurRepository, "Samir Khelifi", true, 36.83 + ThreadLocalRandom.current().nextDouble(0.005, 0.01), 10.17 + ThreadLocalRandom.current().nextDouble(0.005, 0.01));
            insertLivreurIfNotExist(livreurRepository, "Hela Maatoug", true, 36.833833, 10.148004);
            insertLivreurIfNotExist(livreurRepository, "Karim Jouini", true, 36.835556, 10.142389);

            User customerFadi = userRepository.findByEmail("fadi.abaidi@mail.com").orElseThrow();
            User customerYasmine = userRepository.findByEmail("yasmine.benslimane@mail.com").orElseThrow();
            Product huileOlive = productRepository.findByBarcode("6191234567890").orElseThrow();
            Product dattes = productRepository.findByBarcode("6192345678901").orElseThrow();

            // 7. Create Orders and Assigned Delivery Requests
            Order order1 = createOrder(orderRepository, orderItemRepository, customerFadi, huileOlive, 2, "ORD001");
            createDeliveryRequest(deliveryRequestRepository, order1, livreur1, DeliveryStatus.DELIVERED, 5.0, 36.8008, 10.1815); // Tunis Centre

            Order order2 = createOrder(orderRepository, orderItemRepository, customerFadi, huileOlive, 1, "ORD002");
            createDeliveryRequest(deliveryRequestRepository, order2, livreur1, DeliveryStatus.DELIVERED, 5.0, 36.8454, 10.1941); // La Marsa

            Order order3 = createOrder(orderRepository, orderItemRepository, customerFadi, huileOlive, 3, "ORD003");
            createDeliveryRequest(deliveryRequestRepository, order3, livreur2, DeliveryStatus.IN_TRANSIT, 6.0, 36.7948, 10.1007); // Manouba

            Order order4 = createOrder(orderRepository, orderItemRepository, customerFadi, huileOlive, 1, "ORD004");
            createDeliveryRequest(deliveryRequestRepository, order4, livreur1, DeliveryStatus.ASSIGNED, 5.0, 36.8665, 10.1647); // Ariana

            Order order5 = createOrder(orderRepository, orderItemRepository, customerFadi, huileOlive, 2, "ORD005");
            createDeliveryRequest(deliveryRequestRepository, order5, livreur2, DeliveryStatus.DELIVERED, 6.0, 36.8028, 10.1797); // Lac 1

            Order order6 = createOrder(orderRepository, orderItemRepository, customerYasmine, dattes, 5, "ORD006");
            createUnassignedDeliveryRequest(deliveryRequestRepository, order6, 7.5, 36.8550, 10.1850);

            System.out.println("✅ Initialisation complète réussie.");
        };

    }

    private void insertRoleIfNotExist(RoleRepository repo, String name, String description) {
        repo.findByName(name).ifPresentOrElse(role -> {
            // Si le rôle existe, ne rien faire
            System.out.println("Le rôle " + name + " existe déjà.");
        }, () -> {
            // Si le rôle n'existe pas, l'insérer
            repo.save(new Role(name, description));
            System.out.println("Rôle " + name + " inséré avec succès.");
        });
    }

    private void insertUserIfNotExist(UserRepository repo, String username, String email, String pwd, String roleName,
                                      RoleRepository roleRepo, PasswordEncoder encoder) {
        repo.findByEmail(email).ifPresentOrElse(user -> {
            // Si l'utilisateur existe, ne rien faire
            System.out.println("L'utilisateur avec l'email " + email + " existe déjà.");
        }, () -> {
            // Si l'utilisateur n'existe pas, l'insérer
            Role role = roleRepo.findByName(roleName).orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
            repo.save(new User(username, email, encoder.encode(pwd), role));
            System.out.println("Utilisateur " + username + " inséré avec succès.");
        });
    }

    private void insertCategoryIfNotExist(CategoryRepository repo, String name, String desc) {
        repo.findByName(name).ifPresentOrElse(category -> {
            // Si la catégorie existe, ne rien faire
            System.out.println("La catégorie " + name + " existe déjà.");
        }, () -> {
            // Si la catégorie n'existe pas, l'insérer
            repo.save(new Category(name, desc));
            System.out.println("Catégorie " + name + " insérée avec succès.");
        });
    }

    private void insertShelfIfNotExist(ShelfRepository repo, String name, String type, int x, int y, int width, int height) {
        repo.findByName(name).ifPresentOrElse(shelf -> {
            // Si le rayon existe, ne rien faire
            System.out.println("Le rayon " + name + " existe déjà.");
        }, () -> {
            // Si le rayon n'existe pas, l'insérer
            Shelf shelf = new Shelf();
            shelf.setName(name);
            shelf.setType(type);
            shelf.setX(x);
            shelf.setY(y);
            shelf.setWidth(width);
            shelf.setHeight(height);
            repo.save(shelf);
            System.out.println("Rayon " + name + " inséré avec succès.");
        });
    }

    private void insertProduct(String name, String barcode, String description, double price, double weight,
                               String categoryName,
                               int quantity, int minThreshold, String shelfName, int x, int y,
                               CategoryRepository categoryRepo, ShelfRepository shelfRepo,
                               ProductRepository productRepo, StockRepository stockRepo,
                               ProductPositionRepository positionRepo) {

        // Vérification automatique si le produit existe déjà dans la base de données
        productRepo.findByBarcode(barcode).ifPresentOrElse(product -> {
            // Si le produit existe, ne rien faire
            System.out.println("Le produit avec le barcode " + barcode + " existe déjà.");
        }, () -> {
            // Si le produit n'existe pas, l'insérer
            Category category = categoryRepo.findByName(categoryName).orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
            Shelf shelf = shelfRepo.findByName(shelfName).orElseThrow(() -> new RuntimeException("Rayon non trouvé"));

            // Création et enregistrement du produit
            Product product = new Product();
            product.setName(name);
            product.setBarcode(barcode);
            product.setDescription(description);
            product.setPrice(price);
            product.setWeight(weight);
            product.setCategory(category);
            product = productRepo.save(product);

            // Création et enregistrement du stock
            Stock stock = new Stock();
            stock.setProduct(product);
            stock.setQuantity(quantity);
            stock.setMinThreshold(minThreshold);
            stockRepo.save(stock);

            // Création et enregistrement de la position du produit
            ProductPosition position = new ProductPosition();
            position.setProduct(product);
            position.setShelf(shelf);
            position.setX(x);
            position.setY(y);
            position.setWidth(1);
            position.setHeight(1);
            position.setZIndex(0);
            positionRepo.save(position);

            System.out.println("Produit " + name + " inséré avec succès.");
        });
    }

    private Livreur insertLivreurIfNotExist(LivreurRepository repo, String nom, boolean disponible, Double latitude, Double longitude) {
        Optional<Livreur> existing = repo.findAll().stream().filter(l -> l.getNom().equals(nom)).findFirst();
        return existing.orElseGet(() -> {
            Livreur livreur = new Livreur();
            livreur.setNom(nom);
            livreur.setDisponible(disponible);
            livreur.setLatitude(latitude);
            livreur.setLongitude(longitude);
            Livreur savedLivreur = repo.save(livreur);
            System.out.printf("Livreur %s inséré avec succès (Lat: %.5f, Lon: %.5f).%n", nom, latitude, longitude);
            return savedLivreur;
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

        // Vérification si l'ordre existe déjà, sinon création
        if (!orderRepo.existsByOrderNumber(orderNumber)) {
            Order savedOrder = orderRepo.save(order);
            System.out.println("Commande " + orderNumber + " créée avec succès.");
            return savedOrder;
        } else {
            System.out.println("La commande avec le numéro " + orderNumber + " existe déjà.");
            return orderRepo.findByOrderNumber(orderNumber).orElse(null);
        }
    }

    private void createDeliveryRequest(DeliveryRequestRepository deliveryRepo, Order order, Livreur livreur,
                                       DeliveryStatus status, double fees,
                                       double destinationLat, double destinationLon) {
        if (order == null) {
            System.out.println("La commande est null. Impossible de créer la demande de livraison.");
            return; // Arrêter l'exécution si la commande est nulle
        }

        Optional<DeliveryRequest> existingRequest = deliveryRepo.findByOrder(order);
        existingRequest.ifPresentOrElse(request -> {
            System.out.println("La demande de livraison pour la commande " + order.getOrderNumber() + " existe déjà.");
        }, () -> {
            // Si la demande de livraison n'existe pas, la créer
            DeliveryRequest request = new DeliveryRequest();
            request.setOrder(order);  // Associer la commande à la demande
            request.setLivreur(livreur);
            request.setStatus(status);
            request.setDeliveryFee(fees);
            request.setDestinationLat(destinationLat);
            request.setDestinationLon(destinationLon);
            deliveryRepo.save(request);
            System.out.println("Demande de livraison pour la commande " + order.getOrderNumber() + " créée avec succès.");
        });
    }

    private void createUnassignedDeliveryRequest(DeliveryRequestRepository deliveryRepo, Order order,
                                               double fees,
                                               double destinationLat, double destinationLon) {
        if (order == null) {
            System.out.println("La commande est null. Impossible de créer la demande de livraison UNASSIGNED.");
            return; 
        }

        Optional<DeliveryRequest> existingRequest = deliveryRepo.findByOrder(order);
        existingRequest.ifPresentOrElse(request -> {
            System.out.println("La demande de livraison (UNASSIGNED) pour la commande " + order.getOrderNumber() + " existe déjà.");
        }, () -> {
            DeliveryRequest request = new DeliveryRequest();
            request.setOrder(order);
            request.setLivreur(null); // No livreur assigned initially
            request.setStatus(DeliveryStatus.PENDING); // Set initial status (e.g., PENDING)
            request.setDeliveryFee(fees);
            request.setDestinationLat(destinationLat);
            request.setDestinationLon(destinationLon);
            deliveryRepo.save(request);
            System.out.println("Demande de livraison UNASSIGNED pour la commande " + order.getOrderNumber() + " créée avec succès.");
        });
    }

}
