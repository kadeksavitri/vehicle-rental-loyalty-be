package apap.ti._5.vehicle_rental_2306203236_be.repository;

import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {  

    List<Vehicle> findAllByDeletedAtIsNull();

    Optional<Vehicle> findByIdAndDeletedAtIsNull(String id);

    List<Vehicle> findByType(String type);

    boolean existsByLicensePlate(String licensePlate);

    List<Vehicle> findByIdContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCase( String id, String brand, String model);

    List<Vehicle> findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase( String type1, String id, String type2, String brand, String type3, String model);

    Vehicle findFirstByOrderByIdDesc();

    @Query(value = "SELECT * FROM vehicles ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Vehicle findLatestVehicleIncludingDeleted();
}
