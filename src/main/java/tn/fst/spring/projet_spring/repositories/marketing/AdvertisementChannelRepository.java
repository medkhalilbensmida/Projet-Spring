package tn.fst.spring.projet_spring.repositories.marketing;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.projet_spring.entities.marketing.AdvertisementChannel;

public interface AdvertisementChannelRepository extends JpaRepository<AdvertisementChannel, Long> {
}
