package apap.ti._5.vehicle_rental_2306203236_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loyalty_account")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoyaltyAccount {

    @Id
    private UUID customerId;   // Sama persis dengan ID customer dari Profile Service

    @Column(nullable = false)
    private Integer points = 0;

    private LocalDateTime updatedAt = LocalDateTime.now();
}
