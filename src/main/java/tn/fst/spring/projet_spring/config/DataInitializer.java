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
            insertShelfIfNotExist(shelfRepository, "Rayon frais", "Réfrigéré");
            insertShelfIfNotExist(shelfRepository, "Rayon sec", "Normal");
            insertShelfIfNotExist(shelfRepository, "Rayon artisanal", "Normal");

            // 5. Produits initiaux
            insertProductIfMissing("Huile d'olive Tunisienne", "6191234567890", "Huile extra vierge", 25.5, "Alimentation", 100, 10, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Dattes Deglet Nour", "6192345678901", "Dattes premium 500g", 18.0, "Alimentation", 150, 20, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Poterie de Nabeul", "6193456789012", "Pot décoratif", 45.0, "Artisanat", 30, 5, "Rayon artisanal", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Savon d'Alep", "6194567890123", "Savon naturel", 12.5, "Cosmétique", 80, 15, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Miel de thym", "6197890123456", "Miel naturel", 35.0, "Alimentation", 40, 8, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);

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
