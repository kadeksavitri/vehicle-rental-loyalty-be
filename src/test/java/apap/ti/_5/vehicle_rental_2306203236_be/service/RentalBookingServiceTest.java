package apap.ti._5.vehicle_rental_2306203236_be.service;

import apap.ti._5.vehicle_rental_2306203236_be.model.*;
import apap.ti._5.vehicle_rental_2306203236_be.repository.*;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.rentalbooking.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restservice.RentalBookingRestService;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class RentalBookingRestServiceTest {

    @Mock private RentalBookingRepository bookingRepo;
    @Mock private VehicleRepository vehicleRepo;
    @Mock private RentalAddOnRepository addOnRepo;
    @Mock private IdGenerator idGenerator;

    @InjectMocks
    private RentalBookingRestService service;

    private Vehicle vehicle;
    private RentalAddOn addOn;
    private RentalBooking booking;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicle = Vehicle.builder()
                .id("VEH0001")
                .brand("Toyota")
                .type("SUV")
                .price(200000.0)
                .status("Available")
                .location("Depok")
                .build();

        addOn = RentalAddOn.builder()
                .id(UUID.randomUUID())
                .name("GPS")
                .price(50000.0)
                .build();

        booking = RentalBooking.builder()
                .id("VR000001")
                .vehicle(vehicle)
                .vehicleId("VEH0001")
                .pickUpTime(LocalDateTime.now().plusHours(1))
                .dropOffTime(LocalDateTime.now().plusHours(25))
                .pickUpLocation("Depok")
                .dropOffLocation("Depok")
                .capacityNeeded(4)
                .transmissionNeeded("Automatic")
                .includeDriver(true)
                .status("Upcoming")
                .totalPrice(500000.0)
                .listOfAddOns(List.of(addOn))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /* ========================= CREATE ========================= */
    @Test
    void testCreateRentalBooking_Success() {
        CreateRentalBookingRequestDTO dto = CreateRentalBookingRequestDTO.builder()
                .vehicleId("VEH0001")
                .pickUpTime(LocalDateTime.now())
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .pickUpLocation("Depok")
                .dropOffLocation("Depok")
                .capacityNeeded(4)
                .transmissionNeeded("Automatic")
                .includeDriver(true)
                .ListOfAddOns(List.of(addOn.getId().toString()))
                .totalPrice(1000000.0)
                .build();

        when(vehicleRepo.findById("VEH0001")).thenReturn(Optional.of(vehicle));
        when(bookingRepo.findLastestRentalBookingIncludingDeleted()).thenReturn(booking);
        when(idGenerator.generateRentalBookingId(any())).thenReturn("VR000002");
        when(addOnRepo.findById(any(UUID.class))).thenReturn(Optional.of(addOn));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalBookingResponseDTO result = service.createRentalBooking(dto);
        assertNotNull(result);
        assertEquals("VR000002", result.getId());
        assertTrue(result.getTotalPrice() > 0);
    }

    @Test
    void testCreateRentalBooking_VehicleNotFound() {
        CreateRentalBookingRequestDTO dto = CreateRentalBookingRequestDTO.builder()
                .vehicleId("NOTFOUND")
                .pickUpTime(LocalDateTime.now())
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .pickUpLocation("Depok")
                .dropOffLocation("Depok")
                .capacityNeeded(4)
                .transmissionNeeded("Automatic")
                .includeDriver(false)
                .totalPrice(0.0)
                .build();

        when(vehicleRepo.findById("NOTFOUND")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.createRentalBooking(dto));
    }

    @Test
    void testCreateRentalBooking_AddOnNotFound() {
        CreateRentalBookingRequestDTO dto = CreateRentalBookingRequestDTO.builder()
                .vehicleId("VEH0001")
                .pickUpTime(LocalDateTime.now())
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .pickUpLocation("Depok")
                .dropOffLocation("Depok")
                .capacityNeeded(4)
                .transmissionNeeded("Automatic")
                .includeDriver(true)
                .ListOfAddOns(List.of(UUID.randomUUID().toString()))
                .totalPrice(100000.0)
                .build();

        when(vehicleRepo.findById("VEH0001")).thenReturn(Optional.of(vehicle));
        when(addOnRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.createRentalBooking(dto));
    }

    /* ========================= GET ALL ========================= */
    @Test
    void testGetAllRentalBookings() {
        when(bookingRepo.findAllByDeletedAtIsNullOrderByCreatedAtDesc()).thenReturn(List.of(booking));
        List<RentalBookingResponseDTO> result = service.getAllRentalBookings();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllRentalBookingsByKeyword_Found() {
        when(bookingRepo.findByIdContainingIgnoreCaseOrVehicle_IdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(any(), any(), any()))
                .thenReturn(List.of(booking));
        List<RentalBookingResponseDTO> result = service.getAllRentalBookingsByKeyword("VR");
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllRentalBookingsByKeyword_EmptyKeyword() {
        when(bookingRepo.findAllByDeletedAtIsNullOrderByCreatedAtDesc()).thenReturn(List.of(booking));
        List<RentalBookingResponseDTO> result = service.getAllRentalBookingsByKeyword("");
        assertEquals(1, result.size());
    }

    /* ========================= GET ONE ========================= */
    @Test
    void testGetRentalBooking_Success() {
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        RentalBookingResponseDTO result = service.getRentalBooking("VR000001");
        assertNotNull(result);
    }

    @Test
    void testGetRentalBooking_NotFound() {
        when(bookingRepo.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getRentalBooking("X"));
    }

    /* ========================= UPDATE DETAILS ========================= */
    @Test
    void testUpdateRentalBookingDetails_Success() {
        UpdateRentalBookingRequestDTO dto = UpdateRentalBookingRequestDTO.builder()
                .id("VR000001")
                .vehicleId("VEH0001")
                .pickUpTime(LocalDateTime.now())
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .pickUpLocation("Depok")
                .dropOffLocation("Depok")
                .capacityNeeded(4)
                .transmissionNeeded("Automatic")
                .includeDriver(true)
                .status("Upcoming")
                .totalPrice(500000.0)
                .build();

        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(vehicleRepo.findById("VEH0001")).thenReturn(Optional.of(vehicle));
        when(addOnRepo.findById(any(UUID.class))).thenReturn(Optional.of(addOn));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalBookingResponseDTO result = service.updateRentalBookingDetails(dto);
        assertNotNull(result);
    }

    @Test
    void testUpdateRentalBookingDetails_NotUpcoming() {
        booking.setStatus("Done");
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        UpdateRentalBookingRequestDTO dto = UpdateRentalBookingRequestDTO.builder()
                .id("VR000001").vehicleId("VEH0001").status("Done")
                .pickUpTime(LocalDateTime.now()).dropOffTime(LocalDateTime.now().plusDays(1))
                .pickUpLocation("Depok").dropOffLocation("Depok")
                .capacityNeeded(2).transmissionNeeded("Auto")
                .totalPrice(0.0).build();
        assertThrows(IllegalStateException.class, () -> service.updateRentalBookingDetails(dto));
    }

    /* ========================= UPDATE STATUS ========================= */
    @Test
    void testUpdateStatus_UpcomingToOngoing_Success() {
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(bookingRepo.findAllByVehicleAndDeletedAtIsNull(vehicle)).thenReturn(List.of());
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalBookingResponseDTO result = service.updateRentalBookingStatus("VR000001", "Ongoing");
        assertEquals("Ongoing", result.getStatus());
    }

    @Test
    void testUpdateStatus_OngoingToDone_WithPenalty() {
        booking.setStatus("Ongoing");
        booking.setDropOffTime(LocalDateTime.now().minusHours(2));

        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        RentalBookingResponseDTO result = service.updateRentalBookingStatus("VR000001", "Done");
        assertEquals("Done", result.getStatus());
        assertTrue(result.getTotalPrice() >= 0);
    }

    @Test
    void testUpdateStatus_InvalidTransition() {
        booking.setStatus("Upcoming");
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        assertThrows(IllegalStateException.class, () -> service.updateRentalBookingStatus("VR000001", "Done"));
    }

    /* ========================= UPDATE ADDON ========================= */
    @Test
    void testUpdateAddOn_Success() {
        UpdateRentalBookingAddOnRequestDTO dto = new UpdateRentalBookingAddOnRequestDTO("VR000001", List.of(addOn.getId().toString()));
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(addOnRepo.findById(any(UUID.class))).thenReturn(Optional.of(addOn));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalBookingResponseDTO result = service.updateRentalBookingAddOn(dto);
        assertNotNull(result);
        assertTrue(result.getTotalPrice() > 0);
    }

    @Test
    void testUpdateAddOn_NotUpcoming() {
        booking.setStatus("Done");
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        UpdateRentalBookingAddOnRequestDTO dto = new UpdateRentalBookingAddOnRequestDTO("VR000001", List.of());
        assertThrows(IllegalStateException.class, () -> service.updateRentalBookingAddOn(dto));
    }

    /* ========================= DELETE ========================= */
    @Test
    void testDeleteRentalBooking_Success() {
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        RentalBookingResponseDTO result = service.deleteRentalBooking(new DeleteRentalBookingRequestDTO("VR000001"));
        assertEquals("Done", result.getStatus());
    }

    @Test
    void testDeleteRentalBooking_InvalidStatus() {
        booking.setStatus("Ongoing");
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        assertThrows(IllegalStateException.class, () -> service.deleteRentalBooking(new DeleteRentalBookingRequestDTO("VR000001")));
    }

    /* ========================= STATISTICS ========================= */
    @Test
    void testGetRentalBookingStatistics_Quarterly() {
        booking.setCreatedAt(LocalDateTime.of(2025, 3, 1, 0, 0));
        when(bookingRepo.findAllByDeletedAtIsNull()).thenReturn(List.of(booking));
        ChartRentalBookingRequestDTO chart = new ChartRentalBookingRequestDTO("Quarterly", 2025);
        List<Object[]> result = service.getRentalBookingStatistics(chart);
        assertEquals(4, result.size());
    }

    @Test
    void testGetRentalBookingStatistics_Monthly() {
        booking.setCreatedAt(LocalDateTime.of(2025, 5, 1, 0, 0));
        when(bookingRepo.findAllByDeletedAtIsNull()).thenReturn(List.of(booking));
        ChartRentalBookingRequestDTO chart = new ChartRentalBookingRequestDTO("Monthly", 2025);
        List<Object[]> result = service.getRentalBookingStatistics(chart);
        assertEquals(12, result.size());
    }
}
