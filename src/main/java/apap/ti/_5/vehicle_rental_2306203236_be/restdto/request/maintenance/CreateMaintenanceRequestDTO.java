package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMaintenanceRequestDTO {
    @NotBlank(message = "vehicleId required")
    private String vehicleId;

    @NotNull(message = "serviceDate required")
    private LocalDateTime serviceDate;

    private String description;

    private Long cost;

    private String vendorNote;
}