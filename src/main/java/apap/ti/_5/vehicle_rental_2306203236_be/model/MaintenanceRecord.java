package apap.ti._5.vehicle_rental_2306203236_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "maintenance_records")
@SQLDelete(sql = "UPDATE maintenance_records SET deleted_at = NOW() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
public class MaintenanceRecord {

    @Id
    private String id;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_ref", referencedColumnName = "id")
    private Vehicle vehicle;

    @Column(name = "rental_vendor_id", nullable = false)
    private Integer rentalVendorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rental_vendor_ref", referencedColumnName = "id")
    private RentalVendor rentalVendor;

    @Column(name = "service_date", nullable = false)
    private LocalDateTime serviceDate;

    @Column(name = "description")
    private String description;

    @Column(name = "cost")
    private Long cost;

    @Column(name = "vendor_note")
    private String vendorNote;

    @Column(name = "status", nullable = false)
    private String status; // e.g., Ongoing, Completed

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
