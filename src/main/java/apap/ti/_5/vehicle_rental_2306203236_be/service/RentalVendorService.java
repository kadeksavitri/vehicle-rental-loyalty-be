package apap.ti._5.vehicle_rental_2306203236_be.service;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalVendorService {

    private final RentalVendorRepository rentalVendorRepository;
    private final LocationService locationService;

    /**
     * Get or create rental vendor by email.
     * If vendor doesn't exist, creates a new one with dummy data.
     * If vendor exists but has less than 2 locations, adds dummy locations.
     * 
     * @param email the email of the vendor
     * @return the vendor (existing or newly created)
     */
    public RentalVendor getOrCreateVendor(String email) {
        RentalVendor vendor = rentalVendorRepository.findByEmail(email);
        
        if (vendor == null) {
            // Get provinces from API
            List<String> allProvinces = locationService.getAllProvinces();
            List<String> defaultLocations = allProvinces.size() >= 2 
                ? List.of(allProvinces.get(0), allProvinces.get(1))
                : List.of("Jakarta", "Bandung"); // Fallback if API fails
            
            // Create dummy vendor if not found
            String vendorName = "Vendor " + email.split("@")[0];
            vendor = RentalVendor.builder()
                    .name(vendorName)
                    .email(email)
                    .phone("081234567890")
                    .listOfLocations(defaultLocations)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            vendor = rentalVendorRepository.save(vendor);
        } else {
            // Ensure vendor has at least 2 locations
            if (vendor.getListOfLocations() == null || vendor.getListOfLocations().size() < 2) {
                List<String> locations = new ArrayList<>(vendor.getListOfLocations() != null ? vendor.getListOfLocations() : List.of());
                List<String> allProvinces = locationService.getAllProvinces();
                
                // Add provinces until we have at least 2 locations
                for (String province : allProvinces) {
                    if (!locations.contains(province)) {
                        locations.add(province);
                        if (locations.size() >= 2) break;
                    }
                }
                
                vendor.setListOfLocations(locations);
                vendor.setUpdatedAt(LocalDateTime.now());
                vendor = rentalVendorRepository.save(vendor);
            }
        }
        
        return vendor;
    }

    /**
     * Get vendor by ID, returns null if not found
     */
    public RentalVendor getVendorById(Integer id) {
        return rentalVendorRepository.findById(id).orElse(null);
    }

    /**
     * Get vendor by email, returns null if not found
     */
    public RentalVendor getVendorByEmail(String email) {
        return rentalVendorRepository.findByEmail(email);
    }
}
