package uz.mediasolutions.saleservicebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.mediasolutions.saleservicebot.entity.SuggestionsComplaints;

public interface SuggestsComplaintsRepo extends JpaRepository<SuggestionsComplaints, Long> {

}
