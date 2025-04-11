package tn.fst.spring.projet_spring.services.interfaces;

import tn.fst.spring.projet_spring.model.catalog.Shelf;

import java.util.List;
import java.util.Optional;

public interface IShelfService{
    
    Shelf createShelf(Shelf shelf);
    Optional<Shelf> getShelf(Long id);
    List<Shelf> getAllShelves();
    Shelf updateShelf(Long id, Shelf updatedShelf);
    void deleteShelf(Long id);
}

