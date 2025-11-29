package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalAddOnRepository;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/addons")
@RequiredArgsConstructor
public class RentalAddOnRestController {

    private final RentalAddOnRepository rentalAddOnRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','RENTAL_VENDOR')")
    public List<RentalAddOn> getAllAddOns() {
        return rentalAddOnRepository.findAll();
    }
}
