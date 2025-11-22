package apap.ti._5.vehicle_rental_2306203236_be.restdto.response.rentalbooking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalAddOnResponseDTO {
    private String id;
    private String name;
    private Double price;
}