package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.Market;

import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long> {

    Market findByNameUz(String nameUz);

    Market findByNameRu(String nameRu);

    Page<Market> findAllByNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCase(Pageable pageable, String nameUz, String nameRu);

}
