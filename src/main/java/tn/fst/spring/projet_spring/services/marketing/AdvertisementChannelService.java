package tn.fst.spring.projet_spring.services.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.entities.marketing.AdvertisementChannel;
import tn.fst.spring.projet_spring.repositories.marketing.AdvertisementChannelRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementChannelService {

    private final AdvertisementChannelRepository repository;

    public List<AdvertisementChannel> getAll() {
        return repository.findAll();
    }

    public Optional<AdvertisementChannel> getById(Long id) {
        return repository.findById(id);
    }

    public AdvertisementChannel save(AdvertisementChannel channel) {
        return repository.save(channel);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
