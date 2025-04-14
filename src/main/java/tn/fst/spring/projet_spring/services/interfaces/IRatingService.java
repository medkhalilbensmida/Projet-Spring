package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.forum.RatingDTO;

import java.util.List;

public interface IRatingService {
    RatingDTO createRating(RatingDTO request);
    RatingDTO updateRating(Long id,RatingDTO request);
    void deleteRating(Long id);
    List<RatingDTO> getAllRatings(Long topicId);
}
