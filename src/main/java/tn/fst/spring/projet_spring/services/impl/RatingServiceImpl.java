package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.forum.RatingDTO;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.forum.ForumTopic;
import tn.fst.spring.projet_spring.model.forum.Rating;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.forum.ForumTopicRepository;
import tn.fst.spring.projet_spring.repositories.forum.RatingRepository;
import tn.fst.spring.projet_spring.services.interfaces.IRatingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements IRatingService {
    private final UserRepository userRepository;
    private final ForumTopicRepository topicRepository;
    private final RatingRepository ratingRepository;

    @Override
    public RatingDTO createRating(RatingDTO request) {
        User user =userRepository.findById(request.getUserId()).orElseThrow(()->new RuntimeException("user not found"));
        ForumTopic topic=topicRepository.findById(request.getTopicId()).orElseThrow(()-> new RuntimeException("topic not found"));
        Rating rating=new Rating();
        rating.setUser(user);
        rating.setTopic(topic);
        rating.setRating(request.getRating());
        rating.setCreatedAt(LocalDateTime.now());
        Rating saved=ratingRepository.save(rating);
        updateTopicAverageRating(topic);
        return mapToResponseDTO(saved);

    }

    @Override
    public List<RatingDTO> getAllRatings(Long topicId) {
        return ratingRepository.findAllByTopicId(topicId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    public RatingDTO updateRating(Long id,RatingDTO request){
        Rating rating=ratingRepository.findById(id).orElseThrow(()->new RuntimeException("rating not found"));
        ForumTopic topic=topicRepository.findById(request.getTopicId()).orElseThrow(()->new RuntimeException("topic not found"));
        rating.setRating(request.getRating());
        Rating saved=ratingRepository.save(rating);
        updateTopicAverageRating(topic);
        return mapToResponseDTO(saved);
    }
    @Override
    public void deleteRating(Long id){
        Rating rating=ratingRepository.findById(id).orElseThrow(()->new RuntimeException("rating not found"));
        ratingRepository.delete(rating);
        updateTopicAverageRating(rating.getTopic());
    }
    //recalculer moyenne de rating de topic apres creation , suppression ou mise a jour d'un single rating
    public void updateTopicAverageRating(ForumTopic topic) {
        List<Rating> topicRatings = ratingRepository.findByTopicId(topic.getId());
        double averageRating = topicRatings.stream()
                .mapToDouble(Rating::getRating)
                .average()
                .orElse(0.0);

        //on prend juste 2 nombres apres virgule
        double roundedAverage = Math.round(averageRating * 100.0) / 100.0;
        topic.setRating(roundedAverage);
        topicRepository.save(topic);
    }

    private RatingDTO mapToResponseDTO(Rating rating) {
        return RatingDTO.builder()
                .userId(rating.getUser().getId())
                .topicId(rating.getTopic().getId())
                .rating(rating.getRating())
                .build();
    }

}

