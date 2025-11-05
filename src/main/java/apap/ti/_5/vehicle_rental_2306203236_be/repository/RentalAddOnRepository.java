package apap.ti._5.vehicle_rental_2306203236_be.repository;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface RentalAddOnRepository extends JpaRepository<RentalAddOn, java.util.UUID> {
}