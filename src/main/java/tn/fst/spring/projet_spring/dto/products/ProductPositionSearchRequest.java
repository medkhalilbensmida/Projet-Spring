package tn.fst.spring.projet_spring.dto.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductPositionSearchRequest {
    private Long productId;
    private Long shelfId;
    private Integer xmin;
    private Integer xmax;
    private Integer ymin;
    private Integer ymax;
}
