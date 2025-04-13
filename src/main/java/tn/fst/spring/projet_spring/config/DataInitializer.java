    package tn.fst.spring.projet_spring.config;

    import org.springframework.boot.CommandLineRunner;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import tn.fst.spring.projet_spring.model.auth.*;
    import tn.fst.spring.projet_spring.model.catalog.*;
    import tn.fst.spring.projet_spring.repositories.auth.*;
    import tn.fst.spring.projet_spring.repositories.products.*;

    import java.util.*;

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
                ProductPositionRepository productPositionRepository
                ) {

            return args -> {
                // Suppression des données existantes
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
                // createProduct("Huile d'olive Tunisienne", "6191234567890", "Huile d'olive extra vierge 1L",
                // 25.5, alimentation, 100, 10, shelf2, productRepository, stockRepository,
                // productPositionRepository, 1, 1);

                // createProduct("Dattes Deglet Nour", "6192345678901", "Dattes premium 500g",
                //             18.0, alimentation, 150, 20, shelf2, productRepository, stockRepository,
                //             productPositionRepository, 2, 1);

            // 5. Produits initiaux
            insertProductIfNotExist("Huile d'olive Tunisienne", "6191234567890", "Huile d'olive extra vierge 1L",25.5, "Alimentation", 100, 10, Long.valueOf(1L),4,6, categoryRepository, shelfRepository,productRepository, stockRepository,productPositionRepository);

            System.out.println("✅ Initialisation terminée.");
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

}