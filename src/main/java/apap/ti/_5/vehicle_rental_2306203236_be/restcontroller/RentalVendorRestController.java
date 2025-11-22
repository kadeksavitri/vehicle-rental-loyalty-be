package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class RentalVendorRestController {

    private final RentalVendorRepository rentalVendorRepository;

    @GetMapping
    public List<RentalVendor> getAllVendors() {
        return rentalVendorRepository.findAll();
    }

    @GetMapping("/{id}")
    public RentalVendor getVendor(@PathVariable Integer id) {
        return rentalVendorRepository.findById(id).orElse(null);
    }
}
