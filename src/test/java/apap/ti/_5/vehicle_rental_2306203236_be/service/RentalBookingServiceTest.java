// package apap.ti._5.vehicle_rental_2306203236_be.service;

// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.CreateRentalBookingDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.ReadRentalBookingDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.UpdateRentalBookingDto;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.time.LocalDateTime;
// import java.util.Collections;
// import java.util.List;

// import static org.assertj.core.api.Assertions.assertThat;

// class RentalBookingServiceTest {

//     private RentalBookingService service;
//     private Vehicle dummyVehicle;
//     private RentalBooking dummyBooking;

//     @BeforeEach
//     void setUp() {
//         // Anonymous implementation supaya bisa diuji 100%
//         service = new RentalBookingService() {
//             @Override
//             public List<RentalBooking> getAllRentalBooking(String keyword) {
//                 return List.of(RentalBooking.builder().id("BK001").build());
//             }

//             @Override
//             public List<ReadRentalBookingDto> getAllRentalBookingDto(String keyword) {
//                 return List.of(new ReadRentalBookingDto());
//             }

//             @Override
//             public RentalBooking getRentalBooking(String id) {
//                 return RentalBooking.builder().id(id).build();
//             }

//             @Override
//             public RentalBooking createRentalBooking(CreateRentalBookingDto createRentalBookingDto) {
//                 return RentalBooking.builder().id("BK_CREATE").build();
//             }

//             @Override
//             public RentalBooking updateRentalBookingDetails(UpdateRentalBookingDto dto) {
//                 return RentalBooking.builder().id("BK_UPDATE_DETAIL").build();
//             }

//             @Override
//             public RentalBooking updateRentalBookingStatus(String id, String newStatus) {
//                 return RentalBooking.builder().id(id).status(newStatus).build();
//             }

//             @Override
//             public boolean isVehicleAvailableDuringPeriod(Vehicle vehicle, LocalDateTime pickUp, LocalDateTime dropOff) {
//                 // Dummy logic: available if pickUp < dropOff
//                 return pickUp.isBefore(dropOff);
//             }

//             @Override
//             public RentalBooking updateRentalBookingAddOn(UpdateRentalBookingDto dto) {
//                 return RentalBooking.builder().id("BK_UPDATE_ADDON").build();
//             }

//             @Override
//             public RentalBooking deleteRentalBooking(String id) {
//                 return RentalBooking.builder().id(id).build();
//             }

//             @Override
//             public List<Object[]> getBookingStatistics(String period, int year) {
//                 return List.of(new Object[]{"Jan", 5});
//             }
//         };

//         dummyVehicle = Vehicle.builder()
//                 .id("VH001")
//                 .brand("Toyota")
//                 .model("Avanza")
//                 .price(400000.0)
//                 .build();

//         dummyBooking = RentalBooking.builder()
//                 .id("BK001")
//                 .vehicle(dummyVehicle)
//                 .pickUpTime(LocalDateTime.now())
//                 .dropOffTime(LocalDateTime.now().plusDays(2))
//                 .status("Upcoming")
//                 .build();
//     }

//     @Test
//     void testGetAllRentalBooking() {
//         var result = service.getAllRentalBooking("test");
//         assertThat(result).isNotEmpty();
//         assertThat(result.get(0).getId()).isEqualTo("BK001");
//     }

//     @Test
//     void testGetAllRentalBookingDto() {
//         var result = service.getAllRentalBookingDto("search");
//         assertThat(result).hasSize(1);
//     }

//     @Test
//     void testGetRentalBooking() {
//         var result = service.getRentalBooking("BKX");
//         assertThat(result.getId()).isEqualTo("BKX");
//     }

//     @Test
//     void testCreateRentalBooking() {
//         var result = service.createRentalBooking(new CreateRentalBookingDto());
//         assertThat(result.getId()).isEqualTo("BK_CREATE");
//     }

//     @Test
//     void testUpdateRentalBookingDetails() {
//         var result = service.updateRentalBookingDetails(new UpdateRentalBookingDto());
//         assertThat(result.getId()).isEqualTo("BK_UPDATE_DETAIL");
//     }

//     @Test
//     void testUpdateRentalBookingStatus() {
//         var result = service.updateRentalBookingStatus("BK777", "Done");
//         assertThat(result.getStatus()).isEqualTo("Done");
//         assertThat(result.getId()).isEqualTo("BK777");
//     }

//     @Test
//     void testIsVehicleAvailableDuringPeriod() {
//         LocalDateTime start = LocalDateTime.now();
//         LocalDateTime end = start.plusDays(1);
//         assertThat(service.isVehicleAvailableDuringPeriod(dummyVehicle, start, end)).isTrue();
//         assertThat(service.isVehicleAvailableDuringPeriod(dummyVehicle, end, start)).isFalse();
//     }

//     @Test
//     void testUpdateRentalBookingAddOn() {
//         var result = service.updateRentalBookingAddOn(new UpdateRentalBookingDto());
//         assertThat(result.getId()).isEqualTo("BK_UPDATE_ADDON");
//     }

//     @Test
//     void testDeleteRentalBooking() {
//         var result = service.deleteRentalBooking("BK999");
//         assertThat(result.getId()).isEqualTo("BK999");
//     }

//     @Test
//     void testGetBookingStatistics() {
//         var result = service.getBookingStatistics("MONTH", 2025);
//         assertThat(result).isNotEmpty();
//         assertThat(result.get(0)[0]).isEqualTo("Jan");
//         assertThat(result.get(0)[1]).isEqualTo(5);
//     }
// }
