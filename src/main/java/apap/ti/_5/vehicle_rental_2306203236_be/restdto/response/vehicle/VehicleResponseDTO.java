package apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDTO {
    private String id;
    private Integer rentalVendorId;
    private String rentalVendorName;
    private String type;
    private String brand;
    private String model;
    private Integer productionYear;
    private String location;
    private String licensePlate;
    private Integer capacity;
    private String transmission;
    private String fuelType;
    private Double price;
    private String status;
    private LocalDateTime createdAt;
}
