package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.AddVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleRestServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private RentalVendorRepository rentalVendorRepository;

    @Mock
    private RentalBookingRepository rentalBookingRepository;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private VehicleRestServiceImpl vehicleRestService;

    private RentalVendor vendor;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vendor = RentalVendor.builder()
                .id(1)
                .name("Vendor A")
                .email("vendor@example.com")
                .phone("0812345678")
                .listOfLocations(List.of("Depok", "Jakarta"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        vehicle = Vehicle.builder()
                .id("VEH0001")
                .rentalVendor(vendor)
                .rentalVendorId(1)
                .type("Car")
                .brand("Toyota")
                .model("Avanza")
                .productionYear(2022)
                .location("Depok")
                .licensePlate("B1234XYZ")
                .transmission("Automatic")
                .fuelType("Gasoline")
                .capacity(5)
                .price(500000.0)
                .status("Available")
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ---------- CREATE ----------

    @Test
    void testCreateVehicle_Success() {
        AddVehicleRequestDTO dto = new AddVehicleRequestDTO();
        dto.setRentalVendorId(1);
        dto.setType("Car");
        dto.setBrand("Toyota");
        dto.setModel("Avanza");
        dto.setProductionYear(2022);
        dto.setLocation("Depok");
        dto.setLicensePlate("B1234XYZ");
        dto.setTransmission("Automatic");
        dto.setFuelType("Gasoline");
        dto.setCapacity(5);
        dto.setPrice(500000.0);

        when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));
        when(vehicleRepository.existsByLicensePlate("B1234XYZ")).thenReturn(false);
        when(vehicleRepository.findLatestVehicleIncludingDeleted()).thenReturn(vehicle);
        when(idGenerator.generateVehicleId("VEH0001")).thenReturn("VEH0002");
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        VehicleResponseDTO result = vehicleRestService.createVehicle(dto);

        assertNotNull(result);
        assertEquals("VEH0002", result.getId());
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void testCreateVehicle_VendorNotFound_ThrowsException() {
        AddVehicleRequestDTO dto = new AddVehicleRequestDTO();
        dto.setRentalVendorId(99);
        when(rentalVendorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> vehicleRestService.createVehicle(dto));
    }

    @Test
    void testCreateVehicle_InvalidYear_ThrowsException() {
        AddVehicleRequestDTO dto = new AddVehicleRequestDTO();
        dto.setRentalVendorId(1);
        dto.setProductionYear(LocalDateTime.now().getYear() + 1);
        when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));

        assertThrows(IllegalArgumentException.class, () -> vehicleRestService.createVehicle(dto));
    }

    @Test
    void testCreateVehicle_LocationNotInVendor_ThrowsException() {
        AddVehicleRequestDTO dto = new AddVehicleRequestDTO();
        dto.setRentalVendorId(1);
        dto.setProductionYear(2022);
        dto.setLocation("Bandung");
        when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));

        assertThrows(IllegalArgumentException.class, () -> vehicleRestService.createVehicle(dto));
    }

    @Test
    void testCreateVehicle_DuplicateLicense_ThrowsException() {
        AddVehicleRequestDTO dto = new AddVehicleRequestDTO();
        dto.setRentalVendorId(1);
        dto.setProductionYear(2022);
        dto.setLocation("Depok");
        dto.setLicensePlate("B1234XYZ");
        when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));
        when(vehicleRepository.existsByLicensePlate("B1234XYZ")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> vehicleRestService.createVehicle(dto));
    }

    // ---------- GET ALL ----------

    @Test
    void testGetAllVehicle_Success() {
        when(vehicleRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(vehicle));

        List<VehicleResponseDTO> result = vehicleRestService.getAllVehicle();

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void testGetAllVehicleByKeywordAndType_TypeAndKeyword() {
        when(vehicleRepository.findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(List.of(vehicle));

        List<VehicleResponseDTO> result = vehicleRestService.getAllVehicleByKeywordAndType("Toyota", "Car");

        assertEquals(1, result.size());
        verify(vehicleRepository).findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testGetAllVehicleByKeywordAndType_EmptyFilter() {
        when(vehicleRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(vehicle));

        List<VehicleResponseDTO> result = vehicleRestService.getAllVehicleByKeywordAndType(null, null);

        assertEquals(1, result.size());
        verify(vehicleRepository).findAllByDeletedAtIsNull();
    }

    // ---------- GET BY ID ----------

    @Test
    void testGetVehicle_Found() {
        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));

        VehicleResponseDTO result = vehicleRestService.getVehicle("VEH0001");

        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
    }

    @Test
    void testGetVehicle_NotFound_ReturnsNull() {
        when(vehicleRepository.findById("VEH9999")).thenReturn(Optional.empty());

        VehicleResponseDTO result = vehicleRestService.getVehicle("VEH9999");

        assertNull(result);
    }

    // ---------- UPDATE ----------

    @Test
    void testUpdateVehicle_Success() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setId("VEH0001");
        dto.setRentalVendorId(1);
        dto.setType("Car");
        dto.setBrand("Honda");
        dto.setModel("Jazz");
        dto.setLocation("Depok");
        dto.setLicensePlate("B5555ZZZ");
        dto.setStatus("Available");

        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));
        when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        VehicleResponseDTO result = vehicleRestService.updateVehicle(dto);

        assertEquals("Honda", result.getBrand());
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void testUpdateVehicle_NotFound_ReturnsNull() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setId("VEH9999");
        when(vehicleRepository.findById("VEH9999")).thenReturn(Optional.empty());

        VehicleResponseDTO result = vehicleRestService.updateVehicle(dto);
        assertNull(result);
    }

    @Test
    void testUpdateVehicle_VendorNotFound_ThrowsException() {
        UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
        dto.setId("VEH0001");
        dto.setRentalVendorId(2);
        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));
        when(rentalVendorRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> vehicleRestService.updateVehicle(dto));
    }

    // ---------- DELETE ----------

    @Test
    void testDeleteVehicle_Success() {
        vehicle.setStatus("Available");
        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));
        when(rentalBookingRepository.existsByVehicleAndStatusIn(any(Vehicle.class), anyList())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        VehicleResponseDTO result = vehicleRestService.deleteVehicle("VEH0001");

        assertEquals("Unavailable", result.getStatus());
        assertNotNull(result.getId());
    }

    @Test
    void testDeleteVehicle_HasActiveBooking_ThrowsException() {
        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));
        when(rentalBookingRepository.existsByVehicleAndStatusIn(any(Vehicle.class), anyList())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> vehicleRestService.deleteVehicle("VEH0001"));
    }

    @Test
    void testDeleteVehicle_NotFound_ReturnsNull() {
        when(vehicleRepository.findById("VEH9999")).thenReturn(Optional.empty());
        VehicleResponseDTO result = vehicleRestService.deleteVehicle("VEH9999");
        assertNull(result);
    }

    @Test
    void testDeleteVehicle_StatusInUse_ThrowsException() {
        vehicle.setStatus("In Use");
        when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));
        when(rentalBookingRepository.existsByVehicleAndStatusIn(any(Vehicle.class), anyList())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> vehicleRestService.deleteVehicle("VEH0001"));
    }
}
