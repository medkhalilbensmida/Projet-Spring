package tn.fst.spring.projet_spring.dto.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPositionResponse {
    private Long productId;
    private Long shelfId;
    private int x;
    private int y;
    private int width;
    private int height;
    private int zIndex;
}