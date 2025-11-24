// package apap.ti._5.vehicle_rental_2306203236_be.service;

// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.CreateVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.ReadVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.UpdateVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
// import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
// import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
// import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.*;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.time.LocalDateTime;
// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class VehicleServiceTest {

//     @Mock
//     private VehicleRepository vehicleRepository;

//     @Mock
//     private RentalVendorRepository rentalVendorRepository;

//     @Mock
//     private IdGenerator idGenerator;

//     @InjectMocks
//     private VehicleServiceImpl vehicleService;

//     private Vehicle vehicle;
//     private RentalVendor vendor;

//     @BeforeEach
//     void setup() {
//         vendor = RentalVendor.builder()
//                 .id(1)
//                 .name("Vendor A")
//                 .email("vendor@example.com")
//                 .phone("0812345678")
//                 .listOfLocations(List.of("Depok", "Jakarta"))
//                 .createdAt(LocalDateTime.now())
//                 .updatedAt(LocalDateTime.now())
//                 .build();

//         vehicle = Vehicle.builder()
//                 .id("VEH0001")
//                 .rentalVendor(vendor)
//                 .rentalVendorId(1)
//                 .brand("Toyota")
//                 .model("Avanza")
//                 .type("Car")
//                 .productionYear(2020)
//                 .location("Depok")
//                 .licensePlate("B1234XYZ")
//                 .transmission("Automatic")
//                 .fuelType("Gasoline")
//                 .capacity(5)
//                 .price(500000.0)
//                 .status("Available")
//                 .createdAt(LocalDateTime.now())
//                 .build();
//     }

//     // ---------- CREATE VEHICLE ----------

//     @Test
//     void testCreateVehicle_Success() {
//         CreateVehicleDto dto = new CreateVehicleDto();
//         dto.setRentalVendorId(1);
//         dto.setType("Car");
//         dto.setBrand("Toyota");
//         dto.setModel("Avanza");
//         dto.setProductionYear(2020);
//         dto.setLocation("Depok");
//         dto.setLicensePlate("B1234XYZ");
//         dto.setTransmission("Automatic");
//         dto.setFuelType("Gasoline");
//         dto.setCapacity(5);
//         dto.setPrice(500000.0);

//         when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));
//         when(vehicleRepository.existsByLicensePlate("B1234XYZ")).thenReturn(false);
//         when(vehicleRepository.findLatestVehicleIncludingDeleted()).thenReturn(vehicle);
//         when(idGenerator.generateVehicleId("VEH0001")).thenReturn("VEH0002");
//         when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

//         Vehicle result = vehicleService.createVehicle(dto);

//         assertNotNull(result);
//         assertEquals("VEH0002", result.getId());
//         assertEquals("Available", result.getStatus());
//         verify(vehicleRepository).save(any(Vehicle.class));
//     }

//     @Test
//     void testCreateVehicle_VendorNotFound() {
//         CreateVehicleDto dto = new CreateVehicleDto();
//         dto.setRentalVendorId(99);
//         when(rentalVendorRepository.findById(99)).thenReturn(Optional.empty());

//         Vehicle result = vehicleService.createVehicle(dto);
//         assertNull(result);
//     }

//     @Test
//     void testCreateVehicle_InvalidProductionYear_ThrowsException() {
//         CreateVehicleDto dto = new CreateVehicleDto();
//         dto.setRentalVendorId(1);
//         dto.setProductionYear(LocalDateTime.now().getYear() + 1);
//         when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));

//         assertThrows(IllegalArgumentException.class, () -> vehicleService.createVehicle(dto));
//     }

//     @Test
//     void testCreateVehicle_InvalidLocation_ThrowsException() {
//         CreateVehicleDto dto = new CreateVehicleDto();
//         dto.setRentalVendorId(1);
//         dto.setProductionYear(2020);
//         dto.setLocation("Bandung");
//         when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));

//         assertThrows(IllegalArgumentException.class, () -> vehicleService.createVehicle(dto));
//     }

//     @Test
//     void testCreateVehicle_DuplicatePlate_ThrowsException() {
//         CreateVehicleDto dto = new CreateVehicleDto();
//         dto.setRentalVendorId(1);
//         dto.setProductionYear(2020);
//         dto.setLocation("Depok");
//         dto.setLicensePlate("B1234XYZ");

//         when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));
//         when(vehicleRepository.existsByLicensePlate("B1234XYZ")).thenReturn(true);

