// package apap.ti._5.vehicle_rental_2306203236_be.restservice;

// import apap.ti._5.vehicle_rental_2306203236_be.model.Coupon;
// import apap.ti._5.vehicle_rental_2306203236_be.model.LoyaltyAccount;
// import apap.ti._5.vehicle_rental_2306203236_be.model.PurchasedCoupon;
// import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.coupon.UpdateCouponRequestDTO;

// import java.util.List;
// import java.util.UUID;

// public interface LoyaltyRestService {

//     // Loyalty Points
//     LoyaltyAccount addPoints(UUID customerId, Integer points);
//     LoyaltyAccount getAccount(UUID customerId);

//     // Coupon CRUD
//     Coupon createCoupon(Coupon coupon);
//     List<Coupon> getAllCoupons();
//     Coupon getCouponById(UUID id);

//     Coupon updateCoupon(UUID id, UpdateCouponRequestDTO request);

//     // Purchase Coupon
//     PurchasedCoupon purchaseCoupon(UUID customerId, UUID couponId);

//     // Use Coupon (Bill Service)
//     Integer useCoupon(String code, UUID customerId);

//     // Purchased Coupons
//     List<PurchasedCoupon> getPurchasedCoupons(UUID customerId);
// }
