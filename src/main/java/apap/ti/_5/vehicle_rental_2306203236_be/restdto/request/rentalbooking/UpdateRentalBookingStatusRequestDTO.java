package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRentalBookingStatusRequestDTO {

    @NotBlank(message = "Booking ID is required")
    private String id;

    @NotBlank(message = "New status is required")
    private String newStatus; // e.g. "Upcoming", "Ongoing", "Done", "Cancelled"
}
