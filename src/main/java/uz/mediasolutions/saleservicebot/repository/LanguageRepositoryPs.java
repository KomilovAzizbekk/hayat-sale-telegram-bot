package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.LanguagePs;


public interface LanguageRepositoryPs extends JpaRepository<LanguagePs, Long> {
    boolean existsByKey(String key);

    Page<LanguagePs> findAllByKeyContainingIgnoreCase(Pageable pageable, String key);

}
