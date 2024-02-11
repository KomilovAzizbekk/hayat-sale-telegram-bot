package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.TgUser;

public interface TgUserRepository extends JpaRepository<TgUser, Long> {

    TgUser findByChatId(String chatId);

    boolean existsByChatId(String chatId);

    boolean existsAllByPhoneNumber(String phoneNumber);

    Page<TgUser> findAllByNameContainsIgnoreCaseOrPhoneNumberContainsIgnoreCase(Pageable pageable, String name, String phoneNumber);

}
