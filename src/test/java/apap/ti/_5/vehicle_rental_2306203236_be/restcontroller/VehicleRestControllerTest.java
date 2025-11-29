// package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

// import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.AddVehicleRequestDTO;
// import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
// import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle.VehicleResponseDTO;

// import apap.ti._5.vehicle_rental_2306203236_be.restservice.VehicleRestService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.validation.BindingResult;
// import org.springframework.validation.FieldError;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


// import java.util.*;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(VehicleRestController.class)
// class VehicleRestControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private VehicleRestService vehicleRestService;

//     @Mock
//     private BindingResult bindingResult;

//     @Captor
//     ArgumentCaptor<AddVehicleRequestDTO> addCaptor;

//     private VehicleResponseDTO vehicleDTO;
//     private ObjectMapper objectMapper;

//     @BeforeEach
//     void setup() {
//         objectMapper = new ObjectMapper();
//         vehicleDTO = VehicleResponseDTO.builder()
//                 .id("VEH0001")
//                 .rentalVendorId(1)
//                 .rentalVendorName("Vendor A")
//                 .type("Car")
//                 .brand("Toyota")
//                 .model("Avanza")
//                 .productionYear(2020)
//                 .location("Depok")
//                 .licensePlate("B1234XYZ")
//                 .capacity(5)
//                 .transmission("Automatic")
//                 .fuelType("Gasoline")
//                 .price(500000.0)
//                 .status("Available")
//                 .build();
//     }

//     // ---------- GET ALL ----------
//     @Test
//     void testGetAllVehicles_Success_AllEmptyParams() throws Exception {
//         when(vehicleRestService.getAllVehicle()).thenReturn(List.of(vehicleDTO));

//         mockMvc.perform(get("/api/vehicles"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data[0].id").value("VEH0001"))
//                 .andExpect(jsonPath("$.message").value("Data kendaraan berhasil diambil"));
//     }

//     @Test
//     void testGetAllVehicles_WithKeywordAndType() throws Exception {
//         when(vehicleRestService.getAllVehicleByKeywordAndType("Avanza", "Car")).thenReturn(List.of(vehicleDTO));

//         mockMvc.perform(get("/api/vehicles")
//                         .param("keyword", "Avanza")
//                         .param("type", "Car"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data[0].brand").value("Toyota"));
//     }

//     @Test
//     void testGetAllVehicles_WithOnlyKeyword() throws Exception {
//         when(vehicleRestService.getAllVehicleByKeywordAndType("Avanza", "")).thenReturn(List.of(vehicleDTO));

//         mockMvc.perform(get("/api/vehicles")
//                         .param("keyword", "Avanza"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data[0].model").value("Avanza"));
//     }

//     @Test
//     void testGetAllVehicles_ExceptionThrown() throws Exception {
//         when(vehicleRestService.getAllVehicle()).thenThrow(new RuntimeException("DB Down"));

//         mockMvc.perform(get("/api/vehicles"))
//                 .andExpect(status().isInternalServerError())
//                 .andExpect(jsonPath("$.message").value("Gagal mengambil data kendaraan: DB Down"));
//     }

//     // ---------- GET BY ID ----------
//     @Test
//     void testGetVehicle_Success() throws Exception {
//         when(vehicleRestService.getVehicle("VEH0001")).thenReturn(vehicleDTO);

//         mockMvc.perform(get("/api/vehicles/VEH0001"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.data.id").value("VEH0001"));
//     }

//     @Test
//     void testGetVehicle_NotFound() throws Exception {
//         when(vehicleRestService.getVehicle("VEH9999")).thenReturn(null);

//         mockMvc.perform(get("/api/vehicles/VEH9999"))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath("$.message").value("Kendaraan dengan ID VEH9999 tidak ditemukan"));
//     }

//     @Test
//     void testGetVehicle_ExceptionThrown() throws Exception {
//         when(vehicleRestService.getVehicle("VEH0001")).thenThrow(new RuntimeException("Error"));

//         mockMvc.perform(get("/api/vehicles/VEH0001"))
//                 .andExpect(status().isInternalServerError())
//                 .andExpect(jsonPath("$.message").value("Terjadi kesalahan server: Error"));
//     }

//     // ---------- CREATE ----------

//     @Test
//     void testCreateVehicle_BindingError() throws Exception {
//         AddVehicleRequestDTO dto = new AddVehicleRequestDTO();

//         FieldError error = new FieldError("addVehicleRequestDTO", "licensePlate", "License plate required");
//         BindingResult mockBinding = mock(BindingResult.class);
//         when(mockBinding.hasFieldErrors()).thenReturn(true);
//         when(mockBinding.getFieldErrors()).thenReturn(List.of(error));

//         VehicleRestController controller = new VehicleRestController();
//         controller.vehicleRestService = vehicleRestService;
//         var resp = controller.createVehicle(dto, mockBinding);
//         assertEquals(400, resp.getStatusCode().value());
//         assertTrue(resp.getBody().getMessage().contains("License plate required"));
//     }


//     // ---------- UPDATE ----------
//     @Test
//     void testUpdateVehicle_Success() throws Exception {
// UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
// dto.setId("VEH001"); // sama persis dengan path
// dto.setBrand("Honda");
// dto.setType("Car");
// dto.setLicensePlate("B1234XYZ");
// dto.setRentalVendorId(1);

// when(vehicleRestService.updateVehicle(any(UpdateVehicleRequestDTO.class)))
//         .thenReturn(new VehicleResponseDTO());

