package apap.ti._5.vehicle_rental_2306203236_be.util;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306203236_be.service.LocationService;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataSeeder {

    private final RentalVendorRepository rentalVendorRepository;
    private final RentalAddOnRepository rentalAddOnRepository;
    private final LocationService locationService;
    private final Faker faker = new Faker(new Locale("en-US"));

    public DataSeeder(
            RentalVendorRepository rentalVendorRepository,
            RentalAddOnRepository rentalAddOnRepository,
            LocationService locationService
    ) {
        this.rentalVendorRepository = rentalVendorRepository;
        this.rentalAddOnRepository = rentalAddOnRepository;
        this.locationService = locationService;
    }

    @PostConstruct
    public void seedData() {
        seedVendors();
        seedAddOns();
    }

    private void seedVendors() {
        if (rentalVendorRepository.count() > 0) {
            System.out.println("RentalVendor data already exists. Skipping vendor seeding...");
            return;
        }

        List<String> allProvinces;
        try {
            allProvinces = locationService.getAllProvinces();
            System.out.println("Successfully fetched provinces from wilayah.id (" + allProvinces.size() + ")");
        } catch (Exception e) {
            System.err.println("Failed to fetch provinces from wilayah.id, using fallback list.");
            allProvinces = getFallbackProvinces();
        }

        List<RentalVendor> vendorList = new ArrayList<>();

        for (int i = 0; i < 5; i++) { // generate 5 vendors
            Collections.shuffle(allProvinces);
            List<String> vendorLocations = allProvinces.stream()
                    .limit(5)
                    .collect(Collectors.toList());

            RentalVendor vendor = RentalVendor.builder()
                    .name(faker.company().name())
                    .email(faker.internet().emailAddress())
                    .phone(faker.phoneNumber().cellPhone())
                    .listOfLocations(vendorLocations)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            vendorList.add(vendor);
        }

        rentalVendorRepository.saveAll(vendorList);
        System.out.println("Successfully seeded " + vendorList.size() + " RentalVendors with API-based provinces.");
    }

    private List<String> getFallbackProvinces() {
        return Arrays.asList(
                "DKI Jakarta",
                "Jawa Barat",
                "Jawa Tengah",
                "Jawa Timur",
                "Bali",
                "Sumatera Utara",
                "Kalimantan Timur",
                "Sulawesi Selatan",
                "Riau",
                "Papua"
        );
    }

    private void seedAddOns() {
        if (rentalAddOnRepository.count() > 0) {
            System.out.println("RentalAddOn data already exists. Skipping add-on seeding...");
            return;
        }

        String[] addOnNames = {
            "GPS Navigation", "Child Seat", "Wi-Fi Hotspot", "Roof Rack",
            "Additional Insurance", "Portable Cooler", "Dash Camera",
            "Pet Carrier", "Bluetooth Audio", "Car Charger"
        };

        List<RentalAddOn> addOnList = new ArrayList<>();

        for (String name : addOnNames) {
            int price = faker.number().numberBetween(50000, 250000);
            price = (price / 1000) * 1000; 

            RentalAddOn addOn = RentalAddOn.builder()
                    .name(name)
                    .price((double) price)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            addOnList.add(addOn);
        }

        rentalAddOnRepository.saveAll(addOnList);
        System.out.println("Successfully seeded " + addOnList.size() + " RentalAddOns.");
    }

}
