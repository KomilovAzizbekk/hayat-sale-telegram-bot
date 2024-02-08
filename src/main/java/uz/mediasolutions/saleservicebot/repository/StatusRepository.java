package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.Status;
import uz.mediasolutions.saleservicebot.enums.StatusName;

public interface StatusRepository extends JpaRepository<Status, Long> {

    Status findByName(StatusName name);

}
