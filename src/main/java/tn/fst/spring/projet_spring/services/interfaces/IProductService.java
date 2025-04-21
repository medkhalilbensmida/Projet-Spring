package tn.fst.spring.projet_spring.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import tn.fst.spring.projet_spring.dto.products.*;

import java.util.List;

public interface IProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest productRequest);
    void deleteProduct(Long id);
    boolean verifyTunisianBarcode(String barcode);
    Page<ProductResponse> searchProducts(ProductSearchRequest searchRequest);
    ProductResponse updateProduct(Long id, ProductUpdateRequest productRequest);
    BarcodeExtractionResponse extractBarcode(MultipartFile file);
    ProductResponse extractProductDetailsFromBarcodeImage(MultipartFile file);

}