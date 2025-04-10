package tn.fst.spring.projet_spring.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.fst.spring.projet_spring.entities.auth.*;
import tn.fst.spring.projet_spring.entities.catalog.*;
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
            // Suppression des données existantes
            stockRepository.deleteAll();
            productRepository.deleteAll();
            shelfRepository.deleteAll();
            categoryRepository.deleteAll();
            userRepository.deleteAll();
            roleRepository.deleteAll();
            permissionRepository.deleteAll();

            // Création et persistance des permissions
            Permission perm1 = permissionRepository.save(new Permission("PRODUCT_READ"));
            Permission perm2 = permissionRepository.save(new Permission("PRODUCT_WRITE"));
            Permission perm3 = permissionRepository.save(new Permission("USER_READ"));
            Permission perm4 = permissionRepository.save(new Permission("USER_WRITE"));
            Permission perm5 = permissionRepository.save(new Permission("ORDER_MANAGE"));

            // Création des rôles avec les permissions persistées
            Role adminRole = new Role("ROLE_ADMIN", new HashSet<>(List.of(perm1, perm2, perm3, perm4, perm5)));
            Role customerRole = new Role("ROLE_CUSTOMER", new HashSet<>(List.of(perm1, perm5)));
            Role productManagerRole = new Role("ROLE_PRODUCT_MANAGER", new HashSet<>(List.of(perm1, perm2)));

            roleRepository.saveAll(List.of(adminRole, customerRole, productManagerRole));

            // Création des utilisateurs avec constructeurs adaptés
            userRepository.saveAll(List.of(
                    new User("admin", "admin@consummi.tn", passwordEncoder.encode("admin123"), adminRole),
                    new User("customer1", "customer1@mail.com", passwordEncoder.encode("customer123"), customerRole),
                    new User("customer2", "customer2@mail.com", passwordEncoder.encode("customer123"), customerRole),
                    new User("pmanager", "pm@consummi.tn", passwordEncoder.encode("pm123"), productManagerRole)
            ));

            // Création des catégories
            Category alimentation = categoryRepository.save(new Category("Alimentation", "Produits alimentaires tunisiens"));
            Category artisanat = categoryRepository.save(new Category("Artisanat", "Produits artisanaux tunisiens"));
            Category cosmetique = categoryRepository.save(new Category("Cosmétique", "Produits cosmétiques naturels tunisiens"));

            // Création des rayons
            Shelf shelf1 = shelfRepository.save(new Shelf("Rayon frais", "Réfrigéré"));
            Shelf shelf2 = shelfRepository.save(new Shelf("Rayon sec", "Normal"));
            Shelf shelf3 = shelfRepository.save(new Shelf("Rayon artisanal", "Normal"));

            // Création des produits
            createProduct("Huile d'olive Tunisienne", "6191234567890", "Huile d'olive extra vierge 1L", 25.5, alimentation, 100, 10, shelf2, productRepository, stockRepository);
            createProduct("Dattes Deglet Nour", "6192345678901", "Dattes premium 500g", 18.0, alimentation, 150, 20, shelf2, productRepository, stockRepository);
            createProduct("Poterie de Nabeul", "6193456789012", "Pot décoratif artisanal", 45.0, artisanat, 30, 5, shelf3, productRepository, stockRepository);
            createProduct("Savon d'Alep", "6194567890123", "Savon naturel 200g", 12.5, cosmetique, 80, 15, shelf2, productRepository, stockRepository);
            createProduct("Fromage de chèvre", "6195678901234", "Fromage frais 250g", 8.0, alimentation, 60, 10, shelf1, productRepository, stockRepository);
            createProduct("Tapis berbère", "6196789012345", "Tapis artisanal 2x3m", 120.0, artisanat, 15, 3, shelf3, productRepository, stockRepository);
            createProduct("Miel de thym", "6197890123456", "Miel naturel 500g", 35.0, alimentation, 40, 8, shelf2, productRepository, stockRepository);
            createProduct("Argile ghassoul", "6198901234567", "Argile naturelle 1kg", 15.0, cosmetique, 50, 10, shelf2, productRepository, stockRepository);

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
        product.getShelves().add(shelf);

        product = productRepository.save(product);

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(quantity);
        stock.setMinThreshold(minThreshold);
        stockRepository.save(stock);
    }
}
