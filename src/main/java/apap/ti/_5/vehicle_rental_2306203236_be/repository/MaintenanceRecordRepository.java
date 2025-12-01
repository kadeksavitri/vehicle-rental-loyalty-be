package apap.ti._5.vehicle_rental_2306203236_be.repository;

import apap.ti._5.vehicle_rental_2306203236_be.model.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, String> {
    List<MaintenanceRecord> findAllByDeletedAtIsNull();

    java.util.Optional<MaintenanceRecord> findByIdAndDeletedAtIsNull(String id);

    List<MaintenanceRecord> findAllByRentalVendorIdAndDeletedAtIsNull(Integer rentalVendorId);

    List<MaintenanceRecord> findAllByVehicleIdAndDeletedAtIsNull(String vehicleId);
}