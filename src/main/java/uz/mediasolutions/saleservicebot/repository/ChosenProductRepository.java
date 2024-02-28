package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mediasolutions.saleservicebot.entity.ChosenProduct;

import java.util.List;

public interface ChosenProductRepository extends JpaRepository<ChosenProduct, Long> {

    @Query(nativeQuery = true, value = "select * from chosen_product join public.basket_chosen_products bcp on chosen_product.id = bcp.chosen_products_id\n" +
            "where bcp.basket_id=:id order by updated_at")
    List<ChosenProduct> findAllByBasketId(Long id);

}
