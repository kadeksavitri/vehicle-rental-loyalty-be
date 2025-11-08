// package apap.ti._5.vehicle_rental_2306203236_be.model;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;

// import java.time.LocalDateTime;

// import static org.assertj.core.api.Assertions.assertThat;

// class VehicleTest {

//     private Vehicle vehicle;
//     private RentalVendor vendor;

//     @BeforeEach
//     void setUp() {
//         vendor = RentalVendor.builder()
//                 .id(1)
//                 .name("PT Maju Jaya")
//                 .email("vendor@example.com")
//                 .phone("08123456789")
//                 .build();

//         vehicle = Vehicle.builder()
//                 .id("VH001")
//                 .rentalVendorId(1)
//                 .rentalVendor(vendor)
//                 .type("SUV")
//                 .brand("Toyota")
//                 .model("Rush")
//                 .productionYear(2023)
//                 .location("Depok")
//                 .licensePlate("B 1234 ABC")
//                 .capacity(7)
//                 .transmission("Automatic")
//                 .fuelType("Bensin")
//                 .price(450000.0)
//                 .status("Available")
//                 .createdAt(LocalDateTime.of(2025, 11, 7, 10, 0))
//                 .updatedAt(LocalDateTime.of(2025, 11, 7, 12, 0))
//                 .deletedAt(null)
//                 .build();
//     }

//     @Test
//     void testBuilderAndGetters() {
//         assertThat(vehicle.getId()).isEqualTo("VH001");
//         assertThat(vehicle.getRentalVendorId()).isEqualTo(1);
//         assertThat(vehicle.getRentalVendor().getName()).isEqualTo("PT Maju Jaya");
//         assertThat(vehicle.getType()).isEqualTo("SUV");
//         assertThat(vehicle.getBrand()).isEqualTo("Toyota");
//         assertThat(vehicle.getModel()).isEqualTo("Rush");
//         assertThat(vehicle.getProductionYear()).isEqualTo(2023);
//         assertThat(vehicle.getLocation()).isEqualTo("Depok");
//         assertThat(vehicle.getLicensePlate()).isEqualTo("B 1234 ABC");
//         assertThat(vehicle.getCapacity()).isEqualTo(7);
//         assertThat(vehicle.getTransmission()).isEqualTo("Automatic");
//         assertThat(vehicle.getFuelType()).isEqualTo("Bensin");
//         assertThat(vehicle.getPrice()).isEqualTo(450000.0);
//         assertThat(vehicle.getStatus()).isEqualTo("Available");
//         assertThat(vehicle.getCreatedAt()).isNotNull();
//         assertThat(vehicle.getUpdatedAt()).isNotNull();
//         assertThat(vehicle.getDeletedAt()).isNull();
//     }

//     @Test
//     void testSettersMutability() {
//         vehicle.setId("VH002");
//         vehicle.setRentalVendorId(2);
//         vehicle.setRentalVendor(null);
//         vehicle.setType("Sedan");
//         vehicle.setBrand("Honda");
//         vehicle.setModel("City");
//         vehicle.setProductionYear(2020);
//         vehicle.setLocation("Jakarta");
//         vehicle.setLicensePlate("B 5678 XYZ");
//         vehicle.setCapacity(5);
//         vehicle.setTransmission("Manual");
//         vehicle.setFuelType("Diesel");
//         vehicle.setPrice(300000.0);
//         vehicle.setStatus("In Use");
//         LocalDateTime now = LocalDateTime.now();
//         vehicle.setCreatedAt(now);
//         vehicle.setUpdatedAt(now);
//         vehicle.setDeletedAt(now);

//         assertThat(vehicle.getId()).isEqualTo("VH002");
//         assertThat(vehicle.getRentalVendorId()).isEqualTo(2);
//         assertThat(vehicle.getRentalVendor()).isNull();
//         assertThat(vehicle.getType()).isEqualTo("Sedan");
//         assertThat(vehicle.getBrand()).isEqualTo("Honda");
//         assertThat(vehicle.getModel()).isEqualTo("City");
//         assertThat(vehicle.getProductionYear()).isEqualTo(2020);
//         assertThat(vehicle.getLocation()).isEqualTo("Jakarta");
//         assertThat(vehicle.getLicensePlate()).isEqualTo("B 5678 XYZ");
//         assertThat(vehicle.getCapacity()).isEqualTo(5);
//         assertThat(vehicle.getTransmission()).isEqualTo("Manual");
//         assertThat(vehicle.getFuelType()).isEqualTo("Diesel");
//         assertThat(vehicle.getPrice()).isEqualTo(300000.0);
//         assertThat(vehicle.getStatus()).isEqualTo("In Use");
//         assertThat(vehicle.getCreatedAt()).isEqualTo(now);
//         assertThat(vehicle.getUpdatedAt()).isEqualTo(now);
//         assertThat(vehicle.getDeletedAt()).isEqualTo(now);
//     }

