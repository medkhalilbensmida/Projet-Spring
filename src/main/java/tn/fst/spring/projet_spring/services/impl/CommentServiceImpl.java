package tn.fst.spring.projet_spring.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.dto.forum.CommentRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.CommentResponseDTO;
import tn.fst.spring.projet_spring.model.auth.User;
import tn.fst.spring.projet_spring.model.forum.Comment;
import tn.fst.spring.projet_spring.model.forum.ForumTopic;
import tn.fst.spring.projet_spring.repositories.auth.UserRepository;
import tn.fst.spring.projet_spring.repositories.forum.CommentRepository;
import tn.fst.spring.projet_spring.repositories.forum.ForumTopicRepository;
import tn.fst.spring.projet_spring.services.interfaces.ICommentService;
import tn.fst.spring.projet_spring.services.utils.BadWordFilter;
import tn.fst.spring.projet_spring.services.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ForumTopicRepository forumTopicRepository;
    private final BadWordFilter badWordFilter;

    @Override
    public CommentResponseDTO createComment(CommentRequestDTO request) {
        //pour le moment on passe id dans body mais il faut utiliser id de user connecte
        User author = SecurityUtils.getCurrentUser(userRepository);

        ForumTopic topic = forumTopicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (badWordFilter.containsBadWords(request.getContent())) {
            throw new RuntimeException("Le commentaire contient des mots interdits");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setTopic(topic);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLikes(0);

        Comment saved = commentRepository.save(comment);
        return mapToResponseDTO(saved);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByTopic(Long topicId) {
        return commentRepository.findByTopicId(topicId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long id) {
        SecurityUtils.getCurrentUser(userRepository);
        commentRepository.deleteById(id);
    }

    @Override
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO request) {
       Comment comment=commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
       //on verifie si c'est le meme user
        SecurityUtils.getCurrentUser(userRepository);

        comment.setContent(request.getContent());
        Comment updated = commentRepository.save(comment);
        return mapToResponseDTO(updated);
    }
    @Override
    public CommentResponseDTO likeComment(Long commentId) {
        User currentUser = SecurityUtils.getCurrentUser(userRepository);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (comment.getLikedBy().contains(currentUser)) {
            comment.setLikes(comment.getLikes() - 1);
            comment.getLikedBy().remove(currentUser);
        } else {
            comment.setLikes(comment.getLikes() + 1);
            comment.getLikedBy().add(currentUser);

            if (comment.getDislikedBy().contains(currentUser)) {
                comment.setDislikes(comment.getDislikes() - 1);
                comment.getDislikedBy().remove(currentUser);
            }
        }

        commentRepository.save(comment);
        return mapToResponseDTO(comment);
    }

    @Override
    public CommentResponseDTO dislikeComment(Long commentId) {
        User currentUser = SecurityUtils.getCurrentUser(userRepository);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (comment.getDislikedBy().contains(currentUser)) {
            comment.setDislikes(comment.getDislikes() - 1);
            comment.getDislikedBy().remove(currentUser);
        } else {
            comment.setDislikes(comment.getDislikes() + 1);
            comment.getDislikedBy().add(currentUser);

            if (comment.getLikedBy().contains(currentUser)) {
                comment.setLikes(comment.getLikes() - 1);
                comment.getLikedBy().remove(currentUser);
            }
        }

        commentRepository.save(comment);
        return mapToResponseDTO(comment);
    }




    private CommentResponseDTO mapToResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorUsername(comment.getAuthor().getUsername())
                .likes(comment.getLikes())
                .dislikes(comment.getDislikes())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
