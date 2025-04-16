package tn.fst.spring.projet_spring.dto.products;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ShelfRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    @NotBlank
    private int x;
    @NotBlank
    private int y;
    @NotBlank
    private int width;
    @NotBlank
    private int height;
}