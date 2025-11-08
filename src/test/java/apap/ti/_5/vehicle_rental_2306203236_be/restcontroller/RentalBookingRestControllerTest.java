package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.rentalbooking.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restservice.RentalBookingRestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalBookingRestControllerTest {

    @Mock
    private RentalBookingRestService rentalBookingRestService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RentalBookingRestController controller;

    private RentalBookingResponseDTO sampleBooking;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        sampleBooking = RentalBookingResponseDTO.builder()
                .id("VR000001")
                .vehicleId("VEH001")
                .vehicleBrand("Toyota")
                .vehicleType("SUV")
                .pickUpTime(LocalDateTime.now())
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .pickUpLocation("Jakarta")
                .dropOffLocation("Bandung")
                .capacityNeeded(4)
                .transmissionNeeded("Automatic")
                .includeDriver(true)
                .status("Upcoming")
                .totalPrice(1000000.0)
                .listOfAddOns(List.of("GPS"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // --- GET ALL ---
    @Test
    void testGetAllRentalBookings_NoKeyword() {
        when(rentalBookingRestService.getAllRentalBookings()).thenReturn(List.of(sampleBooking));

        ResponseEntity<BaseResponseDTO<List<RentalBookingResponseDTO>>> response = controller.getAllRentalBookings(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Data rental bookings berhasil diambil", response.getBody().getMessage());
    }

    @Test
    void testGetAllRentalBookings_WithKeyword() {
        when(rentalBookingRestService.getAllRentalBookingsByKeyword("test")).thenReturn(List.of(sampleBooking));

        ResponseEntity<BaseResponseDTO<List<RentalBookingResponseDTO>>> response = controller.getAllRentalBookings("test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void testGetAllRentalBookings_Exception() {
        when(rentalBookingRestService.getAllRentalBookings()).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<BaseResponseDTO<List<RentalBookingResponseDTO>>> response = controller.getAllRentalBookings(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Gagal"));
    }

    // --- GET BY ID ---
    @Test
    void testGetRentalBooking_Success() {
        when(rentalBookingRestService.getRentalBooking("VR000001")).thenReturn(sampleBooking);

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response = controller.getRentalBooking("VR000001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleBooking, response.getBody().getData());
    }

    @Test
    void testGetRentalBooking_NotFound() {
        when(rentalBookingRestService.getRentalBooking("INVALID")).thenReturn(null);

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response = controller.getRentalBooking("INVALID");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tidak ditemukan"));
    }

    @Test
    void testGetRentalBooking_Exception() {
        when(rentalBookingRestService.getRentalBooking("ERROR")).thenThrow(new RuntimeException("fail"));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response = controller.getRentalBooking("ERROR");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // --- CREATE ---
    @Test
    void testCreateRentalBooking_Success() {
        CreateRentalBookingRequestDTO dto = new CreateRentalBookingRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(rentalBookingRestService.createRentalBooking(dto)).thenReturn(sampleBooking);

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.createRentalBooking(dto, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Booking berhasil dibuat", response.getBody().getMessage());
    }

    @Test
    void testCreateRentalBooking_ValidationError() {
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "field", "error msg")));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.createRentalBooking(new CreateRentalBookingRequestDTO(), bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("error"));
    }

    @Test
    void testCreateRentalBooking_IllegalArgumentException() {
        CreateRentalBookingRequestDTO dto = new CreateRentalBookingRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(rentalBookingRestService.createRentalBooking(dto))
                .thenThrow(new IllegalArgumentException("Invalid"));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.createRentalBooking(dto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // --- UPDATE DETAILS ---
    @Test
    void testUpdateBookingDetails_Success() {
        UpdateRentalBookingRequestDTO dto = new UpdateRentalBookingRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(rentalBookingRestService.updateRentalBookingDetails(dto)).thenReturn(sampleBooking);

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.updateBookingDetails(dto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateBookingDetails_ValidationError() {
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "field", "error msg")));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.updateBookingDetails(new UpdateRentalBookingRequestDTO(), bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateBookingDetails_Exception() {
        UpdateRentalBookingRequestDTO dto = new UpdateRentalBookingRequestDTO();
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(rentalBookingRestService.updateRentalBookingDetails(dto))
                .thenThrow(new RuntimeException("DB fail"));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.updateBookingDetails(dto, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // --- UPDATE STATUS ---
    @Test
    void testUpdateBookingStatus_Success() {
        UpdateRentalBookingStatusRequestDTO dto = new UpdateRentalBookingStatusRequestDTO("VR000001", "Ongoing");
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(rentalBookingRestService.updateRentalBookingStatus(dto.getId(), dto.getNewStatus()))
                .thenReturn(sampleBooking);

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.updateBookingStatus(dto, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("berhasil"));
    }

    @Test
    void testUpdateBookingStatus_ValidationError() {
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "field", "bad")));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.updateBookingStatus(new UpdateRentalBookingStatusRequestDTO(), bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateBookingStatus_Exception() {
        UpdateRentalBookingStatusRequestDTO dto = new UpdateRentalBookingStatusRequestDTO("VR000001", "Ongoing");
        when(bindingResult.hasFieldErrors()).thenReturn(false);
        when(rentalBookingRestService.updateRentalBookingStatus(dto.getId(), dto.getNewStatus()))
                .thenThrow(new RuntimeException("error"));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.updateBookingStatus(dto, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // --- UPDATE ADDONS ---
    @Test
    void testUpdateAddOns_Success() {
        UpdateRentalBookingAddOnRequestDTO dto = new UpdateRentalBookingAddOnRequestDTO("VR000001", List.of("1"));
        when(rentalBookingRestService.updateRentalBookingAddOn(dto)).thenReturn(sampleBooking);

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response =
                controller.updateAddOns(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateAddOns_Exception() {
        UpdateRentalBookingAddOnRequestDTO dto = new UpdateRentalBookingAddOnRequestDTO("VR000001", List.of("1"));
        when(rentalBookingRestService.updateRentalBookingAddOn(dto)).thenThrow(new RuntimeException("fail"));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response = controller.updateAddOns(dto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // --- DELETE ---
    @Test
    void testDeleteBooking_Success() {
        DeleteRentalBookingRequestDTO dto = new DeleteRentalBookingRequestDTO("VR000001");
        when(rentalBookingRestService.deleteRentalBooking(dto)).thenReturn(sampleBooking);

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response = controller.deleteBooking(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteBooking_Exception() {
        DeleteRentalBookingRequestDTO dto = new DeleteRentalBookingRequestDTO("VR000001");
        when(rentalBookingRestService.deleteRentalBooking(dto)).thenThrow(new RuntimeException("fail"));

        ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> response = controller.deleteBooking(dto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


    @Test
    void testGetBookingChartData_Exception() {
        ChartRentalBookingRequestDTO chartRequest = new ChartRentalBookingRequestDTO("Quarterly", 2025);
        when(rentalBookingRestService.getRentalBookingStatistics(chartRequest))
                .thenThrow(new RuntimeException("error"));

        ResponseEntity<BaseResponseDTO<List<Object[]>>> response = controller.getBookingChartData(chartRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
