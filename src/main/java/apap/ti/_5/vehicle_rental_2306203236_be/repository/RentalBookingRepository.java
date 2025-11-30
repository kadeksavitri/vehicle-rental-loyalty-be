package apap.ti._5.vehicle_rental_2306203236_be.repository;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalBookingRepository extends JpaRepository<RentalBooking, String> {

    List<RentalBooking> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    List<RentalBooking> findAllByDeletedAtIsNull();

    Optional<RentalBooking> findByIdAndDeletedAtIsNull(String id);

    List<RentalBooking> findByIdContainingIgnoreCaseOrVehicle_IdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(
    String keyword1, String keyword2, String keyword3);

    RentalBooking findFirstByOrderByCreatedAtDesc();

    List<RentalBooking> findAllByVehicleAndDeletedAtIsNull(Vehicle vehicle);

    @Query(value = "SELECT * FROM rental_bookings WHERE id LIKE 'VR%' ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    RentalBooking findLastestRentalBookingIncludingDeleted();
    
    boolean existsByVehicleAndStatusIn(Vehicle vehicle, List<String> statuses);

    // Find bookings by rental vendor (through vehicle relationship)
    @Query("SELECT rb FROM RentalBooking rb WHERE rb.vehicle.rentalVendor.id = :vendorId AND rb.deletedAt IS NULL ORDER BY rb.createdAt DESC")
    List<RentalBooking> findAllByVendorId(@Param("vendorId") Integer vendorId);

    // Find bookings by customer ID
    List<RentalBooking> findAllByCustomerIdAndDeletedAtIsNullOrderByCreatedAtDesc(String customerId);

    // Find booking by ID and vendor ID
    @Query("SELECT rb FROM RentalBooking rb WHERE rb.id = :bookingId AND rb.vehicle.rentalVendor.id = :vendorId AND rb.deletedAt IS NULL")
    Optional<RentalBooking> findByIdAndVendorId(@Param("bookingId") String bookingId, @Param("vendorId") Integer vendorId);

    // Find booking by ID and customer ID
    Optional<RentalBooking> findByIdAndCustomerIdAndDeletedAtIsNull(String bookingId, String customerId);

}