package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByTgUserChatId(String chatId);

    boolean existsByTgUserChatId(String chatId);

}