//     @Test
//     void testNoArgsConstructor() {
//         Vehicle empty = new Vehicle();
//         assertThat(empty).isNotNull();
//         empty.setId("VH999");
//         assertThat(empty.getId()).isEqualTo("VH999");
//     }

//     @Test
//     void testAllArgsConstructor() {
//         LocalDateTime now = LocalDateTime.now();
//         Vehicle full = new Vehicle(
//                 "VH003",
//                 3,
//                 vendor,
//                 "MPV",
//                 "Daihatsu",
//                 "Xenia",
//                 2022,
//                 "Bandung",
//                 "B 9999 QWE",
//                 6,
//                 "Automatic",
//                 "Hybrid",
//                 500000.0,
//                 "Unavailable",
//                 now,
//                 now,
//                 null
//         );

//         assertThat(full.getId()).isEqualTo("VH003");
//         assertThat(full.getRentalVendorId()).isEqualTo(3);
//         assertThat(full.getRentalVendor()).isEqualTo(vendor);
//         assertThat(full.getType()).isEqualTo("MPV");
//         assertThat(full.getBrand()).isEqualTo("Daihatsu");
//         assertThat(full.getModel()).isEqualTo("Xenia");
//         assertThat(full.getProductionYear()).isEqualTo(2022);
//         assertThat(full.getLocation()).isEqualTo("Bandung");
//         assertThat(full.getLicensePlate()).isEqualTo("B 9999 QWE");
//         assertThat(full.getCapacity()).isEqualTo(6);
//         assertThat(full.getTransmission()).isEqualTo("Automatic");
//         assertThat(full.getFuelType()).isEqualTo("Hybrid");
//         assertThat(full.getPrice()).isEqualTo(500000.0);
//         assertThat(full.getStatus()).isEqualTo("Unavailable");
//         assertThat(full.getCreatedAt()).isEqualTo(now);
//         assertThat(full.getUpdatedAt()).isEqualTo(now);
//     }

//     @Test
//     void testPrePersistSetsCreatedAndUpdatedAt() {
//         Vehicle v = new Vehicle();
//         v.onCreate();
//         assertThat(v.getCreatedAt()).isNotNull();
//         assertThat(v.getUpdatedAt()).isNotNull();
//         assertThat(v.getCreatedAt()).isEqualTo(v.getUpdatedAt());
//     }

//     @Test
//     void testPreUpdateSetsUpdatedAtLater() throws InterruptedException {
//         vehicle.onCreate();
//         LocalDateTime before = vehicle.getUpdatedAt();
//         Thread.sleep(5);
//         vehicle.onUpdate();
//         assertThat(vehicle.getUpdatedAt()).isAfter(before);
//     }

//     @Test
//     void testEqualsAndHashCode() {
//         Vehicle same = Vehicle.builder()
//                 .id("VH001")
//                 .rentalVendorId(1)
//                 .build();

//         Vehicle different = Vehicle.builder()
//                 .id("DIFF001")
//                 .rentalVendorId(99)
//                 .build();

//         assertThat(vehicle).isEqualTo(vehicle);
//         assertThat(vehicle).isNotEqualTo(null);
//         assertThat(vehicle).isNotEqualTo(different);
//         assertThat(vehicle.hashCode()).isNotZero();
//         assertThat(same.hashCode()).isEqualTo(same.hashCode());
//     }

//     @Test
//     void testToStringContainsKeyFields() {
//         String result = vehicle.toString();
//         assertThat(result).contains("VH001");
//         assertThat(result).contains("Toyota");
//         assertThat(result).contains("Rush");
//         assertThat(result).contains("Available");
//     }
// }
