package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByIdAndEnabledIsTrueAndAccountNonExpiredIsTrueAndCredentialsNonExpiredIsTrueAndAccountNonLockedIsTrue(UUID userId);

    Optional<Object> findFirstByUsernameAndEnabledIsTrueAndAccountNonExpiredIsTrueAndAccountNonLockedIsTrueAndCredentialsNonExpiredIsTrue(String username);

    boolean existsByUsername(String username);

}
