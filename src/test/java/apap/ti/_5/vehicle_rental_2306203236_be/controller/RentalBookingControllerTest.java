// package apap.ti._5.vehicle_rental_2306203236_be.controller;

// import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.CreateRentalBookingDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.ReadRentalBookingDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.UpdateRentalBookingDto;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
// import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalAddOnRepository;
// import apap.ti._5.vehicle_rental_2306203236_be.service.LocationService;
// import apap.ti._5.vehicle_rental_2306203236_be.service.RentalBookingService;
// import apap.ti._5.vehicle_rental_2306203236_be.service.VehicleService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDateTime;
// import java.util.*;

// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// class RentalBookingControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean private RentalBookingService bookingService;
//     @MockBean private VehicleService vehicleService;
//     @MockBean private RentalAddOnRepository addOnRepo;
//     @MockBean private LocationService locationService;

//     private RentalBooking booking;
//     private Vehicle vehicle;
//     private RentalVendor vendor;

//     @BeforeEach
//     void setup() {
//         vendor = RentalVendor.builder()
//                 .id(1)
//                 .name("PT Maju")
//                 .email("vendor@mail.com")
//                 .phone("081")
//                 .listOfLocations(List.of("Depok", "Jakarta"))
//                 .build();

//         vehicle = Vehicle.builder()
//                 .id("VH001")
//                 .rentalVendor(vendor)
//                 .rentalVendorId(1)
//                 .capacity(5)
//                 .transmission("Automatic")
//                 .status("Available")
//                 .location("Depok")
//                 .price(200000.0)
//                 .build();

//         booking = RentalBooking.builder()
//                 .id("BK001")
//                 .status("Upcoming")
//                 .vehicle(vehicle)
//                 .listOfAddOns(List.of())
//                 .build();

//         when(bookingService.getAllRentalBookingDto(any())).thenReturn(List.of(new ReadRentalBookingDto()));
//         when(bookingService.getRentalBooking("BK001")).thenReturn(booking);
//         when(bookingService.getRentalBooking("NOTFOUND")).thenReturn(null);
//         when(vehicleService.getAllVehicle(any(), any())).thenReturn(List.of(vehicle));
//         when(locationService.getAllProvinces()).thenReturn(List.of("Depok", "Jakarta"));
//         when(addOnRepo.findAll()).thenReturn(List.of(new RentalAddOn()));
//         when(bookingService.isVehicleAvailableDuringPeriod(any(), any(), any())).thenReturn(true);
//         when(bookingService.createRentalBooking(any())).thenReturn(booking);
//         when(bookingService.updateRentalBookingDetails(any())).thenReturn(booking);
//         when(bookingService.updateRentalBookingStatus(any(), any())).thenReturn(booking);
//         when(bookingService.updateRentalBookingAddOn(any())).thenReturn(booking);
//         when(bookingService.deleteRentalBooking(any())).thenReturn(booking);
//         when(bookingService.getBookingStatistics(any(), anyInt())).thenReturn(List.of(new Object[]{"Jan", 5}));
//     }

//     // ---------- View All ----------
//     @Test
//     void testViewAllBookings() throws Exception {
//         mockMvc.perform(get("/bookings"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/view-all"))
//                 .andExpect(model().attributeExists("rentalBookings"));
//     }

//     // ---------- Detail ----------
//     @Test
//     void testDetailBookingFound() throws Exception {
//         mockMvc.perform(get("/bookings/BK001"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/detail"))
//                 .andExpect(model().attributeExists("rentalBooking"));
//     }

//     @Test
//     void testDetailBookingNotFound() throws Exception {
//         mockMvc.perform(get("/bookings/NOTFOUND"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("error/404"));
//     }

//     // ---------- Create Form ----------
//     @Test
//     void testCreateForm() throws Exception {
//         mockMvc.perform(get("/bookings/create"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/form-booking"))
//                 .andExpect(model().attribute("isEdit", false));
//     }

//     // ---------- Create Submit (validation fail pickup before now) ----------
//     @Test
//     void testCreateSubmitInvalidPickupTime() throws Exception {
//         CreateRentalBookingDto dto = new CreateRentalBookingDto();
//         dto.setPickUpTime(LocalDateTime.now().minusHours(1));
//         dto.setDropOffTime(LocalDateTime.now().plusHours(3));
//         mockMvc.perform(post("/bookings/create")
//                         .flashAttr("rentalBooking", dto))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/form-booking"));
//     }

