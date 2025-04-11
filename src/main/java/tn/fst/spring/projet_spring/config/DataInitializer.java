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
            PermissionRepository permissionRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            StockRepository stockRepository,
            ShelfRepository shelfRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // 1. Permissions
            insertPermissionIfNotExist(permissionRepository, "PRODUCT_READ");
            insertPermissionIfNotExist(permissionRepository, "PRODUCT_WRITE");
            insertPermissionIfNotExist(permissionRepository, "USER_READ");
            insertPermissionIfNotExist(permissionRepository, "USER_WRITE");
            insertPermissionIfNotExist(permissionRepository, "ORDER_MANAGE");

            // 2. Rôles
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                Role adminRole = new Role("ROLE_ADMIN", new HashSet<>(permissionRepository.findAll()));
                roleRepository.save(adminRole);
            }
            if (roleRepository.findByName("ROLE_CUSTOMER").isEmpty()) {
                Role customerRole = new Role("ROLE_CUSTOMER",
                        new HashSet<>(List.of(
                                permissionRepository.findByName("PRODUCT_READ").orElseThrow(),
                                permissionRepository.findByName("ORDER_MANAGE").orElseThrow()
                        )));
                roleRepository.save(customerRole);
            }
            if (roleRepository.findByName("ROLE_PRODUCT_MANAGER").isEmpty()) {
                Role pmRole = new Role("ROLE_PRODUCT_MANAGER",
                        new HashSet<>(List.of(
                                permissionRepository.findByName("PRODUCT_READ").orElseThrow(),
                                permissionRepository.findByName("PRODUCT_WRITE").orElseThrow()
                        )));
                roleRepository.save(pmRole);
            }

            // 3. Utilisateurs par email unique
            insertUserIfNotExist(userRepository, "admin", "admin@consummi.tn", "admin123", "ROLE_ADMIN", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "customer1", "customer1@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "customer2", "customer2@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            insertUserIfNotExist(userRepository, "pmanager", "pm@consummi.tn", "pm123", "ROLE_PRODUCT_MANAGER", roleRepository, passwordEncoder);

            // 4. Catégories
            insertCategoryIfNotExist(categoryRepository, "Alimentation", "Produits alimentaires tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Artisanat", "Produits artisanaux tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Cosmétique", "Produits cosmétiques naturels tunisiens");

            // 5. Rayons
            insertShelfIfNotExist(shelfRepository, "Rayon frais", "Réfrigéré");
            insertShelfIfNotExist(shelfRepository, "Rayon sec", "Normal");
            insertShelfIfNotExist(shelfRepository, "Rayon artisanal", "Normal");

            // 6. Produits (barcodes uniques)
            insertProductIfMissing("Huile d'olive Tunisienne", "6191234567890", "Huile d'olive extra vierge 1L", 25.5, "Alimentation", 100, 10, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Dattes Deglet Nour", "6192345678901", "Dattes premium 500g", 18.0, "Alimentation", 150, 20, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Poterie de Nabeul", "6193456789012", "Pot décoratif artisanal", 45.0, "Artisanat", 30, 5, "Rayon artisanal", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Savon d'Alep", "6194567890123", "Savon naturel 200g", 12.5, "Cosmétique", 80, 15, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Fromage de chèvre", "6195678901234", "Fromage frais 250g", 8.0, "Alimentation", 60, 10, "Rayon frais", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Tapis berbère", "6196789012345", "Tapis artisanal 2x3m", 120.0, "Artisanat", 15, 3, "Rayon artisanal", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Miel de thym", "6197890123456", "Miel naturel 500g", 35.0, "Alimentation", 40, 8, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);
            insertProductIfMissing("Argile ghassoul", "6198901234567", "Argile naturelle 1kg", 15.0, "Cosmétique", 50, 10, "Rayon sec", categoryRepository, shelfRepository, productRepository, stockRepository);

            System.out.println("✅ Initialisation conditionnelle terminée.");
        };
    }

    private void insertPermissionIfNotExist(PermissionRepository repo, String name) {
        if (repo.findByName(name).isEmpty()) {
            repo.save(new Permission(name));
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
            Category category = categoryRepo.findByName(categoryName).orElseThrow();
            Shelf shelf = shelfRepo.findByName(shelfName).orElseThrow();

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
