package apap.ti._5.vehicle_rental_2306203236_be.controller;

import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.CreateVehicleDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.ReadVehicleDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.UpdateVehicleDto;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @Mock
    private RentalVendorRepository rentalVendorRepository;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private VehicleController vehicleController;

    private Vehicle vehicle;
    private RentalVendor vendor;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);

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
            .rentalVendorId(1)
            .rentalVendor(vendor)
            .type("Car")
            .brand("Toyota")
            .model("Avanza")
            .productionYear(2022)
            .location("Depok")
            .licensePlate("B1234XYZ")
            .capacity(5)
            .transmission("Automatic")
            .fuelType("Gasoline")
            .price(500000.0)
            .status("Available")
            .createdAt(LocalDateTime.now())
            .build();
}


    // ----------- GET ALL -----------
    @Test
    void testViewAllVehicles() {
        List<ReadVehicleDto> dtoList = List.of(ReadVehicleDto.builder()
                .id("VEH0001")
                .brand("Toyota")
                .type("Car")
                .build());

        when(vehicleService.getAllVehicleDto(null, null)).thenReturn(dtoList);

        String view = vehicleController.viewAllVehicles(null, null, model);

        verify(model).addAttribute("vehicles", dtoList);
        verify(model).addAttribute("selectedKeyword", null);
        verify(model).addAttribute("selectedType", null);
        assertEquals("vehicle/view-all", view);
    }

    // ----------- GET DETAIL -----------
    @Test
    void testViewVehicleFound() {
        when(vehicleService.getVehicle("VEH0001")).thenReturn(vehicle);

        String view = vehicleController.viewVehicle("VEH0001", model);

        verify(model).addAttribute("vehicle", vehicle);
        assertEquals("vehicle/detail", view);
    }

    @Test
    void testViewVehicleNotFound() {
        when(vehicleService.getVehicle("VEH9999")).thenReturn(null);

        String view = vehicleController.viewVehicle("VEH9999", model);

        verify(model).addAttribute("title", "Vehicle not found");
        verify(model).addAttribute("message", "Vehicle with id VEH9999 not found");
        assertEquals("error/404", view);
    }

    // ----------- CREATE FORM -----------
    @Test
    void testCreateVehicleForm() {
        when(rentalVendorRepository.findAll()).thenReturn(List.of(vendor));

        String view = vehicleController.createVehicleForm(model);

        verify(model).addAttribute(eq("vehicle"), any(CreateVehicleDto.class));
        verify(model).addAttribute(eq("vendors"), anyList());
        verify(model).addAttribute(eq("rentalVendorLocations"), anyMap());
        verify(model).addAttribute("isEdit", false);
        assertEquals("vehicle/form", view);
    }

    // ----------- CREATE VEHICLE -----------
    @Test
    void testCreateVehicle_Success() {
        CreateVehicleDto dto = new CreateVehicleDto();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(vehicleService.createVehicle(dto)).thenReturn(vehicle);

        String view = vehicleController.createVehicle(dto, bindingResult, redirectAttributes, model);

        verify(redirectAttributes).addFlashAttribute("successMessage", "Successfully created new vehicle.");
        assertEquals("redirect:/vehicles", view);
    }

    @Test
    void testCreateVehicle_BindingError() {
        CreateVehicleDto dto = new CreateVehicleDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = vehicleController.createVehicle(dto, bindingResult, redirectAttributes, model);

        verify(model).addAttribute(eq("errorMessage"), anyString());
        assertEquals("vehicle/form", view);
    }

    @Test
    void testCreateVehicle_FailedService() {
        CreateVehicleDto dto = new CreateVehicleDto();
        when(bindingResult.hasErrors()).thenReturn(false);
        when(vehicleService.createVehicle(dto)).thenReturn(null);

        String view = vehicleController.createVehicle(dto, bindingResult, redirectAttributes, model);

        verify(model).addAttribute("errorMessage", "Failed to create new vehicle.");
        assertEquals("vehicle/form", view);
    }

    // ----------- UPDATE FORM -----------
    @Test
    void testUpdateForm_Success() {
        when(vehicleService.getVehicle("VEH0001")).thenReturn(vehicle);
        when(rentalVendorRepository.findAll()).thenReturn(List.of(vendor));

        String view = vehicleController.updateForm("VEH0001", model);

        verify(model).addAttribute("vehicle", vehicle);
        verify(model).addAttribute("vendors", List.of(vendor));
        verify(model).addAttribute("isEdit", true);
        verify(model).addAttribute("vehicleId", "VEH0001");
        assertEquals("vehicle/form", view);
    }

    @Test
    void testUpdateForm_NotFound() {
        when(vehicleService.getVehicle("VEH9999")).thenReturn(null);

        String view = vehicleController.updateForm("VEH9999", model);

        verify(model).addAttribute("title", "Vehicle not found");
        verify(model).addAttribute("message", "Vehicle with id VEH9999 not found");
        assertEquals("error/404", view);
    }

    @Test
    void testUpdateForm_InUse() {
        vehicle.setStatus("In Use");
        when(vehicleService.getVehicle("VEH0001")).thenReturn(vehicle);

        assertThrows(IllegalArgumentException.class, () -> vehicleController.updateForm("VEH0001", model));
    }

    // ----------- UPDATE VEHICLE -----------
    @Test
    void testUpdateVehicle_Success() {
        UpdateVehicleDto dto = new UpdateVehicleDto();
        dto.setId("VEH0001");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(vehicleService.updateVehicle(dto)).thenReturn(vehicle);

        String view = vehicleController.updateVehicle("VEH0001", dto, bindingResult, redirectAttributes, model);

        verify(redirectAttributes).addFlashAttribute("successMessage", "Successfully updated vehicle with id VEH0001.");
        assertEquals("redirect:/vehicles", view);
    }

    @Test
    void testUpdateVehicle_IdMismatch() {
        UpdateVehicleDto dto = new UpdateVehicleDto();
        dto.setId("DIFF");

        String view = vehicleController.updateVehicle("VEH0001", dto, bindingResult, redirectAttributes, model);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Invalid vehicle id.");
        assertEquals("redirect:/vehicles", view);
    }

    @Test
    void testUpdateVehicle_BindingError() {
        UpdateVehicleDto dto = new UpdateVehicleDto();
        dto.setId("VEH0001");
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = vehicleController.updateVehicle("VEH0001", dto, bindingResult, redirectAttributes, model);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Validation failed. Please check your input.");
        assertEquals("redirect:/vehicles/update/VEH0001", view);
    }

    // ----------- DELETE VEHICLE -----------
    @Test
    void testDeleteVehicle_Success() {
        when(vehicleService.getVehicle("VEH0001")).thenReturn(vehicle);
        when(vehicleService.deleteVehicle("VEH0001")).thenReturn(vehicle);

        String view = vehicleController.deleteVehicle("VEH0001", redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("successMessage", "Successfully delete vehicle with ID VEH0001");
        assertEquals("redirect:/vehicles", view);
    }

    @Test
    void testDeleteVehicle_NotFound() {
        when(vehicleService.getVehicle("VEH9999")).thenReturn(null);

        String view = vehicleController.deleteVehicle("VEH9999", redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage", "Vehicle with id VEH9999 not found.");
        assertEquals("redirect:/vehicles", view);
    }

    @Test
    void testDeleteVehicle_InUse() {
        vehicle.setStatus("In Use");
        when(vehicleService.getVehicle("VEH0001")).thenReturn(vehicle);

        String view = vehicleController.deleteVehicle("VEH0001", redirectAttributes);

        verify(redirectAttributes).addFlashAttribute("errorMessage",
                "Cannot delete vehicle that is currently rented or in use.");
        assertEquals("redirect:/vehicles", view);
    }
}
