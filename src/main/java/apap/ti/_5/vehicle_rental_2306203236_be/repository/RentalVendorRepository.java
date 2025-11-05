package apap.ti._5.vehicle_rental_2306203236_be.repository;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalVendorRepository extends JpaRepository<RentalVendor, Integer> {
}

