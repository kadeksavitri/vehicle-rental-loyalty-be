package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance.CreateMaintenanceRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance.UpdateMaintenanceRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.maintenance.MaintenanceRecordResponseDTO;

import java.util.List;

public interface MaintenanceRestService {
    List<MaintenanceRecordResponseDTO> getAllMaintenanceRecords();
    
    MaintenanceRecordResponseDTO getMaintenanceRecordById(String id);
    
    MaintenanceRecordResponseDTO createMaintenanceRecord(CreateMaintenanceRequestDTO dto);
    
    MaintenanceRecordResponseDTO updateMaintenanceRecord(UpdateMaintenanceRequestDTO dto);
    
    MaintenanceRecordResponseDTO updateMaintenanceRecordStatus(String id, String status);
    
    MaintenanceRecordResponseDTO deleteMaintenanceRecord(String id);
}