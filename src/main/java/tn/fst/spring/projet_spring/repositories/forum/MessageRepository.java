package tn.fst.spring.projet_spring.repositories.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.model.forum.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
