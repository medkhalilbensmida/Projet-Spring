package tn.fst.spring.projet_spring.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.fst.spring.projet_spring.model.auth.*;
import tn.fst.spring.projet_spring.model.catalog.*;
import tn.fst.spring.projet_spring.model.donation.CharityEvent;
import tn.fst.spring.projet_spring.model.donation.Donation;
import tn.fst.spring.projet_spring.model.donation.Fundraiser;
import tn.fst.spring.projet_spring.model.forum.Comment;
import tn.fst.spring.projet_spring.model.forum.ForumTopic;
import tn.fst.spring.projet_spring.model.forum.Rating;
import tn.fst.spring.projet_spring.model.logistics.Livreur;
import tn.fst.spring.projet_spring.model.logistics.DeliveryRequest;
import tn.fst.spring.projet_spring.model.logistics.DeliveryStatus;
import tn.fst.spring.projet_spring.model.order.*;
import tn.fst.spring.projet_spring.model.payment.Payment;
import tn.fst.spring.projet_spring.model.payment.PaymentMethod;
import tn.fst.spring.projet_spring.repositories.auth.*;
import tn.fst.spring.projet_spring.repositories.catalog.*;
import tn.fst.spring.projet_spring.repositories.donation.CharityEventRepository;
import tn.fst.spring.projet_spring.repositories.donation.DonationRepository;
import tn.fst.spring.projet_spring.repositories.donation.FundraiserRepository;
import tn.fst.spring.projet_spring.repositories.forum.CommentRepository;
import tn.fst.spring.projet_spring.repositories.forum.ForumTopicRepository;
import tn.fst.spring.projet_spring.repositories.forum.RatingRepository;
import tn.fst.spring.projet_spring.repositories.logistics.LivreurRepository;
import tn.fst.spring.projet_spring.repositories.logistics.DeliveryRequestRepository;
import tn.fst.spring.projet_spring.repositories.logistics.ComplaintRepository;
import tn.fst.spring.projet_spring.repositories.logistics.ResolutionRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderItemRepository;
import tn.fst.spring.projet_spring.repositories.order.OrderRepository;
import tn.fst.spring.projet_spring.repositories.payment.PaymentRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
            DeliveryRequestRepository deliveryRequestRepository,
            ComplaintRepository complaintRepository,
            ResolutionRepository resolutionRepository,
            PaymentRepository paymentRepository,
            ForumTopicRepository forumTopicRepository,
            RatingRepository ratingRepository,
            CommentRepository commentRepository,
            FundraiserRepository fundraiserRepository,
            CharityEventRepository charityEventRepository,
            DonationRepository donationRepository
    ) {
        return args -> {
            // 1. Roles
            insertRoleIfNotExist(roleRepository, "ROLE_ADMIN", "Administrateur principal");
            insertRoleIfNotExist(roleRepository, "ROLE_CUSTOMER", "Client consommateur");
            insertRoleIfNotExist(roleRepository, "ROLE_PRODUCT_MANAGER", "Responsable des produits");
            insertRoleIfNotExist(roleRepository, "ROLE_SHELF_MANAGER", "Chef de rayon");
            insertRoleIfNotExist(roleRepository, "ROLE_DELIVERY_MANAGER", "Responsable de livraison");
            insertRoleIfNotExist(roleRepository, "ROLE_EVENT_MANAGER", "Responsable caritatif");

            // 2. Users
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

            for (int i = 1; i <= 50; i++) {
                insertUserIfNotExist(userRepository, "Client " + i, "client" + i + "@mail.com", "customer123", "ROLE_CUSTOMER", roleRepository, passwordEncoder);
            }

            // 3. Categories
            insertCategoryIfNotExist(categoryRepository, "Alimentation", "Produits alimentaires tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Artisanat", "Produits artisanaux tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Cosmétique", "Produits cosmétiques naturels tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Boissons", "Boissons tunisiennes");
            insertCategoryIfNotExist(categoryRepository, "Textile", "Vêtements et tissus tunisiens");
            insertCategoryIfNotExist(categoryRepository, "Epicerie", "Produits d'épicerie fine");
            insertCategoryIfNotExist(categoryRepository, "Cuisine", "Ustensiles de cuisine traditionnelle");
            insertCategoryIfNotExist(categoryRepository, "Décoration", "Articles de décoration");

            // 4. Shelves
            insertShelfIfNotExist(shelfRepository, "Rayon sec", "Normal", 0, 0, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon frais", "Réfrigéré", 3, 0, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon artisanal", "Normal", 0, 3, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon boissons", "Normal", 3, 3, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon textile", "Normal", 0, 6, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon épicerie", "Normal", 3, 6, 3, 3);
            insertShelfIfNotExist(shelfRepository, "Rayon décoration", "Fragile", 6, 0, 3, 3);

            // 5. Products (examples only, many more in real code)
            insertProduct("Huile d'olive Tunisienne", "6191234567890", "Huile extra vierge", 25.5, "Alimentation", 100, 10, "Rayon sec", 0, 0, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            insertProduct("Dattes Deglet Nour", "6191234567891", "Dattes premium 500g", 18.0, "Alimentation", 150, 20, "Rayon sec", 0, 1, categoryRepository, shelfRepository, productRepository, stockRepository, productPositionRepository);
            // … (rest of your insertProduct calls)

            // 6. Forum topics, ratings & comments
            insertForumTopic(
                "Les meilleurs produits artisanaux tunisiens",
                "Quels sont selon vous les produits artisanaux tunisiens les plus représentatifs de notre culture ?",
                1L, 0, forumTopicRepository, userRepository
            );
            insertForumTopic(
                "Où acheter de l'huile d'olive tunisienne de qualité ?",
                "Je cherche des adresses fiables pour acheter de l'huile d'olive tunisienne premium, des suggestions ?",
                2L, 3.5, forumTopicRepository, userRepository
            );
            // … (other topics)
            insertRating(3.5, 1L, 2L, ratingRepository, userRepository, forumTopicRepository);
            insertRating(4.0, 3L, 3L, ratingRepository, userRepository, forumTopicRepository);
            insertRating(4.8, 2L, 4L, ratingRepository, userRepository, forumTopicRepository);
            insertCommentWithReactions(
                "L'huile d'olive de Tunisie est la meilleure au monde selon mon expérience !",
                2L, 1L, Set.of(2L,3L), Set.of(4L),
                commentRepository, userRepository, forumTopicRepository
            );
            insertCommentWithReactions(
                "Pour les dattes, je recommande la variété Deglet Nour de Tozeur",
                3L, 2L, Set.of(1L,3L,4L), Set.of(),
                commentRepository, userRepository, forumTopicRepository
            );

            // 7. Donations and events
            initializeDonations(fundraiserRepository, charityEventRepository, donationRepository, userRepository, productRepository);

            // 8. Livreurs
            Livreur livreur1 = insertLivreurIfNotExist(livreurRepository, "Ahmed Ben Ali", false,
                36.83 + ThreadLocalRandom.current().nextDouble(0.01, 0.02),
                10.15 + ThreadLocalRandom.current().nextDouble(0.01, 0.02)
            );
            Livreur livreur2 = insertLivreurIfNotExist(livreurRepository, "Fatma Gharbi", false,
                36.84 + ThreadLocalRandom.current().nextDouble(0.005, 0.015),
                10.16 + ThreadLocalRandom.current().nextDouble(0.005, 0.015)
            );
            insertLivreurIfNotExist(livreurRepository, "Samir Khelifi", true, 36.83 + ThreadLocalRandom.current().nextDouble(0.005, 0.01), 10.17 + ThreadLocalRandom.current().nextDouble(0.005, 0.01));
            insertLivreurIfNotExist(livreurRepository, "Hela Maatoug", true, 36.833833, 10.148004);
            insertLivreurIfNotExist(livreurRepository, "Karim Jouini", true, 36.835556, 10.142389);

            // 9. Orders & delivery requests
            User customerFadi    = userRepository.findByEmail("fadi.abaidi@mail.com").orElseThrow();
            User customerYasmine= userRepository.findByEmail("yasmine.benslimane@mail.com").orElseThrow();
            User customerHedi   = userRepository.findByEmail("hedi.aissi@mail.com").orElseThrow();
            Product huileOlive  = productRepository.findByBarcode("6191234567890").orElseThrow();
            Product dattes      = productRepository.findByBarcode("6191234567891").orElseThrow();

            Order o1 = createOrder(orderRepository, orderItemRepository, customerFadi, huileOlive, 2, "ORD001");
            createDeliveryRequest(deliveryRequestRepository, o1, livreur1, DeliveryStatus.DELIVERED, 5.0, 36.8008, 10.1815);

            Order o2 = createOrder(orderRepository, orderItemRepository, customerYasmine, huileOlive, 1, "ORD002");
            createDeliveryRequest(deliveryRequestRepository, o2, livreur1, DeliveryStatus.DELIVERED, 5.0, 36.8454, 10.1941);

            Order o3 = createOrder(orderRepository, orderItemRepository, customerHedi, huileOlive, 3, "ORD003");
            createDeliveryRequest(deliveryRequestRepository, o3, livreur2, DeliveryStatus.IN_TRANSIT, 6.0, 36.7948, 10.1007);

            Order o4 = createOrder(orderRepository, orderItemRepository, customerFadi, huileOlive, 1, "ORD004");
            createDeliveryRequest(deliveryRequestRepository, o4, livreur1, DeliveryStatus.ASSIGNED, 5.0, 36.8665, 10.1647);

            Order o5 = createOrder(orderRepository, orderItemRepository, customerYasmine, huileOlive, 2, "ORD005");
            createDeliveryRequest(deliveryRequestRepository, o5, livreur2, DeliveryStatus.DELIVERED, 6.0, 36.8028, 10.1797);

            Order o6 = createOrder(orderRepository, orderItemRepository, customerFadi, dattes, 5, "ORD006");
            createUnassignedDeliveryRequest(deliveryRequestRepository, o6, 7.5, 36.8550, 10.1850);

            // 10. Complaints
            createComplaintIfNotExist(complaintRepository, customerFadi, o1, "L'huile d'olive a fui pendant la livraison.");
            createComplaintIfNotExist(complaintRepository, customerYasmine, o6, "Les dattes reçues ne semblent pas fraîches.");
            createComplaintIfNotExist(complaintRepository, customerHedi, o5, "Commande marquée livrée mais jamais reçue.");

            // 11. Initial payments
            List<Order> seededOrders = Arrays.asList(o1, o2, o3, o4, o5, o6);
            for (Order o : seededOrders) {
                if (!paymentRepository.findByOrderId(o.getId()).isPresent()) {
                    Payment p = new Payment();
                    p.setTransactionId("INIT-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
                    p.setPaymentMethod(PaymentMethod.CREDIT_CARD);
                    p.setAmount(o.getTotalAmount());
                    p.setPaymentDate(LocalDateTime.now());
                    p.setOrder(o);
                    p.setSuccessful(true);
                    paymentRepository.save(p);
                    System.out.println("Initial payment created for order " + o.getOrderNumber());
                }
            }

            System.out.println("✅ Initialisation complète réussie avec données de forum, dons, plaintes et paiements.");
        };
    }

    // … (all your helper methods: insertRoleIfNotExist, insertUserIfNotExist, insertCategoryIfNotExist, insertShelfIfNotExist,
    //     insertProduct, insertForumTopic, insertRating, insertCommentWithReactions, initializeDonations,
    //     insertLivreurIfNotExist, createOrder, createDeliveryRequest, createUnassignedDeliveryRequest, createComplaintIfNotExist)

}
