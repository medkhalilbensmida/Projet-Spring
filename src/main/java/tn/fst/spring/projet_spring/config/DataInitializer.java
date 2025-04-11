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
            if (permissionRepository.count() == 0) {
                permissionRepository.saveAll(List.of(
                        new Permission("PRODUCT_READ"),
                        new Permission("PRODUCT_WRITE"),
                        new Permission("USER_READ"),
                        new Permission("USER_WRITE"),
                        new Permission("ORDER_MANAGE")
                ));
            }

            // 2. Rôles
            if (roleRepository.count() == 0) {
                Permission perm1 = permissionRepository.findByName("PRODUCT_READ").orElseThrow();
                Permission perm2 = permissionRepository.findByName("PRODUCT_WRITE").orElseThrow();
                Permission perm3 = permissionRepository.findByName("USER_READ").orElseThrow();
                Permission perm4 = permissionRepository.findByName("USER_WRITE").orElseThrow();
                Permission perm5 = permissionRepository.findByName("ORDER_MANAGE").orElseThrow();

                roleRepository.saveAll(List.of(
                        new Role("ROLE_ADMIN", new HashSet<>(List.of(perm1, perm2, perm3, perm4, perm5))),
                        new Role("ROLE_CUSTOMER", new HashSet<>(List.of(perm1, perm5))),
                        new Role("ROLE_PRODUCT_MANAGER", new HashSet<>(List.of(perm1, perm2)))
                ));
            }

            // 3. Utilisateurs
            if (userRepository.count() == 0) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
                Role customerRole = roleRepository.findByName("ROLE_CUSTOMER").orElseThrow();
                Role productManagerRole = roleRepository.findByName("ROLE_PRODUCT_MANAGER").orElseThrow();

                userRepository.saveAll(List.of(
                        new User("admin", "admin@consummi.tn", passwordEncoder.encode("admin123"), adminRole),
                        new User("customer1", "customer1@mail.com", passwordEncoder.encode("customer123"), customerRole),
                        new User("customer2", "customer2@mail.com", passwordEncoder.encode("customer123"), customerRole),
                        new User("pmanager", "pm@consummi.tn", passwordEncoder.encode("pm123"), productManagerRole)
                ));
            }

            // 4. Catégories
            if (categoryRepository.count() == 0) {
                categoryRepository.saveAll(List.of(
                        new Category("Alimentation", "Produits alimentaires tunisiens"),
                        new Category("Artisanat", "Produits artisanaux tunisiens"),
                        new Category("Cosmétique", "Produits cosmétiques naturels tunisiens")
                ));
            }

            // 5. Rayons
            if (shelfRepository.count() == 0) {
                shelfRepository.saveAll(List.of(
                        new Shelf("Rayon frais", "Réfrigéré"),
                        new Shelf("Rayon sec", "Normal"),
                        new Shelf("Rayon artisanal", "Normal")
                ));
            }

            // 6. Produits
            if (productRepository.count() == 0) {
                Category alimentation = categoryRepository.findByName("Alimentation").orElseThrow();
                Category artisanat = categoryRepository.findByName("Artisanat").orElseThrow();
                Category cosmetique = categoryRepository.findByName("Cosmétique").orElseThrow();

                Shelf shelf1 = shelfRepository.findByName("Rayon frais").orElseThrow();
                Shelf shelf2 = shelfRepository.findByName("Rayon sec").orElseThrow();
                Shelf shelf3 = shelfRepository.findByName("Rayon artisanal").orElseThrow();

                createProduct("Huile d'olive Tunisienne", "6191234567890", "Huile d'olive extra vierge 1L", 25.5, alimentation, 100, 10, shelf2, productRepository, stockRepository);
                createProduct("Dattes Deglet Nour", "6192345678901", "Dattes premium 500g", 18.0, alimentation, 150, 20, shelf2, productRepository, stockRepository);
                createProduct("Poterie de Nabeul", "6193456789012", "Pot décoratif artisanal", 45.0, artisanat, 30, 5, shelf3, productRepository, stockRepository);
                createProduct("Savon d'Alep", "6194567890123", "Savon naturel 200g", 12.5, cosmetique, 80, 15, shelf2, productRepository, stockRepository);
                createProduct("Fromage de chèvre", "6195678901234", "Fromage frais 250g", 8.0, alimentation, 60, 10, shelf1, productRepository, stockRepository);
                createProduct("Tapis berbère", "6196789012345", "Tapis artisanal 2x3m", 120.0, artisanat, 15, 3, shelf3, productRepository, stockRepository);
                createProduct("Miel de thym", "6197890123456", "Miel naturel 500g", 35.0, alimentation, 40, 8, shelf2, productRepository, stockRepository);
                createProduct("Argile ghassoul", "6198901234567", "Argile naturelle 1kg", 15.0, cosmetique, 50, 10, shelf2, productRepository, stockRepository);
            }

            System.out.println("✅ Initialisation terminée avec succès.");
        };
    }

    private void createProduct(String name, String barcode, String description,
                               double price, Category category, int quantity,
                               int minThreshold, Shelf shelf,
                               ProductRepository productRepository,
                               StockRepository stockRepository) {

        Product product = new Product();
        product.setName(name);
        product.setBarcode(barcode);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.getShelves().add(shelf); // 🔥 suffisant, pas besoin de shelf.getProducts().add(product)

        product = productRepository.save(product);

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(quantity);
        stock.setMinThreshold(minThreshold);

        stockRepository.save(stock);
    }
}
