package tn.fst.spring.projet_spring.dto.forum;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingDTO {
    @NotNull
    @Min(1)
    @Max(5)
    private Double rating;

    @NotNull
    private Long topicId;
    @NotNull
    private Long userId;
}
