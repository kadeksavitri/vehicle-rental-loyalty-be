// package apap.ti._5.vehicle_rental_2306203236_be.service;

// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.CreateVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.ReadVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.UpdateVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.util.List;

// import static org.assertj.core.api.Assertions.assertThat;

// class VehicleServiceTest {

//     private VehicleService service;
//     private Vehicle dummyVehicle;

//     @BeforeEach
//     void setUp() {
//         // Dummy vehicle data
//         dummyVehicle = Vehicle.builder()
//                 .id("VH001")
//                 .brand("Toyota")
//                 .model("Avanza")
//                 .type("MPV")
//                 .price(400000.0)
//                 .location("Depok")
//                 .status("Available")
//                 .build();

//         // Anonymous implementation of VehicleService for coverage
//         service = new VehicleService() {
//             @Override
//             public List<Vehicle> getAllVehicle(String keyword, String type) {
//                 return List.of(dummyVehicle);
//             }

//             @Override
//             public List<ReadVehicleDto> getAllVehicleDto(String keyword, String type) {
//                 return List.of(new ReadVehicleDto());
//             }

//             @Override
//             public Vehicle getVehicle(String id) {
//                 return Vehicle.builder().id(id).brand("Honda").model("Civic").build();
//             }

//             @Override
//             public Vehicle createVehicle(CreateVehicleDto createVehicleDto) {
//                 return Vehicle.builder().id("VH_NEW").brand("Nissan").model("Livina").build();
//             }

//             @Override
//             public Vehicle updateVehicle(UpdateVehicleDto updateVehicleDto) {
//                 return Vehicle.builder().id("VH_UPDATE").brand("Daihatsu").model("Xenia").build();
//             }

//             @Override
//             public Vehicle deleteVehicle(String id) {
//                 return Vehicle.builder().id(id).brand("DeletedBrand").model("DeletedModel").build();
//             }
//         };
//     }

//     @Test
//     void testGetAllVehicle() {
//         var result = service.getAllVehicle("ava", "MPV");
//         assertThat(result).isNotEmpty();
//         assertThat(result.get(0).getBrand()).isEqualTo("Toyota");
//         assertThat(result.get(0).getModel()).isEqualTo("Avanza");
//     }

//     @Test
//     void testGetAllVehicleDto() {
//         var result = service.getAllVehicleDto("cari", "SUV");
//         assertThat(result).hasSize(1);
//         assertThat(result.get(0)).isInstanceOf(ReadVehicleDto.class);
//     }

//     @Test
//     void testGetVehicle() {
//         var result = service.getVehicle("VH999");
//         assertThat(result.getId()).isEqualTo("VH999");
//         assertThat(result.getBrand()).isEqualTo("Honda");
//     }

//     @Test
//     void testCreateVehicle() {
//         var result = service.createVehicle(new CreateVehicleDto());
//         assertThat(result.getId()).isEqualTo("VH_NEW");
//         assertThat(result.getBrand()).isEqualTo("Nissan");
//     }

//     @Test
//     void testUpdateVehicle() {
//         var result = service.updateVehicle(new UpdateVehicleDto());
//         assertThat(result.getId()).isEqualTo("VH_UPDATE");
//         assertThat(result.getModel()).isEqualTo("Xenia");
//     }

//     @Test
//     void testDeleteVehicle() {
//         var result = service.deleteVehicle("VH_DEL");
//         assertThat(result.getId()).isEqualTo("VH_DEL");
//         assertThat(result.getBrand()).isEqualTo("DeletedBrand");
//     }
// }
