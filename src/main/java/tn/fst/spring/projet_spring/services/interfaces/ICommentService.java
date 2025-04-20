package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.forum.CommentRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.CommentResponseDTO;

import java.util.List;

public interface ICommentService {
    CommentResponseDTO createComment(CommentRequestDTO request);
    List<CommentResponseDTO> getCommentsByTopic(Long topicId);
    void deleteComment(Long id);
    CommentResponseDTO updateComment(Long commentId, CommentRequestDTO request);
    CommentResponseDTO dislikeComment(Long commentId);
    CommentResponseDTO likeComment(Long commentId);
}
