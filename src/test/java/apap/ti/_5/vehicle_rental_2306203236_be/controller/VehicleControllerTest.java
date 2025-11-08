// package apap.ti._5.vehicle_rental_2306203236_be.controller;

// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.CreateVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.ReadVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.UpdateVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
// import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
// import apap.ti._5.vehicle_rental_2306203236_be.service.VehicleService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.validation.BindingResult;

// import java.util.List;
// import java.util.Map;

// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// class VehicleControllerTest {

//     @Autowired private MockMvc mockMvc;

//     @MockBean private VehicleService vehicleService;
//     @MockBean private RentalVendorRepository vendorRepo;

//     private Vehicle vehicle;
//     private RentalVendor vendor;

//     @BeforeEach
//     void setup() {
//         vendor = RentalVendor.builder()
//                 .id(1)
//                 .name("Vendor A")
//                 .listOfLocations(List.of("Depok","Jakarta"))
//                 .build();

//         vehicle = Vehicle.builder()
//                 .id("VH001")
//                 .rentalVendor(vendor)
//                 .rentalVendorId(1)
//                 .brand("Toyota")
//                 .model("Avanza")
//                 .type("MPV")
//                 .status("Available")
//                 .build();

//         when(vehicleService.getAllVehicleDto(any(), any()))
//                 .thenReturn(List.of(new ReadVehicleDto()));
//         when(vehicleService.getVehicle("VH001")).thenReturn(vehicle);
//         when(vehicleService.getVehicle("NOTFOUND")).thenReturn(null);
//         when(vehicleService.createVehicle(any())).thenReturn(vehicle);
//         when(vehicleService.updateVehicle(any())).thenReturn(vehicle);
//         when(vehicleService.deleteVehicle("VH001")).thenReturn(vehicle);
//         when(vendorRepo.findAll()).thenReturn(List.of(vendor));
//     }

//     // ---------- viewAll ----------
//     @Test
//     void testViewAllVehicles() throws Exception {
//         mockMvc.perform(get("/vehicles")
//                         .param("keyword","ava").param("type","MPV"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("vehicle/view-all"))
//                 .andExpect(model().attributeExists("vehicles"));
//     }

//     // ---------- viewDetail ----------
//     @Test
//     void testViewVehicleFound() throws Exception {
//         mockMvc.perform(get("/vehicles/VH001"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("vehicle/detail"))
//                 .andExpect(model().attributeExists("vehicle"));
//     }

//     @Test
//     void testViewVehicleNotFound() throws Exception {
//         mockMvc.perform(get("/vehicles/NOTFOUND"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("error/404"))
//                 .andExpect(model().attributeExists("title","message"));
//     }

//     // ---------- createForm ----------
//     @Test
//     void testCreateVehicleForm() throws Exception {
//         mockMvc.perform(get("/vehicles/create"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("vehicle/form"))
//                 .andExpect(model().attribute("isEdit",false))
//                 .andExpect(model().attributeExists("vehicle","vendors","rentalVendorLocations"));
//     }

//     // ---------- createVehicle ----------
//     @Test
//     void testCreateVehicleValidationError() throws Exception {
//         CreateVehicleDto dto = new CreateVehicleDto();
//         mockMvc.perform(post("/vehicles/create")
//                         .flashAttr("createVehicleDto", dto)
//                         .flashAttr("bindingResult", (BindingResult) null))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("vehicle/form"));
//     }

//     @Test
//     void testCreateVehicleNullReturned() throws Exception {
//         when(vehicleService.createVehicle(any())).thenReturn(null);
//         mockMvc.perform(post("/vehicles/create")
//                         .flashAttr("createVehicleDto", new CreateVehicleDto()))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("vehicle/form"))
//                 .andExpect(model().attributeExists("errorMessage"));
//     }

//     @Test
//     void testCreateVehicleSuccess() throws Exception {
//         mockMvc.perform(post("/vehicles/create")
//                         .flashAttr("createVehicleDto", new CreateVehicleDto()))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/vehicles"));
//     }

//     // ---------- updateForm ----------
//     @Test
//     void testUpdateFormFound() throws Exception {
//         mockMvc.perform(get("/vehicles/update/VH001"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("vehicle/form"))
//                 .andExpect(model().attribute("isEdit", true));
//     }

//     @Test
//     void testUpdateFormNotFound() throws Exception {
//         mockMvc.perform(get("/vehicles/update/NOTFOUND"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("error/404"));
//     }

//     @Test
//     void testUpdateFormThrowsException() throws Exception {
//         vehicle.setStatus("In Use");
//         when(vehicleService.getVehicle("VH001")).thenReturn(vehicle);
//         mockMvc.perform(get("/vehicles/update/VH001"))
//                 .andExpect(status().isInternalServerError());
//     }

//     // ---------- updateVehicle ----------
//     @Test
//     void testUpdateVehicleMismatchedId() throws Exception {
//         UpdateVehicleDto dto = new UpdateVehicleDto();
//         dto.setId("DIFFERENT");
//         mockMvc.perform(put("/vehicles/VH001/update")
//                         .flashAttr("updateVehicleDto", dto))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/vehicles"));
//     }

//     @Test
//     void testUpdateVehicleBindingError() throws Exception {
//         UpdateVehicleDto dto = new UpdateVehicleDto();
//         dto.setId("VH001");
//         mockMvc.perform(put("/vehicles/VH001/update")
//                         .flashAttr("updateVehicleDto", dto)
//                         .flashAttr("bindingResult", (BindingResult) null))
//                 .andExpect(status().is3xxRedirection());
//     }

//     @Test
//     void testUpdateVehicleNotFound() throws Exception {
//         when(vehicleService.updateVehicle(any())).thenReturn(null);
//         UpdateVehicleDto dto = new UpdateVehicleDto();
//         dto.setId("VH001");
//         mockMvc.perform(put("/vehicles/VH001/update")
//                         .flashAttr("updateVehicleDto", dto))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/vehicles"));
//     }

//     @Test
//     void testUpdateVehicleSuccess() throws Exception {
//         UpdateVehicleDto dto = new UpdateVehicleDto();
//         dto.setId("VH001");
//         mockMvc.perform(put("/vehicles/VH001/update")
//                         .flashAttr("updateVehicleDto", dto))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/vehicles"));
//     }

//     // ---------- deleteVehicle ----------
//     @Test
//     void testDeleteVehicleNotFound() throws Exception {
//         when(vehicleService.getVehicle("X")).thenReturn(null);
//         mockMvc.perform(delete("/vehicles/X/delete"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/vehicles"));
//     }

//     @Test
//     void testDeleteVehicleInUse() throws Exception {
//         vehicle.setStatus("In Use");
//         when(vehicleService.getVehicle("VH001")).thenReturn(vehicle);
//         mockMvc.perform(delete("/vehicles/VH001/delete"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/vehicles"));
//     }

//     @Test
//     void testDeleteVehicleSuccess() throws Exception {
//         mockMvc.perform(delete("/vehicles/VH001/delete"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/vehicles"));
//     }

//     @Test
//     void testDeleteVehicleNullAfterDelete() throws Exception {
//         when(vehicleService.deleteVehicle("VH001")).thenReturn(null);
//         mockMvc.perform(delete("/vehicles/VH001/delete"))
//                 .andExpect(status().is3xxRedirection())
//                 .andExpect(redirectedUrl("/vehicles"));
//     }
// }
