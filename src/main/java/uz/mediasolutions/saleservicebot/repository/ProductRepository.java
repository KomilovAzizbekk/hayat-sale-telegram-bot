package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.Category;
import uz.mediasolutions.saleservicebot.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByCategoryId(Pageable pageable, UUID cId);

    List<Product> findAllByCategoryId(UUID cId, Sort sort);

    Product findByNameRu(String nameRu);

    Product findByNameUz(String nameUz);

    boolean existsByNumberAndCategoryId(Integer number, UUID categoryId);

    Page<Product> findAllByNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCase(Pageable pageable, String nameUz, String nameRu);

    Page<Product> findAllByCategoryIdAndNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCase(Pageable pageable, UUID cId, String nameUz, String nameRu);

}
