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
            ProductPositionRepository productPositionRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // 1. Rôles
            insertRoleIfNotExist(roleRepository, "ROLE_ADMIN", "Administrateur principal");
            insertRoleIfNotExist(roleRepository, "ROLE_CUSTOMER", "Client consommateur");
            insertRoleIfNotExist(roleRepository, "ROLE_PRODUCT_MANAGER", "Responsable des produits");
            insertRoleIfNotExist(roleRepository, "ROLE_SHELF_MANAGER", "Chef de rayon");
            insertRoleIfNotExist(roleRepository, "ROLE_DELIVERY_MANAGER", "Responsable de livraison");
            insertRoleIfNotExist(roleRepository, "ROLE_EVENT_MANAGER", "Responsable caritatif");

            // 2. Utilisateurs
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

            // 3. Catégories
            insertCategoryIfNotExist(categoryRepository, "Alimentation", "Produits alimentaires tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Artisanat", "Produits artisanaux tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Cosmétique", "Produits cosmétiques naturels tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Boissons", "Boissons tunisiennes");
            insertCategoryIfNotExist(categoryRepository, "Textile", "Vêtements et tissus tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Epicerie", "Produits d'épicerie fine");
            insertCategoryIfNotExist(categoryRepository, "Cuisine", "Ustensiles de cuisine traditionnelle");
            insertCategoryIfNotExist(categoryRepository, "Décoration", "Articles de décoration");

            // 4. Rayons
            insertShelfIfNotExist(shelfRepository, "Rayon sec", "Normal", 0, 0, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon frais", "Réfrigéré", 3, 0, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon artisanal", "Normal", 0, 3, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon boissons", "Normal", 3, 3, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon textile", "Normal", 0, 6, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon épicerie", "Normal", 3, 6, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon décoration", "Fragile", 6, 0, 3, 3);

            // 5. Produits
            insertProduct("Huile d'olive Tunisienne", "6191234567890", "Huile extra vierge", 25.5, "Alimentation", 100, 10, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Dattes Deglet Nour", "6192345678901", "Dattes premium 500g", 18.0, "Alimentation", 150, 20, "Rayon sec", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Miel de thym", "6197890123456", "Miel naturel", 35.0, "Alimentation", 40, 8, "Rayon sec", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Harissa", "6191234509876", "Pâte de piment 250g", 8.5, "Alimentation", 75, 15, "Rayon sec", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Couscous fin", "6191234512345", "Couscous 1kg", 6.0, "Alimentation", 120, 30, "Rayon sec", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Confiture de figue", "6191234523456", "Confiture artisanale", 12.0, "Alimentation", 50, 10, "Rayon frais", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Poterie de Nabeul", "6193456789012", "Pot décoratif", 45.0, "Artisanat", 30, 5, "Rayon artisanal", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Tapis berbère", "6193456712345", "Tapis laine 2x3m", 320.0, "Artisanat", 15, 3, "Rayon textile", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau en cuivre", "6193456723456", "Plateau gravé", 75.0, "Artisanat", 25, 5, "Rayon artisanal", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Vase en céramique", "6193456734567", "Vase décoratif", 55.0, "Artisanat", 20, 4, "Rayon décoration", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Savon d'Alep", "6194567890123", "Savon naturel", 12.5, "Cosmétique", 80, 15, "Rayon sec", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Huile d'argan", "6194567812345", "Huile cosmétique", 42.0, "Cosmétique", 35, 7, "Rayon sec", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Gommage au rhassoul", "6194567823456", "Masque argileux", 18.0, "Cosmétique", 45, 9, "Rayon sec", 1, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Thé à la menthe", "6195678912345", "Thé vert 250g", 15.0, "Boissons", 60, 12, "Rayon boissons", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Jus de grenade", "6195678923456", "Jus 100% naturel", 8.0, "Boissons", 90, 18, "Rayon frais", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Café moulu", "6195678934567", "Café tunisien", 22.0, "Boissons", 40, 8, "Rayon boissons", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Chéchia rouge", "6196789123456", "Chéchia traditionnelle", 35.0, "Textile", 25, 5, "Rayon textile", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Jebba homme", "6196789234567", "Tenue traditionnelle", 120.0, "Textile", 15, 3, "Rayon textile", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Pâtes d'amande", "6197891234567", "Pâtes artisanales", 28.0, "Epicerie", 30, 6, "Rayon épicerie", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Fruits secs", "6197892345678", "Mélange premium", 32.0, "Epicerie", 45, 9, "Rayon épicerie", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            System.out.println("✅ Initialisation terminée avec succès");
        };
    }

    // Méthodes d'insertion
    private void insertRoleIfNotExist(RoleRepository repo, String name, String description) {
        if (repo.findByName(name).isEmpty()) {
            repo.save(new Role(name, description));
        }
    }

    private void insertUserIfNotExist(UserRepository repo, String username, String email, String pwd, String roleName,
                                      RoleRepository roleRepo, PasswordEncoder encoder) {
        if (repo.findByEmail(email).isEmpty()) {
            Role role = roleRepo.findByName(roleName).orElseThrow();
            repo.save(new User(username, email, encoder.encode(pwd), role));
        }
    }

    private void insertCategoryIfNotExist(CategoryRepository repo, String name, String desc) {
        if (repo.findByName(name).isEmpty()) {
            repo.save(new Category(name, desc));
        }
    }

    private void insertShelfIfNotExist(ShelfRepository repo, String name, String type, int x, int y, int width, int height) {
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

    private void insertProduct(String name, String barcode, String description, double price, String categoryName,
                               int quantity, int minThreshold, String shelfName, int x, int y,
                               CategoryRepository categoryRepo, ShelfRepository shelfRepo,
                               ProductRepository productRepo, StockRepository stockRepo,
                               ProductPositionRepository positionRepo) {

        if (productRepo.findByBarcode(barcode).isEmpty()) {
            Category category = categoryRepo.findByName(categoryName).orElseThrow();
            Shelf shelf = shelfRepo.findByName(shelfName).orElseThrow();

            Product product = new Product();
            product.setName(name);
            product.setBarcode(barcode);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            product = productRepo.save(product);

            Stock stock = new Stock();
            stock.setProduct(product);
            stock.setQuantity(quantity);
            stock.setMinThreshold(minThreshold);
            stockRepo.save(stock);

            ProductPosition position = new ProductPosition();
            position.setProduct(product);
            position.setShelf(shelf);
            position.setX(x);
            position.setY(y);
            position.setWidth(1);
            position.setHeight(1);
            position.setZIndex(0);
            positionRepo.save(position);
        }
    }
}
