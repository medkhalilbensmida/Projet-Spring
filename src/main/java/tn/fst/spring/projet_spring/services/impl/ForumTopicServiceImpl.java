package tn.fst.spring.projet_spring.services.impl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.forum.ForumTopicRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.ForumTopicResponseDTO;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.forum.ForumTopic;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.forum.ForumTopicRepository;
import tn.fst.spring.projet_spring.services.interfaces.IForumTopicService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumTopicServiceImpl implements IForumTopicService {

    private final ForumTopicRepository forumTopicRepository;
    private final UserRepository userRepository;

    @Override
    public ForumTopicResponseDTO createTopic(ForumTopicRequestDTO request) {
        /*On doit d'abord verifier si le topic existe deja selon titre */
        if (forumTopicRepository.existsByTitle(request.getTitle())) {
            throw new RuntimeException("Un sujet avec ce titre existe déjà !");
        }

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ForumTopic topic = new ForumTopic();
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setAuthor(author);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setRating(0);

        ForumTopic saved = forumTopicRepository.save(topic);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<ForumTopicResponseDTO> getAllTopics() {
        return forumTopicRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ForumTopicResponseDTO getTopicById(Long id) {
        ForumTopic topic = forumTopicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        return mapToResponseDTO(topic);
    }

    @Override
    public ForumTopicResponseDTO updateTopic(Long id, ForumTopicRequestDTO request) {
        ForumTopic topic = forumTopicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());

        ForumTopic updated = forumTopicRepository.save(topic);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteTopic(Long id) {
        forumTopicRepository.deleteById(id);
    }

    private ForumTopicResponseDTO mapToResponseDTO(ForumTopic topic) {
        return ForumTopicResponseDTO.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .content(topic.getContent())
                .authorUsername(topic.getAuthor().getUsername())
                .rating(topic.getRating())
                .createdAt(topic.getCreatedAt())
                .build();
    }

    @Scheduled(cron = "0 0 0 * * *") //  pour tester on peut utulise "*/20 * * * * *"
    @Transactional
    public void deleteInactiveTopics() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);//de meme ici on change a minusMinutes(1)

        List<ForumTopic> inactiveTopics = forumTopicRepository.findAll().stream()
                .filter(t -> t.getCreatedAt().isBefore(threshold))
                .filter(t -> t.getRating() == 0)
                .filter(t -> t.getComments() == null || t.getComments().isEmpty())
                .toList();

        forumTopicRepository.deleteAll(inactiveTopics);

        System.out.println("Suppression automatique : " + inactiveTopics.size() + " sujets supprimés.");
    }

}