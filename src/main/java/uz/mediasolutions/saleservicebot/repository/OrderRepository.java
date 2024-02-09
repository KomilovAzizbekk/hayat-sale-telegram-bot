package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByTgUserChatId(String chatId);

    boolean existsByTgUserChatId(String chatId);

}
