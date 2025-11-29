package apap.ti._5.vehicle_rental_2306203236_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "points", nullable = false)
    private Integer points;    

    @Column(name = "percent_off", nullable = false)
    private Integer percentOff;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate = LocalDateTime.now();
}
