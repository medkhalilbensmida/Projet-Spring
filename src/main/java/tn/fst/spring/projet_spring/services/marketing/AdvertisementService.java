package tn.fst.spring.projet_spring.services.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.entities.marketing.Advertisement;
import tn.fst.spring.projet_spring.repositories.marketing.AdvertisementRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    public List<Advertisement> getAll() {
        return advertisementRepository.findAll();
    }

    public Optional<Advertisement> getById(Long id) {
        return advertisementRepository.findById(id);
    }

    public Advertisement save(Advertisement ad) {
        return advertisementRepository.save(ad);
    }

    public void delete(Long id) {
        advertisementRepository.deleteById(id);
    }
}
