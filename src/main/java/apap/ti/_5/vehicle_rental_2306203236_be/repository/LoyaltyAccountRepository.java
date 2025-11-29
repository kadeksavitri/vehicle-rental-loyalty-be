package apap.ti._5.vehicle_rental_2306203236_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import apap.ti._5.vehicle_rental_2306203236_be.model.LoyaltyAccount;
import java.util.UUID;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, UUID> {
}