package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.coupon;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UseCouponRequestDTO {
    private String code;
    private UUID customerId;
}
