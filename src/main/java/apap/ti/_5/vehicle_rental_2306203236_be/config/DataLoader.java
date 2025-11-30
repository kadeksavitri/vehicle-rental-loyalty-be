package apap.ti._5.vehicle_rental_2306203236_be.config;

import apap.ti._5.vehicle_rental_2306203236_be.model.*;
import apap.ti._5.vehicle_rental_2306203236_be.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!prod")
public class DataLoader implements CommandLineRunner {

    private final RentalVendorRepository rentalVendorRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalAddOnRepository rentalAddOnRepository;
    private final RentalBookingRepository rentalBookingRepository;

    // Threshold untuk data dummy
    private static final int MIN_VENDORS = 3;
    private static final int MIN_VEHICLES_PER_VENDOR = 5;
    private static final int MIN_ADDONS = 5;
    private static final int MIN_BOOKINGS = 10;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data loader...");

        // Load Rental Vendors
        if (rentalVendorRepository.count() < MIN_VENDORS) {
            log.info("Loading dummy rental vendors...");
            loadRentalVendors();
        } else {
            log.info("Rental vendors already populated. Skipping...");
        }

        // Load Add-Ons
        if (rentalAddOnRepository.count() < MIN_ADDONS) {
            log.info("Loading dummy add-ons...");
            loadAddOns();
        } else {
            log.info("Add-ons already populated. Skipping...");
        }

        // Load Vehicles
        List<RentalVendor> vendors = rentalVendorRepository.findAll();
        if (!vendors.isEmpty() && vehicleRepository.count() < (MIN_VENDORS * MIN_VEHICLES_PER_VENDOR)) {
            log.info("Loading dummy vehicles...");
            loadVehicles(vendors);
        } else {
            log.info("Vehicles already populated. Skipping...");
        }

        // Load Bookings
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<RentalAddOn> addOns = rentalAddOnRepository.findAll();
        if (!vehicles.isEmpty() && rentalBookingRepository.count() < MIN_BOOKINGS) {
            log.info("Loading dummy bookings...");
            loadBookings(vehicles, addOns);
        } else {
            log.info("Bookings already populated. Skipping...");
        }

