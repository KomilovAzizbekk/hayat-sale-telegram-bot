package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {



}
