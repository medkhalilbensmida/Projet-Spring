package tn.fst.spring.projet_spring.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.fst.spring.projet_spring.dto.products.ShelfRequest;
import tn.fst.spring.projet_spring.dto.products.ShelfResponse;
import tn.fst.spring.projet_spring.model.catalog.Shelf;
import tn.fst.spring.projet_spring.repositories.products.ShelfRepository;
import tn.fst.spring.projet_spring.services.interfaces.IShelfService;

import java.util.List;
import java.util.Optional;

@Service
public class ShelfServiceImpl implements IShelfService {




    @Autowired
    private ShelfRepository shelfRepository;



    private Shelf toEntity(ShelfRequest request) {
        Shelf shelf =  new Shelf();
        shelf.setName(request.getName());
        shelf.setType(request.getType());
        shelf.setX(request.getX());
        shelf.setY(request.getY());
        shelf.setWidth(request.getWidth());
        shelf.setHeight(request.getHeight());
        return shelf;
    }


    private ShelfResponse toResponse (Shelf shelf){
        ShelfResponse shelfResponse = new ShelfResponse();
        shelfResponse.setId(shelf.getId());
        shelfResponse.setName(shelf.getName());
        shelfResponse.setType(shelf.getType());
        shelfResponse.setX(shelf.getX());
        shelfResponse.setY(shelf.getY());
        shelfResponse.setWidth(shelf.getWidth());
        shelfResponse.setHeight(shelf.getHeight());
        return shelfResponse;
    }

    @Override
    public ShelfResponse createShelf(ShelfRequest shelf) {
        Shelf shelfEntity = toEntity(shelf);
        Shelf savedShelf = shelfRepository.save(shelfEntity);
        return toResponse(savedShelf);
    }

    @Override
    public Optional<ShelfResponse> getShelf(Long id) {
        Optional<Shelf> shelf = shelfRepository.findById(id);
        if (shelf.isPresent()) {
            return Optional.of(toResponse(shelf.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<ShelfResponse> getAllShelves() {
        return shelfRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ShelfResponse updateShelf(Long id, ShelfRequest updatedShelf) {
        Optional<Shelf> existingShelf = shelfRepository.findById(id);

        if (existingShelf.isPresent()) {
            Shelf shelf = existingShelf.get();
            shelf.setName(updatedShelf.getName());
            shelf.setType(updatedShelf.getType());
            shelf.setX(updatedShelf.getX());
            shelf.setY(updatedShelf.getY());
            shelf.setWidth(updatedShelf.getWidth());
            shelf.setHeight(updatedShelf.getHeight());
            Shelf newShelf = shelfRepository.save(shelf);
            return toResponse(newShelf);
        }
        return null;  // ou une exception personnalisée
    }

    @Override
    public void deleteShelf(Long id) {
        shelfRepository.deleteById(id);
    }

    @Override
    public List<ShelfResponse> findShelvesByProduct(Long productId) {
        return shelfRepository.findByPositionsProductId(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ShelfResponse> searchShelvesByType(String type) {
        return shelfRepository.findByType(type)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ShelfResponse> searchShelvesByCoordinates(int x, int y) {
        return shelfRepository.findByXAndY(x, y)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ShelfResponse> searchShelvesBetweenCoordinates(int x1, int y1, int x2, int y2) {
        return shelfRepository.findByXBetweenAndYBetween(x1, x2, y1, y2)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ShelfResponse> findShelvesByName(String name) {
        return shelfRepository.findByName(name)
                .stream()
                .map(this::toResponse)
                .toList();
    }


}


