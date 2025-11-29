package apap.ti._5.vehicle_rental_2306203236_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchased_coupon")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasedCoupon {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;   

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;

    private LocalDateTime purchasedDate = LocalDateTime.now();

    private LocalDateTime usedDate;   // null kalau belum dipakai
}