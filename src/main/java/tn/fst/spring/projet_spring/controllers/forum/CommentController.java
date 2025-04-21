package tn.fst.spring.projet_spring.controllers.forum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.forum.CommentRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.CommentResponseDTO;
import tn.fst.spring.projet_spring.services.interfaces.ICommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;

    @GetMapping("/{topicId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable long topicId) {
        return ResponseEntity.ok(commentService.getCommentsByTopic(topicId));
    }
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO request){
        return ResponseEntity.ok(commentService.createComment(request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable long id, @RequestBody CommentRequestDTO request) {
        return ResponseEntity.ok(commentService.updateComment(id, request));
    }

    @PutMapping("like/{id}")
    public ResponseEntity<CommentResponseDTO> likeComment(@PathVariable long id) {
        return ResponseEntity.ok(commentService.likeComment(id));
    }
    @PutMapping("dislike/{id}")
    public ResponseEntity<CommentResponseDTO> dislikeComment(@PathVariable long id) {
         return ResponseEntity.ok(commentService.dislikeComment(id));
    }


}
