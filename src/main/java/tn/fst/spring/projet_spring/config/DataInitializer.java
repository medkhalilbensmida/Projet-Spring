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
            PasswordEncoder passwordEncoder) {

        return args -> {
            // 1. Rôles nécessaires (fixes comme demandé)
            insertRoleIfNotExist(roleRepository, "ROLE_ADMIN", "Administrateur principal");
            insertRoleIfNotExist(roleRepository, "ROLE_CUSTOMER", "Client consommateur");
            insertRoleIfNotExist(roleRepository, "ROLE_PRODUCT_MANAGER", "Responsable des produits");
            insertRoleIfNotExist(roleRepository, "ROLE_SHELF_MANAGER", "Chef de rayon");
            insertRoleIfNotExist(roleRepository, "ROLE_DELIVERY_MANAGER", "Responsable de livraison");
            insertRoleIfNotExist(roleRepository, "ROLE_EVENT_MANAGER", "Responsable caritatif");

            // 2. Utilisateurs initiaux (enrichi)
            insertUserIfNotExist(userRepository, "admin", "admin@consummi.tn", "admin123", "ROLE_ADMIN", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "customer1", "customer1@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "customer2", "customer2@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "customer3", "customer3@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "pmanager", "pm@consummi.tn", "pm123", "ROLE_PRODUCT_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "pmanager2", "pm2@consummi.tn", "pm123", "ROLE_PRODUCT_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "chefRayon", "shelf@consummi.tn", "shelf123", "ROLE_SHELF_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "chefRayon2", "shelf2@consummi.tn", "shelf123", "ROLE_SHELF_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "livreur", "delivery@consummi.tn", "delivery123", "ROLE_DELIVERY_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "livreur2", "delivery2@consummi.tn", "delivery123", "ROLE_DELIVERY_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "event", "event@consummi.tn", "event123", "ROLE_EVENT_MANAGER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "event2", "event2@consummi.tn", "event123", "ROLE_EVENT_MANAGER", roleRepository, passwordEncoder);

            // 3. Catégories (enrichi)
            insertCategoryIfNotExist(categoryRepository, "Alimentation", "Produits alimentaires tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Artisanat", "Produits artisanaux tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Cosmétique", "Produits cosmétiques naturels tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Boissons", "Boissons tunisiennes");
            insertCategoryIfNotExist(categoryRepository, "Textile", "Vêtements et tissus tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Epicerie", "Produits d'épicerie fine");
            insertCategoryIfNotExist(categoryRepository, "Cuisine", "Ustensiles de cuisine traditionnelle");
            insertCategoryIfNotExist(categoryRepository, "Décoration", "Articles de décoration");

            // 4. Rayons (enrichi)
            insertShelfIfNotExist(shelfRepository, "Rayon frais", "Réfrigéré");
            insertShelfIfNotExist(shelfRepository, "Rayon sec", "Normal");
            insertShelfIfNotExist(shelfRepository, "Rayon artisanal", "Normal");
            insertShelfIfNotExist(shelfRepository, "Rayon boissons", "Normal");
            insertShelfIfNotExist(shelfRepository, "Rayon surgelés", "Congélateur");
            insertShelfIfNotExist(shelfRepository, "Rayon textile", "Normal");
            insertShelfIfNotExist(shelfRepository, "Rayon épicerie", "Normal");
            insertShelfIfNotExist(shelfRepository, "Rayon décoration", "Fragile");

            // 5. Produits initiaux (fortement enrichi)
            // Alimentation
            insertProductIfMissing("Huile d'olive Tunisienne", "6191234567890", "Huile extra vierge", 25.5, "Alimentation", 100, 10, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Dattes Deglet Nour", "6192345678901", "Dattes premium 500g", 18.0, "Alimentation", 150, 20, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Miel de thym", "6197890123456", "Miel naturel", 35.0, "Alimentation", 40, 8, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Harissa", "6191234509876", "Pâte de piment 250g", 8.5, "Alimentation", 75, 15, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Couscous fin", "6191234512345", "Couscous 1kg", 6.0, "Alimentation", 120, 30, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Confiture de figue", "6191234523456", "Confiture artisanale", 12.0, "Alimentation", 50, 10, "Rayon frais", categoryRepository, shelfRepository, productRepository, stockRepository);

            // Artisanat
            insertProductIfMissing("Poterie de Nabeul", "6193456789012", "Pot décoratif", 45.0, "Artisanat", 30, 5, "Rayon artisanal", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Tapis berbère", "6193456712345", "Tapis laine 2x3m", 320.0, "Artisanat", 15, 3, "Rayon textile", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Plateau en cuivre", "6193456723456", "Plateau gravé", 75.0, "Artisanat", 25, 5, "Rayon artisanal", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Vase en céramique", "6193456734567", "Vase décoratif", 55.0, "Artisanat", 20, 4, "Rayon décoration", categoryRepository, shelfRepository, productRepository, stockRepository);

            // Cosmétique
            insertProductIfMissing("Savon d'Alep", "6194567890123", "Savon naturel", 12.5, "Cosmétique", 80, 15, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Huile d'argan", "6194567812345", "Huile cosmétique", 42.0, "Cosmétique", 35, 7, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Gommage au rhassoul", "6194567823456", "Masque argileux", 18.0, "Cosmétique", 45, 9, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);

            // Boissons
            insertProductIfMissing("Thé à la menthe", "6195678912345", "Thé vert 250g", 15.0, "Boissons", 60, 12, "Rayon boissons", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Jus de grenade", "6195678923456", "Jus 100% naturel", 8.0, "Boissons", 90, 18, "Rayon frais", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Café moulu", "6195678934567", "Café tunisien", 22.0, "Boissons", 40, 8, "Rayon boissons", categoryRepository, shelfRepository, productRepository, stockRepository);

            // Textile
            insertProductIfMissing("Chéchia rouge", "6196789123456", "Chéchia traditionnelle", 35.0, "Textile", 25, 5, "Rayon textile", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Jebba homme", "6196789234567", "Tenue traditionnelle", 120.0, "Textile", 15, 3, "Rayon textile", categoryRepository, shelfRepository, productRepository, stockRepository);

            // Epicerie
            insertProductIfMissing("Pâtes d'amande", "6197891234567", "Pâtes artisanales", 28.0, "Epicerie", 30, 6, "Rayon épicerie", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Fruits secs", "6197892345678", "Mélange premium", 32.0, "Epicerie", 45, 9, "Rayon épicerie", categoryRepository, shelfRepository, productRepository, stockRepository);

            System.out.println("✅ Initialisation terminée avec succès (" + productRepository.count() + " produits insérés)");
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

    private void insertShelfIfNotExist(ShelfRepository repo, String name, String desc) {
        if (repo.findByName(name).isEmpty()) {
            repo.save(new Shelf(name, desc));
        }
    }

    private void insertProductIfMissing(String name, String barcode, String description, double price,
                                        String categoryName, int quantity, int minThreshold, String shelfName,
                                        CategoryRepository categoryRepo, ShelfRepository shelfRepo,
                                        ProductRepository productRepo, StockRepository stockRepo) {
        if (productRepo.findByBarcode(barcode).isEmpty()) {
            Category category = categoryRepo.findByName(categoryName)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable : " + categoryName));
            Shelf shelf = shelfRepo.findByName(shelfName)
                    .orElseThrow(() -> new IllegalArgumentException("Rayon introuvable : " + shelfName));

            Product product = new Product();
            product.setName(name);
            product.setBarcode(barcode);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product.getShelves().add(shelf);

            product = productRepo.save(product);

            Stock stock = new Stock();
            stock.setProduct(product);
            stock.setQuantity(quantity);
            stock.setMinThreshold(minThreshold);
            stockRepo.save(stock);
        }
    }
}