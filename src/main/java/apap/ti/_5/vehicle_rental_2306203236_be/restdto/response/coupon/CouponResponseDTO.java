package apap.ti._5.vehicle_rental_2306203236_be.restdto.response.coupon;

import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private Integer points;
    private Integer percentOff;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}