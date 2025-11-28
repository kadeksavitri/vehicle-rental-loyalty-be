package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.service.LocationService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationRestController {

    private final LocationService locationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','RENTAL_VENDOR','CUSTOMER')")
    public List<String> getAllProvinces() {
        return locationService.getAllProvinces();
    }
}
