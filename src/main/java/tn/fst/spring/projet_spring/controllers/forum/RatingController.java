package tn.fst.spring.projet_spring.controllers.forum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.forum.RatingDTO;
import tn.fst.spring.projet_spring.services.interfaces.IRatingService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rate")
public class RatingController {
    private IRatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingDTO> createRating (@RequestBody RatingDTO request) {
        return ResponseEntity.ok(ratingService.createRating(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<RatingDTO> updateRating (@PathVariable Long id ,@RequestBody RatingDTO request) {
        return  ResponseEntity.ok(ratingService.updateRating(id, request));
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<List<RatingDTO>> getRatingByTopicId (@PathVariable Long topicId) {
        return ResponseEntity.ok(ratingService.getAllRatings(topicId));
    }
}
