package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMaintenanceStatusRequestDTO {
    @NotBlank(message = "status required")
    private String status;
}