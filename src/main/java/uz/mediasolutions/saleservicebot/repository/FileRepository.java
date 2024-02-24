package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.mediasolutions.saleservicebot.entity.Basket;
import uz.mediasolutions.saleservicebot.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    FileEntity findByName(String name);

    boolean existsByName(String name);

}
