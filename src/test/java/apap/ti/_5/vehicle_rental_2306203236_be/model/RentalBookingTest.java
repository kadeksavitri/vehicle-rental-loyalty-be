// package apap.ti._5.vehicle_rental_2306203236_be.model;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;

// class RentalBookingTest {

//     private RentalBooking booking;
//     private Vehicle vehicle;
//     private RentalAddOn addOn1;
//     private RentalAddOn addOn2;

//     @BeforeEach
//     void setUp() {
//         vehicle = new Vehicle();
//         vehicle.setId("VEH0001");
//         vehicle.setBrand("Toyota");
//         vehicle.setType("SUV");
//         vehicle.setPrice(500000.0);
//         vehicle.setStatus("Available");
//         vehicle.setLocation("Jakarta");

//         addOn1 = new RentalAddOn();
// addOn1.setId(UUID.randomUUID());
//         addOn1.setName("GPS");
//         addOn1.setPrice(100000.0);

//         addOn2 = new RentalAddOn();
// addOn2.setId(UUID.randomUUID());
//         addOn2.setName("Child Seat");
//         addOn2.setPrice(150000.0);

//         booking = RentalBooking.builder()
//                 .id("VR000001")
//                 .vehicleId("VEH0001")
//                 .vehicle(vehicle)
//                 .pickUpTime(LocalDateTime.of(2025, 1, 1, 10, 0))
//                 .dropOffTime(LocalDateTime.of(2025, 1, 3, 10, 0))
//                 .pickUpLocation("Jakarta")
//                 .dropOffLocation("Bandung")
//                 .capacityNeeded(4)
//                 .transmissionNeeded("Automatic")
//                 .totalPrice(1000000.0)
//                 .includeDriver(true)
//                 .status("Upcoming")
//                 .listOfAddOns(List.of(addOn1, addOn2))
//                 .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
//                 .updatedAt(LocalDateTime.of(2025, 1, 1, 0, 0))
//                 .deletedAt(null)
//                 .build();
//     }

//     @Test
//     void testBuilderAndGetters() {
//         assertEquals("VR000001", booking.getId());
//         assertEquals("VEH0001", booking.getVehicleId());
//         assertEquals(vehicle, booking.getVehicle());
//         assertEquals("Jakarta", booking.getPickUpLocation());
//         assertEquals("Bandung", booking.getDropOffLocation());
//         assertEquals(4, booking.getCapacityNeeded());
//         assertEquals("Automatic", booking.getTransmissionNeeded());
//         assertEquals(1000000.0, booking.getTotalPrice());
//         assertTrue(booking.isIncludeDriver());
//         assertEquals("Upcoming", booking.getStatus());
//         assertEquals(2, booking.getListOfAddOns().size());
//         assertNotNull(booking.getCreatedAt());
//         assertNotNull(booking.getUpdatedAt());
//         assertNull(booking.getDeletedAt());
//     }

//     @Test
//     void testSettersAndGetters() {
//         RentalBooking rb = new RentalBooking();
//         rb.setId("VR000002");
//         rb.setVehicleId("VEH0002");
//         rb.setVehicle(vehicle);
//         rb.setPickUpTime(LocalDateTime.of(2025, 1, 2, 10, 0));
//         rb.setDropOffTime(LocalDateTime.of(2025, 1, 5, 10, 0));
//         rb.setPickUpLocation("Surabaya");
//         rb.setDropOffLocation("Malang");
//         rb.setCapacityNeeded(6);
//         rb.setTransmissionNeeded("Manual");
//         rb.setTotalPrice(2000000.0);
//         rb.setIncludeDriver(false);
//         rb.setStatus("Ongoing");
//         rb.setListOfAddOns(List.of(addOn1));
//         rb.setCreatedAt(LocalDateTime.of(2025, 1, 1, 0, 0));
//         rb.setUpdatedAt(LocalDateTime.of(2025, 1, 2, 0, 0));
//         rb.setDeletedAt(LocalDateTime.of(2025, 1, 3, 0, 0));

//         assertEquals("VR000002", rb.getId());
//         assertEquals("VEH0002", rb.getVehicleId());
//         assertEquals(vehicle, rb.getVehicle());
//         assertEquals("Surabaya", rb.getPickUpLocation());
//         assertEquals("Malang", rb.getDropOffLocation());
//         assertEquals(6, rb.getCapacityNeeded());
//         assertEquals("Manual", rb.getTransmissionNeeded());
//         assertEquals(2000000.0, rb.getTotalPrice());
//         assertFalse(rb.isIncludeDriver());
//         assertEquals("Ongoing", rb.getStatus());
//         assertEquals(1, rb.getListOfAddOns().size());
//         assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), rb.getCreatedAt());
//         assertEquals(LocalDateTime.of(2025, 1, 2, 0, 0), rb.getUpdatedAt());
//         assertEquals(LocalDateTime.of(2025, 1, 3, 0, 0), rb.getDeletedAt());
//     }

//     @Test
//     void testOnCreateSetsTimestamps() {
//         RentalBooking rb = new RentalBooking();
//         assertNull(rb.getCreatedAt());
//         assertNull(rb.getUpdatedAt());

//         rb.onCreate();

//         assertNotNull(rb.getCreatedAt());
//         assertNotNull(rb.getUpdatedAt());
//         assertTrue(rb.getUpdatedAt().isAfter(rb.getCreatedAt().minusSeconds(1)));
//     }

//     @Test
//     void testOnUpdateChangesUpdatedAtOnly() throws InterruptedException {
//         RentalBooking rb = new RentalBooking();
//         rb.onCreate();
//         LocalDateTime before = rb.getUpdatedAt();

//         Thread.sleep(10);
//         rb.onUpdate();

//         assertTrue(rb.getUpdatedAt().isAfter(before));
//         assertEquals(rb.getCreatedAt().getClass(), LocalDateTime.class);
//     }

//     @Test
//     void testEqualsAndHashCode() {
//         RentalBooking rb2 = booking;
//         assertEquals(booking, rb2);
//         assertEquals(booking.hashCode(), rb2.hashCode());
//     }

//     @Test
//     void testToStringContainsImportantFields() {
//         String s = booking.toString();
//         assertTrue(s.contains("VR000001"));
//         assertTrue(s.contains("Upcoming"));
//         assertTrue(s.contains("Jakarta"));
//     }
// }
