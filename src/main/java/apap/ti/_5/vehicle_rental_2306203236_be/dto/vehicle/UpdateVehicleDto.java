package apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleDto {
    
    @NotNull(message = "Vehicle ID is required")
    private String id;

    @NotNull (message = "Rental vendor id required")
    private Integer rentalVendorId;

    @NotNull (message = "Type required")
    private String type;

    @Size(max = 100, message = "Brand must be less than 100 character")
    private String brand;

    @Size(max = 100, message = "Model must be less than 100 character")
    private String model;

    private Integer productionYear;

    @NotNull (message = "Location required")
    private String location;

    @NotEmpty (message = "License plate required")
    private String licensePlate;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity cannot exceed 10")
    private Integer capacity;

    @NotNull (message = "Transmission required")
    private String transmission;

    @NotNull (message = "Fuel type required")
    private String fuelType;

    @NotNull (message = "Price required")
    private Double price;

    @NotEmpty (message = "Status required")
    private String status;

}
