package apap.ti._5.vehicle_rental_2306203236_be.repository;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {

    List<RentalBooking> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    List<RentalBooking> findAllByDeletedAtIsNull();

    Optional<RentalBooking> findByIdAndDeletedAtIsNull(String id);

    List<RentalBooking> findByIdContainingIgnoreCaseOrVehicleId_IdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(
    String keyword1, String keyword2, String keyword3);

    RentalBooking findFirstByOrderByCreatedAtDesc();

    @Query(value = "SELECT * FROM rental_bookings ORDER BY id DESC LIMIT 1", nativeQuery = true)
    RentalBooking findLastestRentalBookingIncludingDeleted();
}