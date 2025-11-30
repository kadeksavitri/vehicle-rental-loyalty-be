package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchAvailableVehicleRequestDTO {

    @NotNull(message = "Pick up time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime pickUpTime;

    @NotNull(message = "Drop off time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dropOffTime;

    @NotBlank(message = "Pick up location is required")
    private String pickUpLocation;

    @NotBlank(message = "Drop off location is required")
    private String dropOffLocation;

    @NotNull(message = "Capacity needed is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacityNeeded;

    @NotBlank(message = "Transmission needed is required")
    private String transmissionNeeded;
}
