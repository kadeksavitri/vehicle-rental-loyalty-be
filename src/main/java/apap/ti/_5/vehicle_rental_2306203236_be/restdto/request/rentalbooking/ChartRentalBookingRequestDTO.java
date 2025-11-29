package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartRentalBookingRequestDTO {

    @NotBlank(message = "Period is required (Monthly or Quarterly)")
    private String period;

    @Min(value = 2000, message = "Year must be >= 2000")
    @Max(value = 2100, message = "Year must be <= 2100")
    private int year;

}