// mockMvc.perform(put("/api/vehicles/update/{id}", "VEH001")
//         .contentType(MediaType.APPLICATION_JSON)
//         .content(objectMapper.writeValueAsString(dto)))
//     .andExpect(status().isOk()); 

//     }

//     @Test
//     void testUpdateVehicle_IdMismatch() throws Exception {
//         UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
//         dto.setId("VEH0002");

//         mockMvc.perform(put("/api/vehicles/update/VEH0001")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(dto)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.message").value("ID kendaraan tidak sesuai dengan data body"));
//     }

//     @Test
//     void testUpdateVehicle_BindingError() throws Exception {
//         UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
//         dto.setId("VEH0001");

//         FieldError error = new FieldError("updateVehicleRequestDTO", "type", "Type required");
//         BindingResult mockBinding = mock(BindingResult.class);
//         when(mockBinding.hasFieldErrors()).thenReturn(true);
//         when(mockBinding.getFieldErrors()).thenReturn(List.of(error));

//         VehicleRestController controller = new VehicleRestController();
//         controller.vehicleRestService = vehicleRestService;
//         var resp = controller.updateVehicle("VEH0001", dto, mockBinding);
//         assertEquals(400, resp.getStatusCode().value());
//         assertTrue(resp.getBody().getMessage().contains("Type required"));
//     }

// @Test
// void testUpdateVehicle_NotFound() throws Exception {
//     UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
//     dto.setId("VEH001"); // sama persis dengan path
//     dto.setType("Car");
//     dto.setBrand("Toyota");
//     dto.setLicensePlate("B1234XYZ");
//     dto.setRentalVendorId(1);

//     when(vehicleRestService.updateVehicle(any(UpdateVehicleRequestDTO.class)))
//         .thenReturn(null);

//     mockMvc.perform(put("/api/vehicles/update/{id}", "VEH001")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(objectMapper.writeValueAsString(dto)))
//         .andExpect(status().isNotFound()); 
// }

// @Test
// void testUpdateVehicle_IllegalArgumentException() throws Exception {
//     UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
//     dto.setId("VEH001");

//     when(vehicleRestService.updateVehicle(any(UpdateVehicleRequestDTO.class)))
//         .thenThrow(new IllegalArgumentException("Bad data"));

//     mockMvc.perform(put("/api/vehicles/update/{id}", "VEH001")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(objectMapper.writeValueAsString(dto)))
//         .andExpect(status().isBadRequest())
//         .andExpect(jsonPath("$.message").value("Gagal memperbarui: Bad data")); // ❌ gagal
// }


// @Test
// void testUpdateVehicle_GenericException() throws Exception {
//     UpdateVehicleRequestDTO dto = new UpdateVehicleRequestDTO();
//     dto.setId("VEH001");
//     dto.setType("Car");
//     dto.setBrand("Toyota");
//     dto.setLicensePlate("B1234XYZ");
//     dto.setRentalVendorId(1);
//     dto.setTransmission("Automatic");
//     dto.setFuelType("Gasoline");
//     dto.setPrice(500000.0);
//     dto.setStatus("Available");
//     dto.setCapacity(5);
//     dto.setLocation("Depok");

//     // Service lempar generic exception
//     when(vehicleRestService.updateVehicle(any(UpdateVehicleRequestDTO.class)))
//         .thenThrow(new RuntimeException("Something broke"));

//     mockMvc.perform(put("/api/vehicles/update/{id}", "VEH001")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(objectMapper.writeValueAsString(dto)))
//         .andDo(print())
//         .andExpect(status().isInternalServerError()) // ✅ Sekarang 500
//         .andExpect(jsonPath("$.message").value("Terjadi kesalahan server: Something broke"));
// }


//     // ---------- DELETE ----------
//     @Test
//     void testDeleteVehicle_Success() throws Exception {
//         when(vehicleRestService.deleteVehicle("VEH0001")).thenReturn(vehicleDTO);

//         mockMvc.perform(delete("/api/vehicles/delete/VEH0001"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.message").value("Kendaraan berhasil dihapus"));
//     }

//     @Test
//     void testDeleteVehicle_NotFound() throws Exception {
//         when(vehicleRestService.deleteVehicle("VEH0001")).thenReturn(null);

//         mockMvc.perform(delete("/api/vehicles/delete/VEH0001"))
//                 .andExpect(status().isNotFound())
//                 .andExpect(jsonPath("$.message").value("Kendaraan dengan ID VEH0001 tidak ditemukan"));
//     }

//     @Test
//     void testDeleteVehicle_IllegalArgumentException() throws Exception {
//         when(vehicleRestService.deleteVehicle("VEH0001"))
//                 .thenThrow(new IllegalArgumentException("Active bookings exist"));

//         mockMvc.perform(delete("/api/vehicles/delete/VEH0001"))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.message").value("Tidak dapat menghapus kendaraan: Active bookings exist"));
//     }

//     @Test
//     void testDeleteVehicle_GenericException() throws Exception {
//         when(vehicleRestService.deleteVehicle("VEH0001"))
//                 .thenThrow(new RuntimeException("Fatal"));

//         mockMvc.perform(delete("/api/vehicles/delete/VEH0001"))
//                 .andExpect(status().isInternalServerError())
//                 .andExpect(jsonPath("$.message").value("Terjadi kesalahan server: Fatal"));
//     }

// }


