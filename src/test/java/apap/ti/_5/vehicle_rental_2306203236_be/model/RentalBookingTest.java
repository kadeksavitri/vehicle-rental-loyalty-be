// package apap.ti._5.vehicle_rental_2306203236_be.model;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;

// import java.time.LocalDateTime;
// import java.util.Collections;

// import static org.assertj.core.api.Assertions.assertThat;

// public class RentalBookingTest {

//     private RentalBooking booking;
//     private Vehicle vehicle;
//     private RentalAddOn addOn;

//     @BeforeEach
//     void setUp() {
//         vehicle = Vehicle.builder()
//                 .id("VH001")
//                 .brand("Toyota")
//                 .model("Avanza")
//                 .capacity(7)
//                 .transmission("Automatic")
//                 .pricePerDay(400000.0)
//                 .build();

//         addOn = RentalAddOn.builder()
//                 .id("AD001")
//                 .name("Car Seat")
//                 .price(50000.0)
//                 .build();

//         booking = RentalBooking.builder()
//                 .id("BK001")
//                 .vehicleId("VH001")
//                 .vehicle(vehicle)
//                 .pickUpTime(LocalDateTime.now().plusDays(1))
//                 .dropOffTime(LocalDateTime.now().plusDays(3))
//                 .pickUpLocation("Depok")
//                 .dropOffLocation("Jakarta")
//                 .capacityNeeded(5)
//                 .transmissionNeeded("Automatic")
//                 .totalPrice(1200000.0)
//                 .includeDriver(false)
//                 .status("Upcoming")
//                 .listOfAddOns(Collections.singletonList(addOn))
//                 .build();
//     }

//     @Test
//     void testBuilderAndGetters() {
//         assertThat(booking.getId()).isEqualTo("BK001");
//         assertThat(booking.getVehicle().getBrand()).isEqualTo("Toyota");
//         assertThat(booking.getListOfAddOns()).hasSize(1);
//         assertThat(booking.getTotalPrice()).isEqualTo(1200000.0);
//         assertThat(booking.isIncludeDriver()).isFalse();
//     }

//     @Test
//     void testPrePersistSetsCreatedAtAndUpdatedAt() {
//         booking.onCreate();
//         assertThat(booking.getCreatedAt()).isNotNull();
//         assertThat(booking.getUpdatedAt()).isNotNull();
//         assertThat(booking.getCreatedAt()).isEqualTo(booking.getUpdatedAt());
//     }

//     @Test
//     void testPreUpdateSetsUpdatedAt() throws InterruptedException {
//         booking.onCreate();
//         LocalDateTime oldUpdatedAt = booking.getUpdatedAt();

//         // simulasi ada jeda waktu sebelum update
//         Thread.sleep(10);
//         booking.onUpdate();

//         assertThat(booking.getUpdatedAt()).isAfter(oldUpdatedAt);
//     }

//     @Test
//     void testToStringEqualsAndHashCode() {
//         RentalBooking another = RentalBooking.builder()
//                 .id("BK001")
//                 .vehicleId("VH001")
//                 .build();

//         assertThat(booking).isNotNull();
//         assertThat(booking).isNotEqualTo(another); // beda field lain
//         assertThat(booking.toString()).contains("BK001");
//         assertThat(booking.hashCode()).isNotZero();
//     }
// }
