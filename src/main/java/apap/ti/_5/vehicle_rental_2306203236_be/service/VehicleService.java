package apap.ti._5.vehicle_rental_2306203236_be.service;

import java.util.List;

import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.CreateVehicleDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.UpdateVehicleDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.ReadVehicleDto;

public interface VehicleService {

    List<Vehicle> getAllVehicle(String keyword, String type);

    List<ReadVehicleDto> getAllVehicleDto(String keyword, String type);

    Vehicle getVehicle(String id);
    
    Vehicle createVehicle(CreateVehicleDto createVehicleDto);

    Vehicle updateVehicle(UpdateVehicleDto updateVehicleDto);

    Vehicle deleteVehicle(String id);
}
