package apap.ti._5.vehicle_rental_2306203236_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rentalBookings")
@SQLDelete(sql = "UPDATE rentalBookings SET deleted_at = NOW() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
public class RentalBooking {

    @Id
    private String id; // format: VRxxxxx

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicleId;

    @Column (name = "pick_up_time", nullable = false)
    private LocalDateTime pickUpTime;

    @Column (name = "drop_off_time", nullable = false)
    private LocalDateTime dropOffTime;

    @Column (name = "pickUpLocation", nullable = false)
    private String pickUpLocation;

    @Column (name = "dropOffLocation", nullable = false)
    private String dropOffLocation;

    @Column (name = "capacityNeeded")
    private Integer capacityNeeded;

    @Column (name = "transmissionNeeded")
    private String transmissionNeeded;

    @Column (name = "totalPrice" , nullable = false)
    private Double totalPrice;

    @Column (name = "includeDriver")
    private boolean includeDriver;

    @Column (name = "status", nullable = false)
    private String status; // Upcoming, Ongoing, Done

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "booking_addon",
        joinColumns = @JoinColumn(name = "booking_id"),
        inverseJoinColumns = @JoinColumn(name = "addon_id")
    )
    private List<RentalAddOn> listOfAddOns;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

