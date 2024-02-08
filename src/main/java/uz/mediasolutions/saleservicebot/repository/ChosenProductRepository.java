package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.ChosenProduct;

public interface ChosenProductRepository extends JpaRepository<ChosenProduct, Long> {

}
