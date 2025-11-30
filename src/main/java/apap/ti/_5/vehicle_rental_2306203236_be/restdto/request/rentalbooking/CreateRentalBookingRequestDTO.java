package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRentalBookingRequestDTO {

    @NotNull (message = "customerId is required")
    private String customerId;

    @NotNull (message = "vehicleId is required")
    private String vehicleId;

    @NotNull (message = "pickupTime is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime pickUpTime;

    @NotNull (message = "dropOffTime is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dropOffTime;


    @NotBlank (message = "Pick up location is required")
    private String pickUpLocation;

    @NotBlank (message = "Drop off location is required")
    private String dropOffLocation;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity cannot exceed 20")
    private Integer capacityNeeded;

    @NotBlank
    (message = "Transmission required")
    private String transmissionNeeded;

    @NotNull (message = "Total price is required")
    private Double totalPrice;

    private List<String> ListOfAddOns;

    private boolean includeDriver;
    
}
