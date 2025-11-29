package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRentalBookingAddOnRequestDTO {
    @NotNull(message = "id is required")
    private String id;

    private List<String> listOfAddOns;
}
