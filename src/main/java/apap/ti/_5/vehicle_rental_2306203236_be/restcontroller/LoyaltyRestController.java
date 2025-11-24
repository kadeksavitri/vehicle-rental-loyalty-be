package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.model.Coupon;
import apap.ti._5.vehicle_rental_2306203236_be.model.LoyaltyAccount;
import apap.ti._5.vehicle_rental_2306203236_be.model.PurchasedCoupon;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.coupon.CreateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.coupon.UpdateCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.coupon.UseCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.purchasecoupon.PurchaseCouponRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.coupon.CouponResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.coupon.LoyaltyAccountResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.purchasecoupon.PurchasedCouponResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restservice.LoyaltyRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyRestController {

    @Autowired
    private LoyaltyRestService loyaltyRestService;

    @GetMapping("/points/{customerId}")
    public BaseResponseDTO<LoyaltyAccountResponseDTO> getPoints(@PathVariable UUID customerId) {

        LoyaltyAccount acc = loyaltyRestService.getAccount(customerId);

        var dto = LoyaltyAccountResponseDTO.builder()
                .customerId(acc.getCustomerId())
                .points(acc.getPoints())
                .build();

        return BaseResponseDTO.<LoyaltyAccountResponseDTO>builder()
                .status(200)
                .message("Success")
                .data(dto)
                .build();
    }

    @PostMapping("/points/add/{customerId}")
    public BaseResponseDTO<LoyaltyAccountResponseDTO> addPoints(
            @PathVariable UUID customerId,
            @RequestParam Integer points
    ) {
        LoyaltyAccount acc = loyaltyRestService.addPoints(customerId, points);

        var dto = LoyaltyAccountResponseDTO.builder()
                .customerId(acc.getCustomerId())
                .points(acc.getPoints())
                .build();

        return BaseResponseDTO.<LoyaltyAccountResponseDTO>builder()
                .status(200)
                .message("Points added")
                .data(dto)
                .build();
    }

    @PostMapping("/coupons")
    public BaseResponseDTO<CouponResponseDTO> createCoupon(@RequestBody CreateCouponRequestDTO request) {

        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setPoints(request.getPoints());
        coupon.setPercentOff(request.getPercentOff());

        coupon = loyaltyRestService.createCoupon(coupon);

        var dto = CouponResponseDTO.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .points(coupon.getPoints())
                .percentOff(coupon.getPercentOff())
                .createdDate(coupon.getCreatedDate())
                .updatedDate(coupon.getUpdatedDate())
                .build();

        return BaseResponseDTO.<CouponResponseDTO>builder()
                .status(200)
                .message("Coupon created")
                .data(dto)
                .build();
    }

    @GetMapping("/coupons")
    public BaseResponseDTO<List<CouponResponseDTO>> getAllCoupons() {

        var list = loyaltyRestService.getAllCoupons().stream().map(c ->
                CouponResponseDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .points(c.getPoints())
                        .percentOff(c.getPercentOff())
                        .createdDate(c.getCreatedDate())
                        .updatedDate(c.getUpdatedDate())
                        .build()
        ).collect(Collectors.toList());

        return BaseResponseDTO.<List<CouponResponseDTO>>builder()
                .status(200)
                .message("Success")
                .data(list)
                .build();
    }

    @PutMapping("/coupons/{couponId}")
    public BaseResponseDTO<CouponResponseDTO> updateCoupon(
            @PathVariable UUID couponId,
            @RequestBody UpdateCouponRequestDTO request
    ) {

        Coupon updated = loyaltyRestService.updateCoupon(couponId, request);

        var dto = CouponResponseDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .description(updated.getDescription())
                .points(updated.getPoints())
                .percentOff(updated.getPercentOff())
                .createdDate(updated.getCreatedDate())
                .updatedDate(updated.getUpdatedDate())
                .build();

        return BaseResponseDTO.<CouponResponseDTO>builder()
                .status(200)
                .message("Coupon updated")
                .data(dto)
                .build();
    }
    // ------------------------------------------------
    // PURCHASE COUPON (customer beli kupon)
    // ------------------------------------------------
    @PostMapping("/purchase/{customerId}")
    public BaseResponseDTO<PurchasedCouponResponseDTO> purchase(
            @PathVariable UUID customerId,
            @RequestBody PurchaseCouponRequestDTO request
    ) {
        PurchasedCoupon pc = loyaltyRestService.purchaseCoupon(customerId, request.getCouponId());

        var dto = PurchasedCouponResponseDTO.builder()
                .id(pc.getId())
                .code(pc.getCode())
                .couponId(pc.getCouponId())
                .customerId(pc.getCustomerId())
                .purchasedDate(pc.getPurchasedDate())
                .usedDate(pc.getUsedDate())
                .build();

        return BaseResponseDTO.<PurchasedCouponResponseDTO>builder()
                .status(200)
                .message("Coupon purchased")
                .data(dto)
                .build();
    }

    // ------------------------------------------------
    // USE COUPON (dipakai Bill Service)
    // ------------------------------------------------
    @PostMapping("/use")
    public BaseResponseDTO<Integer> useCoupon(@RequestBody UseCouponRequestDTO request) {

        Integer percent = loyaltyRestService.useCoupon(
                request.getCode(),
                request.getCustomerId()
        );

        return BaseResponseDTO.<Integer>builder()
                .status(200)
                .message(percent > 0 ? "Valid coupon" : "Invalid coupon")
                .data(percent)
                .build();
    }

    // ------------------------------------------------
    // GET PURCHASED COUPONS
    // ------------------------------------------------
    @GetMapping("/purchased/{customerId}")
    public BaseResponseDTO<List<PurchasedCouponResponseDTO>> getPurchased(@PathVariable UUID customerId) {

        var list = loyaltyRestService.getPurchasedCoupons(customerId).stream().map(pc ->
                PurchasedCouponResponseDTO.builder()
                        .id(pc.getId())
                        .code(pc.getCode())
                        .couponId(pc.getCouponId())
                        .customerId(pc.getCustomerId())
                        .purchasedDate(pc.getPurchasedDate())
                        .usedDate(pc.getUsedDate())
                        .build()
        ).collect(Collectors.toList());

        return BaseResponseDTO.<List<PurchasedCouponResponseDTO>>builder()
                .status(200)
                .message("Success")
                .data(list)
                .build();
    }
}
