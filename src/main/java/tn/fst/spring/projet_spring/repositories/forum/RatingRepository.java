package tn.fst.spring.projet_spring.repositories.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.fst.spring.projet_spring.model.forum.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {
    List<Rating> findByTopicId(Long id);

    List<Rating> findAllByTopicId(Long topicId);

    Optional<Rating> findByTopicIdAndUserId(Long topicId, Long userId);
}
