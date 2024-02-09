package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.Market;

public interface MarketRepository extends JpaRepository<Market, Long> {

    Market findByNameUz(String nameUz);

    Market findByNameRu(String nameRu);

    Page<Market> findAllByNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCase(String nameUz, String nameRu, Pageable pageable);

}
