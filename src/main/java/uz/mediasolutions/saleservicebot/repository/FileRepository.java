package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
