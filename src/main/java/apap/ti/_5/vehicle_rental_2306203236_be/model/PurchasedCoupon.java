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

    private String code;   // Wajib unik

    private UUID customerId;

    private UUID couponId;

    private LocalDateTime purchasedDate = LocalDateTime.now();

    private LocalDateTime usedDate;   // null jika belum dipakai
}