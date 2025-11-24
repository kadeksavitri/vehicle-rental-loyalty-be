package apap.ti._5.vehicle_rental_2306203236_be.restdto.response.coupon;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyAccountResponseDTO {
    private UUID customerId;
    private Integer points;
}