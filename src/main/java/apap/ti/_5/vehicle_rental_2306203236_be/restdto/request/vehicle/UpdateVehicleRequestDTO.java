package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull       ;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleRequestDTO {
    
    @NotNull (message = "Vehicle ID is required")
    private String id;

    @NotNull (message = "Rental vendor id required")
    private Integer rentalVendorId;

    @NotBlank (message = "Type required")
    private String type;

    @Size(max = 100, message = "Brand must be less than 100 character")
    private String brand;

    @Size(max = 100, message = "Model must be less than 100 character")
    private String model;

    @Min(value = 1900, message = "Production year must be at least 1900")
    @Max(value = 2025, message = "Production year cannot exceed 2025")
    private Integer productionYear;

    @NotBlank (message = "Location required")
    private String location;

    @NotBlank (message = "License plate required")
    private String licensePlate;

    @NotNull (message = "Capacity required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity canNotBlank exceed 10")
    private Integer capacity;

    @NotBlank (message = "Transmission required")
    private String transmission;

    @NotBlank (message = "Fuel type required")
    private String fuelType;

    @NotNull (message = "Price required")
    private Double price;

    @NotBlank(message = "Status required")
    private String status;

}
