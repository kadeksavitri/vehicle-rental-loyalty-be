package apap.ti._5.vehicle_rental_2306203236_be.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRentalBookingDto {

    @NotNull (message = "vehicleId is required")
    private String vehicleId;

    @NotNull (message = "pickupTime is required")
    private LocalDateTime pickUpTime;

    @NotNull (message = "dropOffTime is required")
    private LocalDateTime dropOffTime;

    @NotNull (message = "Pick up location is required")
    private String pickUpLocation;

    @NotNull (message = "Drop off location is required")
    private String dropOffLocation;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity cannot exceed 20")
    private Integer capacityNeeded;

    @NotNull (message = "Transmission required")
    private String transmissionNeeded;

    @NotNull (message = "Total price is required")
    private Double totalPrice;

    private List<String> ListOfAddOns;

    private boolean includeDriver;
    
}
