package apap.ti._5.vehicle_rental_2306203236_be.controller;

import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private RentalVendorRepository rentalVendorRepository;

    @MockBean
    private RentalBookingRepository rentalBookingRepository;

    @BeforeEach
    void setup() {
        when(vehicleRepository.count()).thenReturn(10L);
        when(rentalVendorRepository.count()).thenReturn(5L);
        when(rentalBookingRepository.count()).thenReturn(3L);
    }

    @Test
    void testHomePageReturnsCorrectViewAndModel() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("vehicleCount", 10L))
                .andExpect(model().attribute("vendorCount", 5L))
                .andExpect(model().attribute("bookingCount", 3L));
    }

    @Test
    void testHomePageWithZeroCounts() throws Exception {
        when(vehicleRepository.count()).thenReturn(0L);
        when(rentalVendorRepository.count()).thenReturn(0L);
        when(rentalBookingRepository.count()).thenReturn(0L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("vehicleCount", 0L))
                .andExpect(model().attribute("vendorCount", 0L))
                .andExpect(model().attribute("bookingCount", 0L));
    }
}
