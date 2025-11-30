package apap.ti._5.vehicle_rental_2306203236_be.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponseDTO {
    private long registeredVehicles;
    private long registeredVendors;
    private long bookingsMade;
}