//         assertThrows(IllegalArgumentException.class, () -> vehicleService.createVehicle(dto));
//     }

//     // ---------- GET VEHICLE ----------

//     @Test
//     void testGetVehicle_Found() {
//         when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));

//         Vehicle result = vehicleService.getVehicle("VEH0001");

//         assertNotNull(result);
//         assertEquals("Toyota", result.getBrand());
//     }

//     @Test
//     void testGetVehicle_NotFound() {
//         when(vehicleRepository.findById("VEH9999")).thenReturn(Optional.empty());

//         Vehicle result = vehicleService.getVehicle("VEH9999");
//         assertNull(result);
//     }

//     // ---------- UPDATE VEHICLE ----------

//     @Test
//     void testUpdateVehicle_Success() {
//         UpdateVehicleDto dto = new UpdateVehicleDto();
//         dto.setId("VEH0001");
//         dto.setRentalVendorId(1);
//         dto.setBrand("Honda");
//         dto.setModel("Jazz");
//         dto.setLocation("Depok");
//         dto.setLicensePlate("B7777ABC");
//         dto.setStatus("Available");

//         when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));
//         when(rentalVendorRepository.findById(1)).thenReturn(Optional.of(vendor));
//         when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

//         Vehicle result = vehicleService.updateVehicle(dto);

//         assertEquals("Honda", result.getBrand());
//         verify(vehicleRepository).save(any(Vehicle.class));
//     }

//     @Test
//     void testUpdateVehicle_VehicleNotFound_ReturnsNull() {
//         UpdateVehicleDto dto = new UpdateVehicleDto();
//         dto.setId("VEH9999");
//         when(vehicleRepository.findById("VEH9999")).thenReturn(Optional.empty());

//         Vehicle result = vehicleService.updateVehicle(dto);
//         assertNull(result);
//     }

//     @Test
//     void testUpdateVehicle_VendorNotFound_ReturnsNull() {
//         UpdateVehicleDto dto = new UpdateVehicleDto();
//         dto.setId("VEH0001");
//         dto.setRentalVendorId(2);
//         when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));
//         when(rentalVendorRepository.findById(2)).thenReturn(Optional.empty());

//         Vehicle result = vehicleService.updateVehicle(dto);
//         assertNull(result);
//     }

//     // ---------- DELETE VEHICLE ----------

//     @Test
//     void testDeleteVehicle_Success() {
//         vehicle.setStatus("Available");
//         when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));
//         when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

//         Vehicle deleted = vehicleService.deleteVehicle("VEH0001");

//         assertEquals("Unavailable", deleted.getStatus());
//         assertNotNull(deleted.getDeletedAt());
//     }

//     @Test
//     void testDeleteVehicle_NotFound_ReturnsNull() {
//         when(vehicleRepository.findById("VEH9999")).thenReturn(Optional.empty());
//         Vehicle result = vehicleService.deleteVehicle("VEH9999");
//         assertNull(result);
//     }

//     @Test
//     void testDeleteVehicle_InUse_ThrowsException() {
//         vehicle.setStatus("In Use");
//         when(vehicleRepository.findById("VEH0001")).thenReturn(Optional.of(vehicle));

//         assertThrows(IllegalArgumentException.class, () -> vehicleService.deleteVehicle("VEH0001"));
//     }

//     // ---------- GET ALL VEHICLES ----------

//     @Test
//     void testGetAllVehicle() {
//         when(vehicleRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(vehicle));

//         List<Vehicle> result = vehicleService.getAllVehicle();

//         assertEquals(1, result.size());
//         verify(vehicleRepository).findAllByDeletedAtIsNull();
//     }

//     // ---------- GET ALL VEHICLE DTO ----------

//     @Test
//     void testGetAllVehicleDto_NoFilter() {
//         when(vehicleRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(vehicle));

//         List<ReadVehicleDto> result = vehicleService.getAllVehicleDto(null, null);

//         assertEquals(1, result.size());
//         assertEquals("Toyota", result.get(0).getBrand());
//     }

//     @Test
//     void testGetAllVehicleDto_WithTypeAndKeyword() {
//         when(vehicleRepository.findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase(
//                 anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
//         )).thenReturn(List.of(vehicle));

//         List<ReadVehicleDto> result = vehicleService.getAllVehicleDto("Toyota", "Car");

//         assertEquals(1, result.size());
//         verify(vehicleRepository).findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase(
//                 anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
//     }
// }
