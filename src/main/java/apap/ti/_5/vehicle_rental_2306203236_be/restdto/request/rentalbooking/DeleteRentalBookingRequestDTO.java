package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRentalBookingRequestDTO {

    @NotNull(message = "Rental Booking ID is required")
    private String id;
}