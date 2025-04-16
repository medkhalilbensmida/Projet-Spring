package tn.fst.spring.projet_spring.repositories.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.model.forum.ForumTopic;

public interface ForumTopicRepository extends JpaRepository<ForumTopic, Long> {
    boolean existsByTitle(String title);

}