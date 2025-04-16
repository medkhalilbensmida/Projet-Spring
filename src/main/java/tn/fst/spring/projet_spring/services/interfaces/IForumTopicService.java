package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.forum.ForumTopicRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.ForumTopicResponseDTO;

import java.util.List;

public interface IForumTopicService {
    ForumTopicResponseDTO createTopic(ForumTopicRequestDTO request);
    List<ForumTopicResponseDTO> getAllTopics();
    ForumTopicResponseDTO getTopicById(Long id);
    ForumTopicResponseDTO updateTopic(Long id, ForumTopicRequestDTO request);
    void deleteTopic(Long id);
}