//     // ---------- Create Submit success ----------
//     @Test
//     void testCreateSubmitSuccess() throws Exception {
//         CreateRentalBookingDto dto = new CreateRentalBookingDto();
//         dto.setPickUpLocation("Depok");
//         dto.setDropOffLocation("Jakarta");
//         dto.setPickUpTime(LocalDateTime.now().plusHours(2));
//         dto.setDropOffTime(LocalDateTime.now().plusDays(1));
//         dto.setCapacityNeeded(4);
//         dto.setTransmissionNeeded("Automatic");
//         mockMvc.perform(post("/bookings/create")
//                         .flashAttr("rentalBooking", dto))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/form-booking"))
//                 .andExpect(model().attributeExists("availableVehicles"));
//     }

//     // ---------- AddOns Form ----------
//     @Test
//     void testCreateAddOnsForm() throws Exception {
//         mockMvc.perform(post("/bookings/create/addons")
//                         .flashAttr("rentalBooking", new CreateRentalBookingDto()))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/form-addons"))
//                 .andExpect(model().attributeExists("addons"));
//     }

//     // ---------- Save Booking ----------
//     @Test
//     void testCreateBookingSaveSuccess() throws Exception {
//         mockMvc.perform(post("/bookings/create/save")
//                         .flashAttr("rentalBooking", new CreateRentalBookingDto()))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/bookings"));
//     }

//     @Test
//     void testCreateBookingSaveNull() throws Exception {
//         when(bookingService.createRentalBooking(any())).thenReturn(null);
//         mockMvc.perform(post("/bookings/create/save")
//                         .flashAttr("rentalBooking", new CreateRentalBookingDto()))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/form-booking"));
//     }

//     // ---------- Update Details ----------
//     @Test
//     void testUpdateDetailsFormFound() throws Exception {
//         mockMvc.perform(get("/bookings/BK001/update-details"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/form-booking"));
//     }

//     @Test
//     void testUpdateDetailsFormNotFound() throws Exception {
//         mockMvc.perform(get("/bookings/NOTFOUND/update-details"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("error/404"));
//     }

//     // ---------- Update Status ----------
//     @Test
//     void testUpdateStatusForm() throws Exception {
//         mockMvc.perform(get("/bookings/BK001/update-status"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/form-status"));
//     }

//     @Test
//     void testUpdateStatusBookingNull() throws Exception {
//         when(bookingService.getRentalBooking("X")).thenReturn(null);
//         mockMvc.perform(get("/bookings/X/update-status"))
//                 .andExpect(status().is3xxRedirection());
//     }

//     @Test
//     void testUpdateStatusSubmitSuccess() throws Exception {
//         mockMvc.perform(put("/bookings/update-status")
//                         .param("id", "BK001")
//                         .param("newStatus", "Done"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/bookings/BK001"));
//     }

//     @Test
//     void testUpdateStatusSubmitFailed() throws Exception {
//         when(bookingService.updateRentalBookingStatus(any(), any())).thenReturn(null);
//         mockMvc.perform(put("/bookings/update-status")
//                         .param("id", "BK001")
//                         .param("newStatus", "Fail"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/bookings/BK001/update-status"));
//     }

//     // ---------- Delete ----------
//     @Test
//     void testDeleteConfirmForm() throws Exception {
//         mockMvc.perform(get("/bookings/BK001/delete"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/confirm-delete"));
//     }

//     @Test
//     void testDeleteConfirmFormForbidden() throws Exception {
//         booking.setStatus("Done");
//         mockMvc.perform(get("/bookings/BK001/delete"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("error/403"));
//     }

//     @Test
//     void testDeleteConfirmed() throws Exception {
//         mockMvc.perform(delete("/bookings/BK001/delete"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/bookings"));
//     }

//     // ---------- Chart ----------
//     @Test
//     void testChartPage() throws Exception {
//         mockMvc.perform(get("/bookings/chart"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("booking/booking-chart"))
//                 .andExpect(model().attributeExists("period", "year"));
//     }

//     @Test
//     void testApiChart() throws Exception {
//         mockMvc.perform(get("/bookings/api/chart"))
//                 .andExpect(status().isOk());
//         Mockito.verify(bookingService).getBookingStatistics(any(), anyInt());
//     }
// }
