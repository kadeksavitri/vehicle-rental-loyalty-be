package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.AddVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle.VehicleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface VehicleRestService {
    VehicleResponseDTO createVehicle(AddVehicleRequestDTO addVehicleRequestDTO);

    List<VehicleResponseDTO> getAllVehicleByKeywordAndType(String keyword, String type);

    List<VehicleResponseDTO> getAllVehicle();

    VehicleResponseDTO getVehicle(String id);

    VehicleResponseDTO updateVehicle(UpdateVehicleRequestDTO updateVehicleRequestDTO);

    VehicleResponseDTO deleteVehicle(String id);
    
}
