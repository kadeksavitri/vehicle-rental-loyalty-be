package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.config.security.ProfileClient;
import apap.ti._5.vehicle_rental_2306203236_be.model.Coupon;
import apap.ti._5.vehicle_rental_2306203236_be.model.LoyaltyAccount;
import apap.ti._5.vehicle_rental_2306203236_be.model.PurchasedCoupon;
import apap.ti._5.vehicle_rental_2306203236_be.repository.CouponRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.LoyaltyAccountRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.PurchasedCouponRepository;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.coupon.UpdateCouponRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoyaltyRestServiceImpl implements LoyaltyRestService {

	@Autowired
	private LoyaltyAccountRepository loyaltyAccountRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private PurchasedCouponRepository purchasedCouponRepository;

	@Autowired
	private ProfileClient profileClient;

	@Override
	public LoyaltyAccount addPoints(UUID customerId, Integer points) {
		LoyaltyAccount acc = loyaltyAccountRepository
				.findById(customerId)
				.orElse(new LoyaltyAccount(customerId, 0, LocalDateTime.now()));

		acc.setPoints(acc.getPoints() + points);
		acc.setUpdatedAt(LocalDateTime.now());
		return loyaltyAccountRepository.save(acc);
	}

	@Override
	public LoyaltyAccount getAccount(UUID customerId) {
		return loyaltyAccountRepository
				.findById(customerId)
				.orElse(new LoyaltyAccount(customerId, 0, LocalDateTime.now()));
	}

	@Override
	public Coupon createCoupon(Coupon coupon) {
		coupon.setCreatedDate(LocalDateTime.now());
		coupon.setUpdatedDate(LocalDateTime.now());
		return couponRepository.save(coupon);
	}

	@Override
	public List<Coupon> getAllCoupons() {
		return couponRepository.findAll();
	}

	@Override
	public Coupon getCouponById(UUID id) {
		return couponRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Coupon not found"));
	}
	@Override
	public Coupon updateCoupon(UUID id, UpdateCouponRequestDTO request) {

		Coupon existing = couponRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Coupon not found"));

		existing.setName(request.getName());
		existing.setDescription(request.getDescription());
		existing.setPoints(request.getPoints());
		existing.setPercentOff(request.getPercentOff());
		existing.setUpdatedDate(LocalDateTime.now());

		return couponRepository.save(existing);
	}

	@Override
	public PurchasedCoupon purchaseCoupon(UUID customerId, UUID couponId) {

		LoyaltyAccount acc = getAccount(customerId);
		Coupon coupon = getCouponById(couponId);

		if (acc.getPoints() < coupon.getPoints()) {
			throw new RuntimeException("Poin tidak mencukupi");
		}

		acc.setPoints(acc.getPoints() - coupon.getPoints());
		acc.setUpdatedAt(LocalDateTime.now());
		loyaltyAccountRepository.save(acc);

		String code = generateCouponCode(customerId, coupon);

		PurchasedCoupon pc = new PurchasedCoupon();
		pc.setCode(code);
		pc.setCouponId(couponId);
		pc.setCustomerId(customerId);
		pc.setPurchasedDate(LocalDateTime.now());
		pc.setUsedDate(null);

		return purchasedCouponRepository.save(pc);
	}

	@Override
	public Integer useCoupon(String code, UUID customerId) {
		PurchasedCoupon pc = purchasedCouponRepository.findByCode(code);

		if (pc == null)
			return 0;

		if (!pc.getCustomerId().equals(customerId))
			return 0;

		if (pc.getUsedDate() != null)
			return 0;

		Coupon coupon = getCouponById(pc.getCouponId());

		pc.setUsedDate(LocalDateTime.now());
		purchasedCouponRepository.save(pc);

		return coupon.getPercentOff();
	}

	@Override
	public List<PurchasedCoupon> getPurchasedCoupons(UUID customerId) {
		return purchasedCouponRepository.findAll()
				.stream()
				.filter(x -> x.getCustomerId().equals(customerId))
				.toList();
	}

	private String generateCouponCode(UUID customerId, Coupon coupon) {

		String customerName = "UNKNOWN";
		try {
			var user = profileClient.getUserById(customerId.toString());
			if (user != null && user.getData() != null && user.getData().getName() != null) {
				customerName = user.getData().getName();
			}
		} catch (Exception ignored) {}

		String couponPart = coupon.getName()
				.replaceAll("\\s+", "")
				.substring(0, Math.min(5, coupon.getName().length()))
				.toUpperCase();

		String namePart = customerName
				.replaceAll("\\s+", "")
				.substring(0, Math.min(5, customerName.length()))
				.toUpperCase();

		long count = purchasedCouponRepository.count() + 1;

		return couponPart + "-" + namePart + "-" + count;
	}
}
