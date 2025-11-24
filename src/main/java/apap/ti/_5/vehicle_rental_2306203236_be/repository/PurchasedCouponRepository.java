package apap.ti._5.vehicle_rental_2306203236_be.repository;

import apap.ti._5.vehicle_rental_2306203236_be.model.PurchasedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchasedCouponRepository extends JpaRepository<PurchasedCoupon, UUID> {

    PurchasedCoupon findByCode(String code);
}