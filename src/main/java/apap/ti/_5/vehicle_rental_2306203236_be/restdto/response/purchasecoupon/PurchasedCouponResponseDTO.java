package apap.ti._5.vehicle_rental_2306203236_be.restdto.response.purchasecoupon;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchasedCouponResponseDTO {
    private UUID id;
    private String code;
    private UUID customerId;
    private UUID couponId;
    private LocalDateTime purchasedDate;
    private LocalDateTime usedDate;
}
