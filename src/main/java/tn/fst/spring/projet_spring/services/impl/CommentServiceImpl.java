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
        /*User author = SecurityUtils.getCurrentUser(userRepository);*/
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));

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
        commentRepository.deleteById(id);
    }

    @Override
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO request) {
       Comment comment=commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
       //on verifie si c'est le meme user
//        User author = SecurityUtils.getCurrentUser(userRepository);
//        if(!author.getId().equals(comment.getAuthor().getId())){
//            throw new RuntimeException("Vous n'avez pas le droit de modifier ce commentaire");
//        }
        comment.setContent(request.getContent());
        Comment updated = commentRepository.save(comment);
        return mapToResponseDTO(updated);
    }

    private CommentResponseDTO mapToResponseDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorUsername(comment.getAuthor().getUsername())
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
