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
import java.time.temporal.ChronoUnit;

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
            // 1. Initialisation des rôles
            insertRoleIfNotExist(roleRepository, "ROLE_ADMIN", "Administrateur principal");
            insertRoleIfNotExist(roleRepository, "ROLE_CUSTOMER", "Client consommateur");
            insertRoleIfNotExist(roleRepository, "ROLE_PRODUCT_MANAGER", "Responsable des produits");
            insertRoleIfNotExist(roleRepository, "ROLE_SHELF_MANAGER", "Chef de rayon");
            insertRoleIfNotExist(roleRepository, "ROLE_DELIVERY_MANAGER", "Responsable de livraison");
            insertRoleIfNotExist(roleRepository, "ROLE_EVENT_MANAGER", "Responsable caritatif");

            // 2. Initialisation des utilisateurs
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

            // Ajout de plus d'utilisateurs pour les statistiques
            for (int i = 1; i <= 50; i++) {
                insertUserIfNotExist(userRepository,
                        "Client " + i,
                        "client" + i + "@mail.com",
                        "customer123",
                        "ROLE_CUSTOMER",
                        roleRepository,
                        passwordEncoder);
            }

            // 3. Initialisation des catégories
            insertCategoryIfNotExist(categoryRepository, "Alimentation", "Produits alimentaires tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Artisanat", "Produits artisanaux tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Cosmétique", "Produits cosmétiques naturels tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Boissons", "Boissons tunisiennes");
            insertCategoryIfNotExist(categoryRepository, "Textile", "Vêtements et tissus tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Epicerie", "Produits d'épicerie fine");
            insertCategoryIfNotExist(categoryRepository, "Cuisine", "Ustensiles de cuisine traditionnelle");
            insertCategoryIfNotExist(categoryRepository, "Décoration", "Articles de décoration");

            // 4. Initialisation des rayons
            insertShelfIfNotExist(shelfRepository, "Rayon sec", "Normal", 0, 0, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon frais", "Réfrigéré", 3, 0, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon artisanal", "Normal", 0, 3, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon boissons", "Normal", 3, 3, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon textile", "Normal", 0, 6, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon épicerie", "Normal", 3, 6, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon décoration", "Fragile", 6, 0, 3, 3);

            // 5. Produits initiaux (fortement enrichi)
            // Alimentation
            insertProduct("Huile d'olive Tunisienne", "6191234567890", "Huile extra vierge", 25.5, "Alimentation", 100, 10, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Dattes Deglet Nour", "6191234567891", "Dattes premium 500g", 18.0, "Alimentation", 150, 20, "Rayon sec", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Miel de thym", "6191234567892", "Miel naturel", 35.0, "Alimentation", 40, 8, "Rayon sec", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Harissa", "6191234567893", "Pâte de piment 250g", 8.5, "Alimentation", 75, 15, "Rayon sec", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Couscous fin", "6191234567894", "Couscous 1kg", 6.0, "Alimentation", 120, 30, "Rayon sec", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Confiture de figue", "6191234567895", "Confiture artisanale", 12.0, "Alimentation", 50, 10, "Rayon frais", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Biscuits traditionnels", "6191234567896", "Biscuits aux amandes", 15.0, "Alimentation", 80, 15, "Rayon sec", 1, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Sauce tomate", "6191234567897", "Sauce naturelle", 7.5, "Alimentation", 90, 20, "Rayon sec", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Pâtes artisanales", "6191234567898", "Pâtes fraîches", 9.0, "Alimentation", 60, 12, "Rayon frais", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Olives vertes", "6191234567899", "Olives marinées", 11.0, "Alimentation", 70, 14, "Rayon sec", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // Artisanat
            insertProduct("Poterie de Nabeul", "6192345678901", "Pot décoratif", 45.0, "Artisanat", 30, 5, "Rayon artisanal", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Tapis berbère", "6192345678902", "Tapis laine 2x3m", 320.0, "Artisanat", 15, 3, "Rayon textile", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau en cuivre", "6192345678903", "Plateau gravé", 75.0, "Artisanat", 25, 5, "Rayon artisanal", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Vase en céramique", "6192345678904", "Vase décoratif", 55.0, "Artisanat", 20, 4, "Rayon décoration", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau en bois", "6192345678905", "Bois d'olivier", 65.0, "Artisanat", 18, 3, "Rayon artisanal", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Théière traditionnelle", "6192345678906", "Théière en argent", 120.0, "Artisanat", 12, 2, "Rayon artisanal", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Moucharabieh", "6192345678907", "Décoration murale", 95.0, "Artisanat", 10, 2, "Rayon décoration", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Bracelet en argent", "6192345678908", "Bijou traditionnel", 45.0, "Artisanat", 25, 5, "Rayon artisanal", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau mosaïque", "6192345678909", "Mosaïque colorée", 85.0, "Artisanat", 15, 3, "Rayon artisanal", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Lampe en fer forgé", "6192345678910", "Lampe artisanale", 65.0, "Artisanat", 20, 4, "Rayon décoration", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // Cosmétique
            insertProduct("Savon d'Alep", "6193456789012", "Savon naturel", 12.5, "Cosmétique", 80, 15, "Rayon sec", 2, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Huile d'argan", "6193456789013", "Huile cosmétique", 42.0, "Cosmétique", 35, 7, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Gommage au rhassoul", "6193456789014", "Masque argileux", 18.0, "Cosmétique", 45, 9, "Rayon sec", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Crème hydratante", "6193456789015", "Crème à l'huile d'olive", 25.0, "Cosmétique", 30, 6, "Rayon sec", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Shampoing naturel", "6193456789016", "Shampoing aux plantes", 20.0, "Cosmétique", 40, 8, "Rayon sec", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Eau de rose", "6193456789017", "Eau florale", 15.0, "Cosmétique", 50, 10, "Rayon sec", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Masque visage", "6193456789018", "Masque argile", 22.0, "Cosmétique", 35, 7, "Rayon sec", 1, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Gel douche", "6193456789019", "Gel à la lavande", 18.0, "Cosmétique", 45, 9, "Rayon sec", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Baume à lèvres", "6193456789020", "Baume naturel", 10.0, "Cosmétique", 60, 12, "Rayon sec", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Parfum naturel", "6193456789021", "Parfum oriental", 50.0, "Cosmétique", 25, 5, "Rayon sec", 2, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // Boissons
            insertProduct("Thé à la menthe", "6194567890123", "Thé vert 250g", 15.0, "Boissons", 60, 12, "Rayon boissons", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Jus de grenade", "6194567890124", "Jus 100% naturel", 8.0, "Boissons", 90, 18, "Rayon frais", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Café moulu", "6194567890125", "Café tunisien", 22.0, "Boissons", 40, 8, "Rayon boissons", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Sirop de grenadine", "6194567890126", "Sirop artisanal", 12.0, "Boissons", 50, 10, "Rayon boissons", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Eau minérale", "6194567890127", "Eau de source", 3.0, "Boissons", 120, 30, "Rayon boissons", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Jus d'orange", "6194567890128", "Jus pressé", 7.0, "Boissons", 80, 16, "Rayon frais", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Infusion aux plantes", "6194567890129", "Mélange de plantes", 10.0, "Boissons", 45, 9, "Rayon boissons", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Limonade artisanale", "6194567890130", "Limonade naturelle", 6.0, "Boissons", 70, 14, "Rayon boissons", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Vin de figue", "6194567890131", "Vin artisanal", 35.0, "Boissons", 20, 4, "Rayon boissons", 2, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Thé aux amandes", "6194567890132", "Thé parfumé", 18.0, "Boissons", 30, 6, "Rayon boissons", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // Textile
            insertProduct("Chéchia rouge", "6195678912345", "Chéchia traditionnelle", 35.0, "Textile", 25, 5, "Rayon textile", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Jebba homme", "6195678912346", "Tenue traditionnelle", 120.0, "Textile", 15, 3, "Rayon textile", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Foulard en soie", "6195678912347", "Foulard coloré", 45.0, "Textile", 20, 4, "Rayon textile", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Tunique femme", "6195678912348", "Tunique brodée", 65.0, "Textile", 18, 3, "Rayon textile", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Ceinture en cuir", "6195678912349", "Ceinture artisanale", 40.0, "Textile", 22, 4, "Rayon textile", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Chaussons brodés", "6195678912350", "Chaussons traditionnels", 30.0, "Textile", 25, 5, "Rayon textile", 1, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Sac en tissu", "6195678912351", "Sac artisanal", 25.0, "Textile", 30, 6, "Rayon textile", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Gandoura homme", "6195678912352", "Robe traditionnelle", 85.0, "Textile", 15, 3, "Rayon textile", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Châle en laine", "6195678912353", "Châle chaud", 55.0, "Textile", 20, 4, "Rayon textile", 2, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Couvre-chef", "6195678912354", "Accessoire traditionnel", 28.0, "Textile", 25, 5, "Rayon textile", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // Epicerie
            insertProduct("Pâtes d'amande", "6196789123456", "Pâtes artisanales", 28.0, "Epicerie", 30, 6, "Rayon épicerie", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Fruits secs", "6196789123457", "Mélange premium", 32.0, "Epicerie", 45, 9, "Rayon épicerie", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Confiture d'orange", "6196789123458", "Confiture maison", 15.0, "Epicerie", 35, 7, "Rayon épicerie", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Miel de cactus", "6196789123459", "Miel rare", 45.0, "Epicerie", 20, 4, "Rayon épicerie", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Amandes grillées", "6196789123460", "Amandes naturelles", 25.0, "Epicerie", 40, 8, "Rayon épicerie", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Pistaches", "6196789123461", "Pistaches de qualité", 38.0, "Epicerie", 30, 6, "Rayon épicerie", 1, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Dattes farcies", "6196789123462", "Dattes aux amandes", 22.0, "Epicerie", 35, 7, "Rayon épicerie", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Figues séchées", "6196789123463", "Figues biologiques", 28.0, "Epicerie", 25, 5, "Rayon épicerie", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Mélange oriental", "6196789123464", "Mélange de fruits secs", 35.0, "Epicerie", 30, 6, "Rayon épicerie", 2, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Pâte de sésame", "6196789123465", "Tahini artisanal", 18.0, "Epicerie", 40, 8, "Rayon épicerie", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // Cuisine
            insertProduct("Tajine en terre", "6197890123456", "Tajine traditionnel", 55.0, "Cuisine", 20, 4, "Rayon artisanal", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Moulin à épices", "6197890123457", "Moulin en bois", 32.0, "Cuisine", 25, 5, "Rayon artisanal", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau service", "6197890123458", "Plateau en laiton", 45.0, "Cuisine", 18, 3, "Rayon artisanal", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Couscoussier", "6197890123459", "Couscoussier inox", 65.0, "Cuisine", 15, 3, "Rayon artisanal", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Théière en cuivre", "6197890123460", "Théière traditionnelle", 75.0, "Cuisine", 12, 2, "Rayon artisanal", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Verres à thé", "6197890123461", "Set de 6 verres", 28.0, "Cuisine", 25, 5, "Rayon artisanal", 1, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau à café", "6197890123462", "Plateau en argent", 95.0, "Cuisine", 10, 2, "Rayon artisanal", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Mortier en marbre", "6197890123463", "Mortier et pilon", 42.0, "Cuisine", 20, 4, "Rayon artisanal", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Couteau de cuisine", "6197890123464", "Couteau artisanal", 35.0, "Cuisine", 22, 4, "Rayon artisanal", 2, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Set d'épices", "6197890123465", "Assortiment d'épices", 28.0, "Cuisine", 30, 6, "Rayon épicerie", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // Décoration
            insertProduct("Miroir mosaïque", "6198901234567", "Miroir décoratif", 85.0, "Décoration", 15, 3, "Rayon décoration", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau mural", "6198901234568", "Décoration murale", 65.0, "Décoration", 20, 4, "Rayon décoration", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Bougeoir en cuivre", "6198901234569", "Bougeoir artisanal", 45.0, "Décoration", 25, 5, "Rayon décoration", 0, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Ventilateur mural", "6198901234570", "Ventilateur décoratif", 75.0, "Décoration", 18, 3, "Rayon décoration", 1, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Table basse", "6198901234571", "Table en bois", 120.0, "Décoration", 10, 2, "Rayon décoration", 1, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Pouf en cuir", "6198901234572", "Pouf traditionnel", 85.0, "Décoration", 15, 3, "Rayon décoration", 1, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Rideaux brodés", "6198901234573", "Rideaux artisanaux", 65.0, "Décoration", 20, 4, "Rayon décoration", 2, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Tableau tissé", "6198901234574", "Tableau traditionnel", 95.0, "Décoration", 12, 2, "Rayon décoration", 2, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Lanterne en fer", "6198901234575", "Lanterne décorative", 55.0, "Décoration", 22, 4, "Rayon décoration", 2, 2, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Plateau à bijoux", "6198901234576", "Plateau en céramique", 45.0, "Décoration", 25, 5, "Rayon décoration", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);

            // 6. Insertion des livreurs
            Livreur livreur1 = insertLivreurIfNotExist(livreurRepository, "Ahmed Ben Ali", true);
            Livreur livreur2 = insertLivreurIfNotExist(livreurRepository, "Fatma Gharbi", true);
            Livreur livreur3 = insertLivreurIfNotExist(livreurRepository, "Mohamed Trabelsi", true);
            Livreur livreur4 = insertLivreurIfNotExist(livreurRepository, "Samira Khemiri", false);

            // 7. Création de commandes et livraisons
            User customer1 = userRepository.findByEmail("fadi.abaidi@mail.com").orElseThrow();
            User customer2 = userRepository.findByEmail("yasmine.benslimane@mail.com").orElseThrow();
            User customer3 = userRepository.findByEmail("hedi.aissi@mail.com").orElseThrow();

            Product huileOlive = productRepository.findByBarcode("6191234567890").orElseThrow();
            Product dattes = productRepository.findByBarcode("6191234567891").orElseThrow();
            Product miel = productRepository.findByBarcode("6191234567892").orElseThrow();
            Product poterie = productRepository.findByBarcode("6192345678901").orElseThrow();
            Product tapis = productRepository.findByBarcode("6192345678902").orElseThrow();
            Product savon = productRepository.findByBarcode("6193456789012").orElseThrow();
            Product the = productRepository.findByBarcode("6194567890123").orElseThrow();
            Product chechia = productRepository.findByBarcode("6195678912345").orElseThrow();
            Product patesAmande = productRepository.findByBarcode("6196789123456").orElseThrow();
            Product tajine = productRepository.findByBarcode("6197890123456").orElseThrow();
            Product miroir = productRepository.findByBarcode("6198901234567").orElseThrow();

            // Commandes pour customer1
            Order order1 = createOrder(orderRepository, orderItemRepository, customer1, huileOlive, 2, "ORD001");
            createDeliveryRequest(deliveryRequestRepository, order1, livreur1, DeliveryStatus.DELIVERED, 5.0);

            Order order2 = createOrder(orderRepository, orderItemRepository, customer1, dattes, 3, "ORD002");
            createDeliveryRequest(deliveryRequestRepository, order2, livreur1, DeliveryStatus.DELIVERED, 5.0);

            Order order3 = createOrder(orderRepository, orderItemRepository, customer1, miel, 1, "ORD003");
            createDeliveryRequest(deliveryRequestRepository, order3, livreur2, DeliveryStatus.IN_TRANSIT, 6.0);

            Order order4 = createOrder(orderRepository, orderItemRepository, customer1, poterie, 1, "ORD004");
            createDeliveryRequest(deliveryRequestRepository, order4, livreur1, DeliveryStatus.ASSIGNED, 5.0);

            Order order5 = createOrder(orderRepository, orderItemRepository, customer1, tapis, 1, "ORD005");
            createDeliveryRequest(deliveryRequestRepository, order5, livreur2, DeliveryStatus.DELIVERED, 10.0);

            // Commandes pour customer2
            Order order6 = createOrder(orderRepository, orderItemRepository, customer2, savon, 5, "ORD006");
            createDeliveryRequest(deliveryRequestRepository, order6, livreur3, DeliveryStatus.DELIVERED, 5.0);

            Order order7 = createOrder(orderRepository, orderItemRepository, customer2, the, 2, "ORD007");
            createDeliveryRequest(deliveryRequestRepository, order7, livreur3, DeliveryStatus.DELIVERED, 5.0);

            Order order8 = createOrder(orderRepository, orderItemRepository, customer2, chechia, 1, "ORD008");
            createDeliveryRequest(deliveryRequestRepository, order8, livreur4, DeliveryStatus.PENDING, 5.0);

            // Commandes pour customer3
            Order order9 = createOrder(orderRepository, orderItemRepository, customer3, patesAmande, 2, "ORD009");
            createDeliveryRequest(deliveryRequestRepository, order9, livreur1, DeliveryStatus.DELIVERED, 5.0);

            Order order10 = createOrder(orderRepository, orderItemRepository, customer3, tajine, 1, "ORD010");
            createDeliveryRequest(deliveryRequestRepository, order10, livreur2, DeliveryStatus.IN_TRANSIT, 8.0);

            Order order11 = createOrder(orderRepository, orderItemRepository, customer3, miroir, 1, "ORD011");
            createDeliveryRequest(deliveryRequestRepository, order11, livreur3, DeliveryStatus.ASSIGNED, 8.0);

            // Ajout de commandes historiques pour les statistiques
            for (int i = 12; i <= 50; i++) {
                Product randomProduct = getRandomProduct(productRepository);
                int quantity = new Random().nextInt(5) + 1;
                Order order = createOrder(orderRepository, orderItemRepository,
                        getRandomCustomer(userRepository),
                        randomProduct,
                        quantity,
                        "ORD" + String.format("%03d", i));

                createDeliveryRequest(deliveryRequestRepository, order,
                        getRandomLivreur(livreurRepository),
                        getRandomDeliveryStatus(),
                        new Random().nextInt(10) + 5);
            }

            System.out.println("✅ Initialisation complète réussie avec des données enrichies pour les statistiques.");
        };
    }

    // Méthodes utilitaires
    private Product getRandomProduct(ProductRepository repo) {
        List<Product> products = repo.findAll();
        return products.get(new Random().nextInt(products.size()));
    }

    private User getRandomCustomer(UserRepository repo) {
        List<User> customers = repo.findByRoleName("ROLE_CUSTOMER");
        return customers.get(new Random().nextInt(customers.size()));
    }

    private Livreur getRandomLivreur(LivreurRepository repo) {
        List<Livreur> livreurs = repo.findAll();
        return livreurs.get(new Random().nextInt(livreurs.size()));
    }

    private DeliveryStatus getRandomDeliveryStatus() {
        DeliveryStatus[] statuses = DeliveryStatus.values();
        return statuses[new Random().nextInt(statuses.length)];
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

    private void insertProduct(String name, String barcode, String description, double price, String categoryName,
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

    private Livreur insertLivreurIfNotExist(LivreurRepository repo, String nom, boolean disponible) {
        Optional<Livreur> existing = repo.findAll().stream().filter(l -> l.getNom().equals(nom)).findFirst();
        return existing.orElseGet(() -> {
            Livreur livreur = new Livreur();
            livreur.setNom(nom);
            livreur.setDisponible(disponible);
            Livreur savedLivreur = repo.save(livreur);
            System.out.println("Livreur " + nom + " inséré avec succès.");
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
            return null;
        }
    }

    private void createDeliveryRequest(DeliveryRequestRepository deliveryRepo, Order order, Livreur livreur, DeliveryStatus status, double fees) {
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
            deliveryRepo.save(request);
            System.out.println("Demande de livraison pour la commande " + order.getOrderNumber() + " créée avec succès.");
        });
    }

}
