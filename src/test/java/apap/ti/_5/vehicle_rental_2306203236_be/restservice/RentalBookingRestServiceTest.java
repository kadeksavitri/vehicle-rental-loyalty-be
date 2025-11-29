// package apap.ti._5.vehicle_rental_2306203236_be.restservice;

// import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.*;
// import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.rentalbooking.RentalBookingResponseDTO;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.util.ArrayList;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// class RentalBookingRestServiceTest {

//     private RentalBookingRestService service;

//     // ✅ Dummy Implementation (langsung digunakan tanpa Mockito)
//     static class DummyRentalBookingRestService implements RentalBookingRestService {
//         @Override
//         public RentalBookingResponseDTO createRentalBooking(CreateRentalBookingRequestDTO createRentalBookingDto) {
//             var dto = new RentalBookingResponseDTO();
//             dto.setId("RB001");
//             return dto;
//         }

//         @Override
//         public List<RentalBookingResponseDTO> getAllRentalBookingsByKeyword(String keyword) {
//             return List.of(new RentalBookingResponseDTO());
//         }

//         @Override
//         public List<RentalBookingResponseDTO> getAllRentalBookings() {
//             return List.of(new RentalBookingResponseDTO(), new RentalBookingResponseDTO());
//         }

//         @Override
//         public RentalBookingResponseDTO getRentalBooking(String id) {
//             var dto = new RentalBookingResponseDTO();
//             dto.setId(id);
//             return dto;
//         }

//         @Override
//         public RentalBookingResponseDTO updateRentalBookingDetails(UpdateRentalBookingRequestDTO dto) {
//             var res = new RentalBookingResponseDTO();
//             res.setId("UPDATED");
//             return res;
//         }

//         @Override
//         public RentalBookingResponseDTO updateRentalBookingStatus(String id, String newStatus) {
//             var res = new RentalBookingResponseDTO();
//             res.setStatus(newStatus);
//             return res;
//         }

//         @Override
//         public RentalBookingResponseDTO updateRentalBookingAddOn(UpdateRentalBookingAddOnRequestDTO dto) {
//             var res = new RentalBookingResponseDTO();
//             res.setId("ADDON_UPDATED");
//             return res;
//         }

//         @Override
//         public RentalBookingResponseDTO deleteRentalBooking(DeleteRentalBookingRequestDTO dto) {
//             var res = new RentalBookingResponseDTO();
//             res.setId("DELETED");
//             return res;
//         }

//         @Override
//         public List<Object[]> getRentalBookingStatistics(ChartRentalBookingRequestDTO chartRequest) {
//             List<Object[]> chartData = new ArrayList<>();
//             chartData.add(new Object[]{"Jan", 10});
//             chartData.add(new Object[]{"Feb", 5});
//             return chartData;
//         }
//     }

//     @BeforeEach
//     void setup() {
//         service = new DummyRentalBookingRestService();
//     }

//     /* ========================== TESTS ========================== */

//     @Test
//     void testCreateRentalBooking_Success() {
//         var dto = new CreateRentalBookingRequestDTO();
//         var result = service.createRentalBooking(dto);
//         assertNotNull(result);
//         assertEquals("RB001", result.getId());
//     }

//     @Test
//     void testGetAllRentalBookingsByKeyword() {
//         var result = service.getAllRentalBookingsByKeyword("jakarta");
//         assertEquals(1, result.size());
//     }

//     @Test
//     void testGetAllRentalBookings() {
//         var result = service.getAllRentalBookings();
//         assertEquals(2, result.size());
//     }

//     @Test
//     void testGetRentalBooking() {
//         var result = service.getRentalBooking("RB001");
//         assertEquals("RB001", result.getId());
//     }

//     @Test
//     void testUpdateRentalBookingDetails() {
//         var dto = new UpdateRentalBookingRequestDTO();
//         var result = service.updateRentalBookingDetails(dto);
//         assertEquals("UPDATED", result.getId());
//     }

//     @Test
//     void testUpdateRentalBookingStatus() {
//         var result = service.updateRentalBookingStatus("RB001", "Done");
//         assertEquals("Done", result.getStatus());
//     }

//     @Test
//     void testUpdateRentalBookingAddOn() {
//         var dto = new UpdateRentalBookingAddOnRequestDTO();
//         var result = service.updateRentalBookingAddOn(dto);
//         assertEquals("ADDON_UPDATED", result.getId());
//     }

//     @Test
//     void testDeleteRentalBooking() {
//         var dto = new DeleteRentalBookingRequestDTO();
//         var result = service.deleteRentalBooking(dto);
//         assertEquals("DELETED", result.getId());
//     }

//     @Test
//     void testGetRentalBookingStatistics() {
//         var dto = new ChartRentalBookingRequestDTO();
//         var result = service.getRentalBookingStatistics(dto);
//         assertEquals(2, result.size());
//         assertEquals("Jan", result.get(0)[0]);
//         assertEquals(10, result.get(0)[1]);
//     }
// }
