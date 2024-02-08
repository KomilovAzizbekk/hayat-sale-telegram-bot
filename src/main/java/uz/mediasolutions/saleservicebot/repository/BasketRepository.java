package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mediasolutions.saleservicebot.entity.Basket;

public interface BasketRepository extends JpaRepository<Basket, Long> {

    Basket findByTgUserChatId(String chatId);

    boolean existsByTgUserChatId(String chatId);

    @Query(nativeQuery = true, value = "delete from basket_chosen_products where chosen_products_id=:id")
    void deleteChosenProductsFromBasket(Long id);

}
