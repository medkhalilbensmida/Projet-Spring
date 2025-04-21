package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.dto.products.ShelfRequest;
import tn.fst.spring.projet_spring.dto.products.ShelfResponse;
import tn.fst.spring.projet_spring.model.catalog.Shelf;

import java.util.List;
import java.util.Optional;

public interface IShelfService{
    
    ShelfResponse createShelf(ShelfRequest shelf);
    Optional<ShelfResponse> getShelf(Long id);
    List<ShelfResponse> getAllShelves();
    ShelfResponse updateShelf(Long id, ShelfRequest updatedShelf);
    void deleteShelf(Long id);
    
    List<ShelfResponse> findShelvesByName(String name);
    List<ShelfResponse> findShelvesByProduct(Long productId);
    List<ShelfResponse> searchShelvesByType(String type);
    List<ShelfResponse> searchShelvesByCoordinates(int x, int y);
    List<ShelfResponse> searchShelvesBetweenCoordinates(int x1, int x2, int y1, int y2);

}

