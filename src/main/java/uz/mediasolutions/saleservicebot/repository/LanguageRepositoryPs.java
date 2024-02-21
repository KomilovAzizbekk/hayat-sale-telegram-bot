package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mediasolutions.saleservicebot.entity.LanguagePs;

import java.util.Optional;


public interface LanguageRepositoryPs extends JpaRepository<LanguagePs, Long> {
    boolean existsByKey(String key);

    Optional<LanguagePs> findByIdAndKey(Long id, String key);

    @Query(nativeQuery = true,
            value = "SELECT distinct l.* FROM language_ps l JOIN public.language_source ls on l.id = ls.language_ps_id\n" +
                    "            WHERE LOWER(l.key) LIKE LOWER(CONCAT('%', :searchKey, '%')) OR\n" +
                    "            LOWER(ls.translation) LIKE LOWER(CONCAT('%', :searchKey, '%'))\n")
    Page<LanguagePs> findAllByKeyAndTranslations(Pageable pageable, String searchKey);

    LanguagePs findByKey(String key);
}
