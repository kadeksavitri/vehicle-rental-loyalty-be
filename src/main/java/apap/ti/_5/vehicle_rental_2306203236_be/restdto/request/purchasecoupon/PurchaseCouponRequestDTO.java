package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.purchasecoupon;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseCouponRequestDTO {
    private UUID couponId;
}
