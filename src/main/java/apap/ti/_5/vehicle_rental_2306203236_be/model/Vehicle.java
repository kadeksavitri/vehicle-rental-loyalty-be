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
@Table(name = "vehicles")
@SQLDelete(sql = "UPDATE vehicles  SET deleted_at = NOW() WHERE id=?")
@Where(clause = "deleted_at IS NULL")
public class Vehicle {

    @Id
    private String id; // format: VEHxxxx

    @Column (name = "id_rental_vendor", nullable = false)
    private Integer rentalVendorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rental_vendor_id", referencedColumnName = "id")
    private RentalVendor rentalVendor;

    @Column (name = "type", nullable = false)
    private String type; // Sedan, SUV, MPV, Luxury

    @Column (name = "brand")
    private String brand;

    @Column (name = "model")
    private String model;

    @Column (name = "production_year")
    private Integer productionYear;

    @Column (name = "location", nullable = false)
    private String location;

    @Column (name = "license_plate", nullable = false)
    private String licensePlate;

    @Column (name = "capacity")
    private Integer capacity;

    @Column (name = "transmission", nullable = false)
    private String transmission; // Manual / Automatic

    @Column (name = "fuel_type", nullable = false)
    private String fuelType; // Bensin, Diesel, Hybrid, Listrik

    @Column (name = "price", nullable = false)
    private Double price;

    @Column (name = "status", nullable = false)
    private String status; // Available, In Use, Unavailable

    @Column (name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column (name = "updated_at")
    private LocalDateTime updatedAt;

    @Column (name = "deleted_at")
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

