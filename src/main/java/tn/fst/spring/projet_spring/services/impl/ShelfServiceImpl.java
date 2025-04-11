package tn.fst.spring.projet_spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.model.catalog.Shelf;
import tn.fst.spring.projet_spring.repositories.products.ShelfRepository;
import tn.fst.spring.projet_spring.services.interfaces.IShelfService;

import java.util.List;
import java.util.Optional;

@Service
public class ShelfServiceImpl implements IShelfService {

    @Autowired
    private ShelfRepository shelfRepository;

    @Override
    public Shelf createShelf(Shelf shelf) {
        return shelfRepository.save(shelf);
    }

    @Override
    public Optional<Shelf> getShelf(Long id) {
        return shelfRepository.findById(id);
    }

    @Override
    public List<Shelf> getAllShelves() {
        return shelfRepository.findAll();
    }

    @Override
    public Shelf updateShelf(Long id, Shelf updatedShelf) {
        Optional<Shelf> existingShelf = shelfRepository.findById(id);

        if (existingShelf.isPresent()) {
            Shelf shelf = existingShelf.get();
            shelf.setName(updatedShelf.getName());
            shelf.setType(updatedShelf.getType());
            shelf.setX(updatedShelf.getX());
            shelf.setY(updatedShelf.getY());
            shelf.setWidth(updatedShelf.getWidth());
            shelf.setHeight(updatedShelf.getHeight());
            return shelfRepository.save(shelf);
        }
        return null;  // ou une exception personnalisée
    }

    @Override
    public void deleteShelf(Long id) {
        shelfRepository.deleteById(id);
    }
}
