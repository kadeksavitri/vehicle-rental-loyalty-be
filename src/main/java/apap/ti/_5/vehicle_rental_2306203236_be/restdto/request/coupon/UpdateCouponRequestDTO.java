package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.coupon;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCouponRequestDTO {
    private String name;
    private String description;
    private Integer points;
    private Integer percentOff;
}