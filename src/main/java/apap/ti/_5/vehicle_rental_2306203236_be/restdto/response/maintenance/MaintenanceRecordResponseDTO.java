package apap.ti._5.vehicle_rental_2306203236_be.restdto.response.maintenance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRecordResponseDTO {
    private String id;
    private String vehicleId;
    private String vehicleDisplay; // brief vehicle info (e.g., id - license)
    private LocalDateTime serviceDate;
    private String description;
    private Long cost;
    private String vendorNote;
    private String status;
    private LocalDateTime createdAt;
}
