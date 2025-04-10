package tn.fst.spring.projet_spring.dto.products;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class ProductSearchRequest {
    private String name;
    private String barcode;
    private String categoryName;
    private Double minPrice;
    private Double maxPrice;
    private Integer minStock;
    private Integer maxStock;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "name";
    private Sort.Direction sortDirection = Sort.Direction.ASC;
}