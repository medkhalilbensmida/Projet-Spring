package tn.fst.spring.projet_spring.services.marketing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.fst.spring.projet_spring.model.marketing.TargetedAudience;
import tn.fst.spring.projet_spring.repositories.marketing.TargetedAudienceRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TargetedAudienceService {

    private final TargetedAudienceRepository repository;

    public List<TargetedAudience> getAll() {
        return repository.findAll();
    }

    public Optional<TargetedAudience> getById(Long id) {
        return repository.findById(id);
    }

    public TargetedAudience save(TargetedAudience audience) {
        return repository.save(audience);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
