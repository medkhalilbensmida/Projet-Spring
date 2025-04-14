package tn.fst.spring.projet_spring.controllers.forum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.forum.ForumTopicRequestDTO;
import tn.fst.spring.projet_spring.dto.forum.ForumTopicResponseDTO;
import tn.fst.spring.projet_spring.services.interfaces.IForumTopicService;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class ForumTopicController {
    private final IForumTopicService forumTopicService ;

    @PostMapping
    public ResponseEntity<ForumTopicResponseDTO> createTopic(@RequestBody ForumTopicRequestDTO request) {
        ForumTopicResponseDTO created = forumTopicService.createTopic(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ForumTopicResponseDTO>> getAllTopics() {
        return ResponseEntity.ok(forumTopicService.getAllTopics());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumTopicResponseDTO> getTopicById(@PathVariable Long id) {
        return ResponseEntity.ok(forumTopicService.getTopicById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumTopicResponseDTO> updateTopic(@PathVariable Long id, @RequestBody ForumTopicRequestDTO request) {
        return ResponseEntity.ok(forumTopicService.updateTopic(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        forumTopicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}
