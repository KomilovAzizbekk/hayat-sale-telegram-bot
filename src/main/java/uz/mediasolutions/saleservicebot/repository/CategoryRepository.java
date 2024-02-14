package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Category findByNameUz(String nameUz);

    Category findByNameRu(String nameRu);

    boolean existsByNumber(Integer number);

    boolean existsByNumberAndId(Integer number, UUID id);

    Page<Category> findAllByNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCase(Pageable pageable, String nameUz, String nameRu);

}
