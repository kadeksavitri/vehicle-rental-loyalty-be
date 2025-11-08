package apap.ti._5.vehicle_rental_2306203236_be.service;

import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.CreateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.UpdateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.model.*;
import apap.ti._5.vehicle_rental_2306203236_be.repository.*;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalBookingServiceImplTest {

    @Mock private RentalBookingRepository bookingRepo;
    @Mock private VehicleRepository vehicleRepo;
    @Mock private RentalAddOnRepository addOnRepo;
    @Mock private IdGenerator idGenerator;
    @InjectMocks private RentalBookingServiceImpl service;

    private Vehicle vehicle;
    private RentalBooking booking;
    private RentalVendor vendor;
    private RentalAddOn addOn;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        vendor = RentalVendor.builder()
                .id(1)
                .name("Vendor A")
                .listOfLocations(List.of("Depok", "Jakarta"))
                .build();

        vehicle = Vehicle.builder()
                .id("VEH001")
                .brand("Toyota")
                .type("SUV")
                .price(200000.0)
                .status("Available")
                .location("Depok")
                .rentalVendor(vendor)
                .build();

        addOn = RentalAddOn.builder()
                .id(UUID.randomUUID())
                .name("GPS")
                .price(50000.0)
                .build();

        booking = RentalBooking.builder()
                .id("VR000001")
                .vehicle(vehicle)
                .vehicleId("VEH001")
                .pickUpTime(LocalDateTime.now().plusHours(1))
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .pickUpLocation("Depok")
                .dropOffLocation("Jakarta")
                .includeDriver(true)
                .status("Upcoming")
                .totalPrice(300000.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /* ===================== getAllRentalBooking ===================== */
    @Test
    void testGetAllRentalBooking_NoKeyword() {
        when(bookingRepo.findAllByDeletedAtIsNullOrderByCreatedAtDesc()).thenReturn(List.of(booking));
        assertEquals(1, service.getAllRentalBooking(null).size());
    }

    @Test
    void testGetAllRentalBooking_WithKeyword() {
        when(bookingRepo.findByIdContainingIgnoreCaseOrVehicle_IdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(any(), any(), any()))
                .thenReturn(List.of(booking));
        assertEquals(1, service.getAllRentalBooking("VR000001").size());
    }

    /* ===================== createRentalBooking ===================== */
    @Test
    void testCreateRentalBooking_Success() {
        CreateRentalBookingDto dto = CreateRentalBookingDto.builder()
                .vehicleId("VEH001")
                .pickUpTime(LocalDateTime.now())
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .pickUpLocation("Depok")
                .dropOffLocation("Jakarta")
                .includeDriver(true)
                .listOfAddOns(List.of(addOn.getId().toString()))
                .build();

        when(vehicleRepo.findById("VEH001")).thenReturn(Optional.of(vehicle));
        when(bookingRepo.findLastestRentalBookingIncludingDeleted()).thenReturn(booking);
        when(idGenerator.generateRentalBookingId(any())).thenReturn("VR000002");
        when(addOnRepo.findAll()).thenReturn(List.of(addOn));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalBooking created = service.createRentalBooking(dto);
        assertNotNull(created);
        assertEquals("VR000002", created.getId());
    }

    @Test
    void testCreateRentalBooking_VehicleNotFound() {
        when(vehicleRepo.findById("X")).thenReturn(Optional.empty());
        CreateRentalBookingDto dto = CreateRentalBookingDto.builder()
                .vehicleId("X").build();
        assertNull(service.createRentalBooking(dto));
    }

    @Test
    void testCreateRentalBooking_InvalidLocationThrowsError() {
        vendor.setListOfLocations(List.of("Depok"));
        vehicle.setRentalVendor(vendor);

        CreateRentalBookingDto dto = CreateRentalBookingDto.builder()
                .vehicleId("VEH001")
                .pickUpLocation("Depok")
                .dropOffLocation("Surabaya")
                .pickUpTime(LocalDateTime.now())
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .build();

        when(vehicleRepo.findById("VEH001")).thenReturn(Optional.of(vehicle));
        when(bookingRepo.findLastestRentalBookingIncludingDeleted()).thenReturn(null);
        when(idGenerator.generateRentalBookingId(any())).thenReturn("VR000002");
        when(addOnRepo.findAll()).thenReturn(List.of(addOn));

        assertThrows(IllegalArgumentException.class, () -> service.createRentalBooking(dto));
    }

    /* ===================== updateRentalBookingDetails ===================== */
    @Test
    void testUpdateRentalBookingDetails_Success() {
        UpdateRentalBookingDto dto = UpdateRentalBookingDto.builder()
                .id("VR000001")
                .vehicleId("VEH001")
                .pickUpLocation("Depok")
                .dropOffLocation("Jakarta")
                .pickUpTime(LocalDateTime.now())
                .dropOffTime(LocalDateTime.now().plusDays(1))
                .status("Upcoming")
                .capacityNeeded(4)
                .transmissionNeeded("Auto")
                .includeDriver(true)
                .totalPrice(100000.0)
                .build();

        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(vehicleRepo.findById("VEH001")).thenReturn(Optional.of(vehicle));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalBooking updated = service.updateRentalBookingDetails(dto);
        assertNotNull(updated);
    }

    @Test
    void testUpdateRentalBookingDetails_NotFound() {
        when(bookingRepo.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());
        UpdateRentalBookingDto dto = UpdateRentalBookingDto.builder().id("X").build();
        assertNull(service.updateRentalBookingDetails(dto));
    }

    /* ===================== updateRentalBookingStatus ===================== */
    @Test
    void testUpdateStatus_UpcomingToOngoing_Valid() {
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(bookingRepo.findAllByVehicleAndDeletedAtIsNull(vehicle)).thenReturn(List.of());
        when(vehicleRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        booking.setPickUpTime(LocalDateTime.now().minusMinutes(10));
        booking.setDropOffTime(LocalDateTime.now().plusHours(1));

        RentalBooking result = service.updateRentalBookingStatus("VR000001", "Ongoing");
        assertEquals("Ongoing", result.getStatus());
    }

    @Test
    void testUpdateStatus_OngoingToDone_WithPenalty() {
        booking.setStatus("Ongoing");
        booking.setDropOffTime(LocalDateTime.now().minusHours(2));
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(vehicleRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalBooking result = service.updateRentalBookingStatus("VR000001", "Done");
        assertEquals("Done", result.getStatus());
        assertTrue(result.getTotalPrice() > 0);
    }

    @Test
    void testUpdateStatus_InvalidTransition_ReturnsNull() {
        booking.setStatus("Done");
        when(bookingRepo.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(booking));
        assertNull(service.updateRentalBookingStatus("VR000001", "Ongoing"));
    }


    @Test
    void testUpdateAddOn_BookingNotFound() {
        when(bookingRepo.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());
        UpdateRentalBookingDto dto = UpdateRentalBookingDto.builder().id("X").build();
        assertNull(service.updateRentalBookingAddOn(dto));
    }

    /* ===================== deleteRentalBooking ===================== */
    @Test
    void testDeleteRentalBooking_Success() {
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        when(vehicleRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalBooking deleted = service.deleteRentalBooking("VR000001");
        assertEquals("Done", deleted.getStatus());
        assertNotNull(deleted.getDeletedAt());
    }

    @Test
    void testDeleteRentalBooking_NotUpcoming() {
        booking.setStatus("Ongoing");
        when(bookingRepo.findByIdAndDeletedAtIsNull("VR000001")).thenReturn(Optional.of(booking));
        assertNull(service.deleteRentalBooking("VR000001"));
    }

    /* ===================== isVehicleAvailableDuringPeriod ===================== */
    @Test
    void testIsVehicleAvailable_NoOverlap() {
        when(bookingRepo.findAllByVehicleAndDeletedAtIsNull(vehicle)).thenReturn(List.of(booking));
        boolean result = service.isVehicleAvailableDuringPeriod(vehicle,
                booking.getDropOffTime().plusHours(1),
                booking.getDropOffTime().plusDays(1));
        assertTrue(result);
    }

    @Test
    void testIsVehicleAvailable_Overlap() {
        when(bookingRepo.findAllByVehicleAndDeletedAtIsNull(vehicle)).thenReturn(List.of(booking));
        boolean result = service.isVehicleAvailableDuringPeriod(vehicle,
                booking.getPickUpTime().minusHours(1),
                booking.getDropOffTime());
        assertFalse(result);
    }

    /* ===================== getBookingStatistics ===================== */
    @Test
    void testGetBookingStatistics_Quarterly() {
        booking.setCreatedAt(LocalDateTime.of(2025, 3, 10, 0, 0));
        when(bookingRepo.findAllByDeletedAtIsNull()).thenReturn(List.of(booking));
        List<Object[]> result = service.getBookingStatistics("Quarterly", 2025);
        assertEquals(4, result.size());
    }

    @Test
    void testGetBookingStatistics_Monthly() {
        booking.setCreatedAt(LocalDateTime.of(2025, 7, 10, 0, 0));
        when(bookingRepo.findAllByDeletedAtIsNull()).thenReturn(List.of(booking));
        List<Object[]> result = service.getBookingStatistics("Monthly", 2025);
        assertEquals(12, result.size());
    }
}