        log.info("Data loader completed!");
    }

    private void loadRentalVendors() {
        List<RentalVendor> vendors = Arrays.asList(
            RentalVendor.builder()
                .name("Jakarta Premium Rentals")
                .email("contact@jakartapremium.com")
                .phone("021-12345678")
                .listOfLocations(Arrays.asList("Jakarta Pusat", "Jakarta Selatan", "Jakarta Utara"))
                .build(),
            
            RentalVendor.builder()
                .name("Bali Auto Rental")
                .email("info@baliauto.com")
                .phone("0361-987654")
                .listOfLocations(Arrays.asList("Denpasar", "Ubud", "Seminyak"))
                .build(),
            
            RentalVendor.builder()
                .name("Surabaya Speed Rentals")
                .email("support@surabayaspeed.com")
                .phone("031-555123")
                .listOfLocations(Arrays.asList("Surabaya Pusat", "Surabaya Timur"))
                .build()
        );

        rentalVendorRepository.saveAll(vendors);
        log.info("Loaded {} rental vendors", vendors.size());
    }

    private void loadAddOns() {
        List<RentalAddOn> addOns = Arrays.asList(
            RentalAddOn.builder()
                .name("GPS Navigation")
                .price(50000.0)
                .build(),
            
            RentalAddOn.builder()
                .name("Child Safety Seat")
                .price(75000.0)
                .build(),
            
            RentalAddOn.builder()
                .name("Additional Driver")
                .price(100000.0)
                .build(),
            
            RentalAddOn.builder()
                .name("Full Insurance Coverage")
                .price(150000.0)
                .build(),
            
            RentalAddOn.builder()
                .name("WiFi Hotspot")
                .price(30000.0)
                .build()
        );

        rentalAddOnRepository.saveAll(addOns);
        log.info("Loaded {} add-ons", addOns.size());
    }

    private void loadVehicles(List<RentalVendor> vendors) {
        List<Vehicle> allVehicles = new ArrayList<>();

        String[][] vehicleData = {
            // {type, brand, model, transmission, fuelType, licensePlate}
            {"Sedan", "Toyota", "Camry", "Automatic", "Petrol", "B 1234 XYZ"},
            {"SUV", "Honda", "CR-V", "Automatic", "Petrol", "B 5678 ABC"},
            {"MPV", "Toyota", "Avanza", "Manual", "Petrol", "B 9012 DEF"},
            {"Sedan", "Mercedes", "C-Class", "Automatic", "Diesel", "B 3456 GHI"},
            {"SUV", "Mitsubishi", "Pajero Sport", "Automatic", "Diesel", "B 7890 JKL"},
            {"Hatchback", "Honda", "Jazz", "Automatic", "Petrol", "DK 1111 MNO"},
            {"MPV", "Daihatsu", "Xenia", "Manual", "Petrol", "DK 2222 PQR"},
            {"Sedan", "BMW", "320i", "Automatic", "Petrol", "DK 3333 STU"},
            {"SUV", "Toyota", "Fortuner", "Automatic", "Diesel", "L 4444 VWX"},
            {"MPV", "Suzuki", "Ertiga", "Manual", "Petrol", "L 5555 YZA"},
            {"Sedan", "Mazda", "6", "Automatic", "Petrol", "L 6666 BCD"},
            {"SUV", "Nissan", "X-Trail", "Automatic", "Petrol", "L 7777 EFG"},
            {"Hatchback", "Toyota", "Yaris", "Automatic", "Petrol", "B 8888 HIJ"},
            {"MPV", "Honda", "Mobilio", "Manual", "Petrol", "DK 9999 KLM"},
            {"Sedan", "Hyundai", "Elantra", "Automatic", "Petrol", "L 1010 NOP"}
        };

        int vehicleIndex = 0;
        for (RentalVendor vendor : vendors) {
            for (int i = 0; i < MIN_VEHICLES_PER_VENDOR; i++) {
                String[] data = vehicleData[vehicleIndex % vehicleData.length];
                
                String vehicleId = "VEH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                
                Vehicle vehicle = Vehicle.builder()
                    .id(vehicleId)
                    .rentalVendor(vendor)
                    .rentalVendorId(vendor.getId())
                    .type(data[0])
                    .brand(data[1])
                    .model(data[2])
                    .productionYear(2020 + (vehicleIndex % 4))
                    .location(vendor.getListOfLocations().get(0))
                    .licensePlate(data[5])
                    .capacity(data[0].equals("Sedan") ? 5 : data[0].equals("SUV") ? 7 : 6)
                    .transmission(data[3])
                    .fuelType(data[4])
                    .price(calculatePrice(data[0], data[3]))
                    .status("Available")
                    .build();

                allVehicles.add(vehicle);
                vehicleIndex++;
            }
        }

        vehicleRepository.saveAll(allVehicles);
        log.info("Loaded {} vehicles", allVehicles.size());
    }

    private void loadBookings(List<Vehicle> vehicles, List<RentalAddOn> addOns) {
        List<RentalBooking> bookings = new ArrayList<>();

        String[] statuses = {"Upcoming", "Ongoing", "Done"};
        
        for (int i = 0; i < MIN_BOOKINGS; i++) {
            Vehicle vehicle = vehicles.get(i % vehicles.size());
            String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            LocalDateTime pickUpTime = LocalDateTime.now().plusDays(i - 5);
            LocalDateTime dropOffTime = pickUpTime.plusDays(2 + (i % 3));
            
            // Select random add-ons (0-2 add-ons per booking)
            List<RentalAddOn> selectedAddOns = new ArrayList<>();
            if (!addOns.isEmpty()) {
                int addOnCount = i % 3; // 0, 1, or 2 add-ons
                for (int j = 0; j < addOnCount && j < addOns.size(); j++) {
                    selectedAddOns.add(addOns.get((i + j) % addOns.size()));
                }
            }
            
            double addOnsPrice = selectedAddOns.stream().mapToDouble(RentalAddOn::getPrice).sum();
            long days = java.time.Duration.between(pickUpTime, dropOffTime).toDays();
            double totalPrice = (vehicle.getPrice() * days) + addOnsPrice;
            
            RentalBooking booking = RentalBooking.builder()
                .id(bookingId)
                .vehicle(vehicle)
                .vehicleId(vehicle.getId())
                .pickUpTime(pickUpTime)
                .dropOffTime(dropOffTime)
                .pickUpLocation(vehicle.getLocation())
                .dropOffLocation(vehicle.getLocation())
                .capacityNeeded(vehicle.getCapacity())
                .transmissionNeeded(vehicle.getTransmission())
                .totalPrice(totalPrice)
                .includeDriver(i % 2 == 0)
                .status(statuses[i % 3])
                .listOfAddOns(selectedAddOns)
                .build();

            bookings.add(booking);
        }

        rentalBookingRepository.saveAll(bookings);
        log.info("Loaded {} bookings", bookings.size());
    }

    private double calculatePrice(String type, String transmission) {
        double basePrice = switch (type) {
            case "Sedan" -> 300000.0;
            case "SUV" -> 500000.0;
            case "MPV" -> 350000.0;
            case "Hatchback" -> 250000.0;
            default -> 300000.0;
        };

        if ("Automatic".equals(transmission)) {
            basePrice += 50000.0;
        }

        return basePrice;
    }
}
