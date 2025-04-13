package tn.fst.spring.projet_spring.repositories.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.model.forum.Comment;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByTopicId(Long topicId);
}
