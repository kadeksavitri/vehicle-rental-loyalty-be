// package apap.ti._5.vehicle_rental_2306203236_be.model;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;

// import java.time.LocalDateTime;
// import java.util.Collections;
// import java.util.List;

// import static org.assertj.core.api.Assertions.assertThat;

// class RentalVendorTest {

//     private RentalVendor vendor;
//     private Vehicle vehicle;

//     @BeforeEach
//     void setUp() {
//         vehicle = Vehicle.builder()
//                 .id("V001")
//                 .brand("Toyota")
//                 .model("Avanza")
//                 .capacity(7)
//                 .transmission("Automatic")
//                 .pricePerDay(450000.0)
//                 .build();

//         vendor = RentalVendor.builder()
//                 .id(1)
//                 .name("PT Maju Jaya")
//                 .email("vendor@example.com")
//                 .phone("08123456789")
//                 .listOfLocations(List.of("Depok", "Jakarta"))
//                 .listOfVehicles(List.of(vehicle))
//                 .createdAt(LocalDateTime.of(2025, 11, 7, 10, 0))
//                 .updatedAt(LocalDateTime.of(2025, 11, 7, 12, 0))
//                 .build();
//     }

//     @Test
//     void testBuilderAndGetters() {
//         assertThat(vendor.getId()).isEqualTo(1);
//         assertThat(vendor.getName()).isEqualTo("PT Maju Jaya");
//         assertThat(vendor.getEmail()).isEqualTo("vendor@example.com");
//         assertThat(vendor.getPhone()).isEqualTo("08123456789");
//         assertThat(vendor.getListOfLocations()).containsExactly("Depok", "Jakarta");
//         assertThat(vendor.getListOfVehicles()).hasSize(1);
//         assertThat(vendor.getCreatedAt()).isNotNull();
//         assertThat(vendor.getUpdatedAt()).isNotNull();
//     }

//     @Test
//     void testSettersMutability() {
//         vendor.setId(2);
//         vendor.setName("PT Baru Maju");
//         vendor.setEmail("new@example.com");
//         vendor.setPhone("08987654321");
//         vendor.setListOfLocations(List.of("Bandung"));
//         vendor.setListOfVehicles(Collections.emptyList());
//         LocalDateTime now = LocalDateTime.now();
//         vendor.setCreatedAt(now);
//         vendor.setUpdatedAt(now);

//         assertThat(vendor.getId()).isEqualTo(2);
//         assertThat(vendor.getName()).isEqualTo("PT Baru Maju");
//         assertThat(vendor.getEmail()).isEqualTo("new@example.com");
//         assertThat(vendor.getPhone()).isEqualTo("08987654321");
//         assertThat(vendor.getListOfLocations()).containsExactly("Bandung");
//         assertThat(vendor.getListOfVehicles()).isEmpty();
//         assertThat(vendor.getCreatedAt()).isEqualTo(now);
//         assertThat(vendor.getUpdatedAt()).isEqualTo(now);
//     }

//     @Test
//     void testNoArgsConstructor() {
//         RentalVendor empty = new RentalVendor();
//         assertThat(empty).isNotNull();
//         empty.setName("No Args Vendor");
//         assertThat(empty.getName()).isEqualTo("No Args Vendor");
//     }

//     @Test
//     void testAllArgsConstructor() {
//         LocalDateTime now = LocalDateTime.now();
//         RentalVendor full = new RentalVendor(
//                 5,
//                 "PT Full Vendor",
//                 "full@example.com",
//                 "0811223344",
//                 List.of("Bekasi", "Bogor"),
//                 List.of(vehicle),
//                 now,
//                 now
//         );

//         assertThat(full.getId()).isEqualTo(5);
//         assertThat(full.getName()).isEqualTo("PT Full Vendor");
//         assertThat(full.getEmail()).isEqualTo("full@example.com");
//         assertThat(full.getPhone()).isEqualTo("0811223344");
//         assertThat(full.getListOfLocations()).containsExactly("Bekasi", "Bogor");
//         assertThat(full.getListOfVehicles()).hasSize(1);
//         assertThat(full.getCreatedAt()).isEqualTo(now);
//         assertThat(full.getUpdatedAt()).isEqualTo(now);
//     }

//     @Test
//     void testPrePersistSetsCreatedAndUpdatedAt() {
//         RentalVendor newVendor = new RentalVendor();
//         newVendor.onCreate();
//         assertThat(newVendor.getCreatedAt()).isNotNull();
//         assertThat(newVendor.getUpdatedAt()).isNotNull();
//         assertThat(newVendor.getCreatedAt()).isEqualTo(newVendor.getUpdatedAt());
//     }

//     @Test
//     void testPreUpdateSetsUpdatedAtLater() throws InterruptedException {
//         vendor.onCreate();
//         LocalDateTime before = vendor.getUpdatedAt();
//         Thread.sleep(10);
//         vendor.onUpdate();
//         assertThat(vendor.getUpdatedAt()).isAfter(before);
//     }

//     @Test
//     void testEqualsAndHashCode() {
//         RentalVendor same = RentalVendor.builder()
//                 .id(1)
//                 .name("PT Maju Jaya")
//                 .email("vendor@example.com")
//                 .build();

//         RentalVendor different = RentalVendor.builder()
//                 .id(99)
//                 .name("PT Lain")
//                 .email("other@example.com")
//                 .build();

//         assertThat(vendor).isEqualTo(vendor);
//         assertThat(vendor).isNotEqualTo(null);
//         assertThat(vendor).isNotEqualTo(different);
//         assertThat(vendor.hashCode()).isNotZero();
//         assertThat(same.hashCode()).isEqualTo(same.hashCode());
//     }

//     @Test
//     void testToStringContainsKeyFields() {
//         String text = vendor.toString();
//         assertThat(text).contains("PT Maju Jaya");
//         assertThat(text).contains("vendor@example.com");
//         assertThat(text).contains("08123456789");
//     }
// }
