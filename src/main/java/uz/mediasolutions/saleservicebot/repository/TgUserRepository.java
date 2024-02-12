package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.TgUser;

import java.util.List;

public interface TgUserRepository extends JpaRepository<TgUser, Long> {

    TgUser findByChatId(String chatId);

    boolean existsByChatId(String chatId);

    boolean existsAllByPhoneNumber(String phoneNumber);

    List<TgUser> findAllByNameContainsIgnoreCaseOrPhoneNumberContainsIgnoreCase(String name, String phoneNumber);

}
