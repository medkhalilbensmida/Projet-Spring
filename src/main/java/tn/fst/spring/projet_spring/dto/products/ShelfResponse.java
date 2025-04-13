package tn.fst.spring.projet_spring.dto.products;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShelfResponse {
    private Long id;
    private String name;
    private String type;
    private int x;
    private int y;
    private int width;
    private int height;
}