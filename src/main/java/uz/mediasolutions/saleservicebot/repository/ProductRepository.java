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

    Page<Product> findAllByCategoryIdAndDeletedIsFalseOrderByCreatedAtDesc(Pageable pageable, UUID cId);

    List<Product> findAllByCategoryIdAndDeletedIsFalse(UUID cId, Sort sort);

    List<Product> findAllByCategoryIdAndDeletedIsFalse(UUID cId);

    Product findByNameRuAndDeletedIsFalse(String nameRu);

    Product findByNameUzAndDeletedIsFalse(String nameUz);

    boolean existsByNumberAndCategoryIdAndDeletedIsFalse(Integer number, UUID categoryId);

    boolean existsByNumberAndCategoryIdAndIdAndDeletedIsFalse(Integer number, UUID categoryId, UUID id);

    Page<Product> findAllByNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCaseAndDeletedIsFalseOrderByNumberAsc(Pageable pageable, String nameUz, String nameRu);

    Page<Product> findAllByCategoryIdAndNameRuContainsIgnoreCaseOrNameUzContainsIgnoreCaseAndDeletedIsFalseOrderByNumberAsc(Pageable pageable, UUID cId, String nameUz, String nameRu);

    Page<Product> findAllByDeletedIsFalseOrderByNumberAsc(Pageable pageable);

    Product findByForUniqueAndDeletedIsFalse(Integer num);
}
