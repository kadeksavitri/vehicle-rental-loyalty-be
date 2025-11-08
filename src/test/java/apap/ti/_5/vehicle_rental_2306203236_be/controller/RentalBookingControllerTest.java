package apap.ti._5.vehicle_rental_2306203236_be.controller;

import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.CreateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.UpdateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.ReadRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.model.*;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306203236_be.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalBookingController.class)
class RentalBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalBookingService bookingService;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private RentalAddOnRepository addOnRepository;

    @MockBean
    private LocationService locationService;

    private Vehicle vehicle;
    private RentalBooking booking;
    private RentalVendor vendor;
    private List<String> provinces;

    @BeforeEach
    void setup() {
        provinces = List.of("Depok", "Jakarta");

        vendor = new RentalVendor();
        vendor.setId(1);
        vendor.setName("VendorA");
        vendor.setListOfLocations(provinces);

        vehicle = new Vehicle();
        vehicle.setId("VEH001");
        vehicle.setBrand("Toyota");
        vehicle.setLocation("Depok");
        vehicle.setStatus("Available");
        vehicle.setTransmission("Automatic");
        vehicle.setCapacity(5);
        vehicle.setPrice(200000.0);
        vehicle.setRentalVendor(vendor);

        booking = new RentalBooking();
        booking.setId("VR001");
        booking.setVehicle(vehicle);
        booking.setVehicleId("VEH001");
        booking.setPickUpTime(LocalDateTime.now().plusDays(1));
        booking.setDropOffTime(LocalDateTime.now().plusDays(2));
        booking.setPickUpLocation("Depok");
        booking.setDropOffLocation("Jakarta");
        booking.setStatus("Upcoming");
        booking.setTotalPrice(500000.0);
    }


    // ---------------- DETAIL ----------------
    @Test
    void testDetailFound() throws Exception {
        when(bookingService.getRentalBooking("VR001")).thenReturn(booking);
        mockMvc.perform(get("/bookings/VR001"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/detail"))
                .andExpect(model().attributeExists("rentalBooking"))
                .andExpect(model().attributeExists("canUpdateDetails"))
                .andExpect(model().attributeExists("canCancel"));
    }

    @Test
    void testDetailNotFound() throws Exception {
        when(bookingService.getRentalBooking("NOTFOUND")).thenReturn(null);
        mockMvc.perform(get("/bookings/NOTFOUND"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"))
                .andExpect(model().attribute("message", "Rental Booking with id NOTFOUND not found"));
    }

    // ---------------- CREATE ----------------
    @Test
    void testCreateForm() throws Exception {
        when(locationService.getAllProvinces()).thenReturn(provinces);

        mockMvc.perform(get("/bookings/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/form-booking"))
                .andExpect(model().attributeExists("locations"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    void testCreateSubmit_InvalidTime() throws Exception {
        when(locationService.getAllProvinces()).thenReturn(provinces);
        CreateRentalBookingDto dto = new CreateRentalBookingDto();
        dto.setPickUpTime(LocalDateTime.now().minusHours(1));
        dto.setDropOffTime(LocalDateTime.now().plusHours(3));
        dto.setPickUpLocation("Depok");
        dto.setDropOffLocation("Jakarta");

        mockMvc.perform(post("/bookings/create").flashAttr("rentalBooking", dto))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/form-booking"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testCreateSubmit_NoAvailableVehicles() throws Exception {
        CreateRentalBookingDto dto = new CreateRentalBookingDto();
        dto.setPickUpTime(LocalDateTime.now().plusHours(1));
        dto.setDropOffTime(LocalDateTime.now().plusDays(1));
        dto.setPickUpLocation("Depok");
        dto.setDropOffLocation("Jakarta");
        dto.setTransmissionNeeded("Automatic");
        dto.setCapacityNeeded(4);

        when(locationService.getAllProvinces()).thenReturn(provinces);
        when(vehicleService.getAllVehicle()).thenReturn(List.of());
        mockMvc.perform(post("/bookings/create").flashAttr("rentalBooking", dto))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/form-booking"));
    }

    @Test
    void testCreateAddOnsForm() throws Exception {
        when(addOnRepository.findAll()).thenReturn(List.of(new RentalAddOn()));
        mockMvc.perform(post("/bookings/create/addons")
                        .flashAttr("rentalBooking", new CreateRentalBookingDto()))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/form-addons"));
    }

    @Test
    void testCreateSave_Success() throws Exception {
        when(bookingService.createRentalBooking(any())).thenReturn(booking);
        mockMvc.perform(post("/bookings/create/save")
                        .flashAttr("rentalBooking", new CreateRentalBookingDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings"));
    }

    @Test
    void testUpdateDetailsForm_Found() throws Exception {
        when(bookingService.getRentalBooking("VR001")).thenReturn(booking);
        when(locationService.getAllProvinces()).thenReturn(provinces);

        mockMvc.perform(get("/bookings/VR001/update-details"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/form-booking"));
    }

    @Test
    void testUpdateDetailsForm_NotFound() throws Exception {
        when(bookingService.getRentalBooking("X")).thenReturn(null);
        mockMvc.perform(get("/bookings/X/update-details"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    void testUpdateDetailsSave_Success() throws Exception {
        UpdateRentalBookingDto dto = new UpdateRentalBookingDto();
        dto.setId("VR001");
        when(bookingService.updateRentalBookingDetails(any())).thenReturn(booking);

        mockMvc.perform(put("/bookings/update-details").flashAttr("rentalBooking", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/VR001"));
    }

    @Test
    void testUpdateDetailsSave_Failed() throws Exception {
        UpdateRentalBookingDto dto = new UpdateRentalBookingDto();
        dto.setId("VR001");
        when(bookingService.updateRentalBookingDetails(any())).thenReturn(null);

        mockMvc.perform(put("/bookings/update-details").flashAttr("rentalBooking", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/VR001"));
    }

    // ---------------- UPDATE STATUS ----------------
    @Test
    void testUpdateStatusForm_Found() throws Exception {
        when(bookingService.getRentalBooking("VR001")).thenReturn(booking);
        mockMvc.perform(get("/bookings/VR001/update-status"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/form-status"));
    }

    @Test
    void testUpdateStatusForm_Done() throws Exception {
        booking.setStatus("Done");
        when(bookingService.getRentalBooking("VR001")).thenReturn(booking);
        mockMvc.perform(get("/bookings/VR001/update-status"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/VR001"));
    }

    @Test
    void testUpdateStatusSubmit_Success() throws Exception {
        when(bookingService.updateRentalBookingStatus(any(), any())).thenReturn(booking);
        mockMvc.perform(put("/bookings/update-status")
                        .param("id", "VR001").param("newStatus", "Ongoing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/VR001"));
    }

    @Test
    void testUpdateStatusSubmit_Failed() throws Exception {
        when(bookingService.updateRentalBookingStatus(any(), any())).thenReturn(null);
        mockMvc.perform(put("/bookings/update-status")
                        .param("id", "VR001").param("newStatus", "Done"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/VR001/update-status"));
    }

    // ---------------- UPDATE ADDONS ----------------
    @Test
    void testUpdateAddOnsForm_Success() throws Exception {
        when(bookingService.getRentalBooking("VR001")).thenReturn(booking);
        when(addOnRepository.findAll()).thenReturn(List.of(new RentalAddOn()));

        mockMvc.perform(get("/bookings/VR001/update-addons"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/form-addons"));
    }

    @Test
    void testUpdateAddOnsSubmit() throws Exception {
        mockMvc.perform(put("/bookings/update-addons")
                        .flashAttr("rentalBooking", new UpdateRentalBookingDto("VR001", "VEH001",
                                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                                "Depok", "Jakarta", 4, "Automatic", 300000.0,
                                List.of(), false, "Upcoming")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings/VR001"));
    }

    // ---------------- DELETE ----------------
    @Test
    void testDeleteBookingConfirm_Success() throws Exception {
        when(bookingService.getRentalBooking("VR001")).thenReturn(booking);
        mockMvc.perform(get("/bookings/VR001/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/confirm-delete"));
    }


    @Test
    void testDeleteBookingConfirmed() throws Exception {
        mockMvc.perform(delete("/bookings/VR001/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings"));
    }

    // ---------------- CHART ----------------
    @Test
    void testChartView() throws Exception {
        mockMvc.perform(get("/bookings/chart").param("period", "Monthly").param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/booking-chart"))
                .andExpect(model().attributeExists("period"))
                .andExpect(model().attributeExists("year"));
    }

}
