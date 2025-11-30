package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.config.security.CurrentUser;
import apap.ti._5.vehicle_rental_2306203236_be.model.MaintenanceRecord;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.repository.MaintenanceRecordRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance.CreateMaintenanceRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance.UpdateMaintenanceRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.maintenance.MaintenanceRecordResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.service.RentalVendorService;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaintenanceRestServiceImpl implements MaintenanceRestService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final RentalVendorService rentalVendorService;
    private final IdGenerator idGenerator;

    public MaintenanceRestServiceImpl(MaintenanceRecordRepository maintenanceRecordRepository,
                                      VehicleRepository vehicleRepository,
                                      RentalVendorRepository rentalVendorRepository,
                                      RentalVendorService rentalVendorService,
                                      IdGenerator idGenerator) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
        this.rentalVendorService = rentalVendorService;
        this.idGenerator = idGenerator;
    }

    @Override
    public List<MaintenanceRecordResponseDTO> getAllMaintenanceRecords() {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();

        List<MaintenanceRecord> records;
        if ("ROLE_SUPERADMIN".equals(role)) {
            records = maintenanceRecordRepository.findAllByDeletedAtIsNull();
        } else if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            records = maintenanceRecordRepository.findAllByRentalVendorIdAndDeletedAtIsNull(vendor.getId());
        } else {
            throw new IllegalArgumentException("Role tidak valid untuk mengakses maintenance record");
        }

        return records.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceRecordResponseDTO getMaintenanceRecordById(String id) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();

        Optional<MaintenanceRecord> optRecord = maintenanceRecordRepository.findByIdAndDeletedAtIsNull(id);
        if (optRecord.isEmpty()) {
            return null;
        }

        MaintenanceRecord record = optRecord.get();

        // Check ownership for rental vendor
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            if (!record.getRentalVendorId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses ke maintenance record ini");
            }
        }

        return convertToResponseDTO(record);
    }

    @Override
    public MaintenanceRecordResponseDTO createMaintenanceRecord(CreateMaintenanceRequestDTO dto) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();

        // Fetch vehicle
        Optional<Vehicle> optVehicle = vehicleRepository.findById(dto.getVehicleId());
        if (optVehicle.isEmpty()) {
            throw new IllegalArgumentException("Vehicle dengan ID " + dto.getVehicleId() + " tidak ditemukan");
        }

        Vehicle vehicle = optVehicle.get();

        // Check if vehicle status is "Available"
        if (!"Available".equalsIgnoreCase(vehicle.getStatus())) {
            throw new IllegalArgumentException("Vehicle harus berstatus Available untuk membuat maintenance record");
        }

        // Get or determine vendor
        RentalVendor vendor;
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            vendor = rentalVendorService.getOrCreateVendor(email);
            // Check ownership of the vehicle
            if (!vehicle.getRentalVendorId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses ke vehicle ini");
            }
        } else {
            // Superadmin uses the vehicle's vendor
            vendor = vehicle.getRentalVendor();
        }

        // Generate ID
        List<MaintenanceRecord> allRecords = maintenanceRecordRepository.findAll();
        String lastId = allRecords.isEmpty() ? null : allRecords.get(allRecords.size() - 1).getId();
        String newId = idGenerator.generateMaintenanceRecordId(lastId);

        // Build maintenance record
        MaintenanceRecord record = MaintenanceRecord.builder()
                .id(newId)
                .vehicleId(vehicle.getId())
                .vehicle(vehicle)
                .rentalVendorId(vendor.getId())
                .rentalVendor(vendor)
                .serviceDate(dto.getServiceDate())
                .description(dto.getDescription())
                .cost(dto.getCost())
                .vendorNote(dto.getVendorNote())
                .status("Ongoing")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        maintenanceRecordRepository.save(record);

        // Update vehicle status to "In Maintenance"
        vehicle.setStatus("In Maintenance");
        vehicle.setUpdatedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);

        return convertToResponseDTO(record);
    }

    @Override
    public MaintenanceRecordResponseDTO updateMaintenanceRecord(UpdateMaintenanceRequestDTO dto) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();

        Optional<MaintenanceRecord> optRecord = maintenanceRecordRepository.findByIdAndDeletedAtIsNull(dto.getId());
        if (optRecord.isEmpty()) {
            return null;
        }

        MaintenanceRecord record = optRecord.get();

        // Check ownership for rental vendor
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            if (!record.getRentalVendorId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses untuk mengubah maintenance record ini");
            }
        }

        // Fetch vehicle if changed
        if (!record.getVehicleId().equals(dto.getVehicleId())) {
            Optional<Vehicle> optVehicle = vehicleRepository.findById(dto.getVehicleId());
            if (optVehicle.isEmpty()) {
                throw new IllegalArgumentException("Vehicle tidak ditemukan");
            }
            Vehicle newVehicle = optVehicle.get();
            record.setVehicleId(newVehicle.getId());
            record.setVehicle(newVehicle);
        }

        record.setServiceDate(dto.getServiceDate());
        record.setDescription(dto.getDescription());
        record.setCost(dto.getCost());
        record.setVendorNote(dto.getVendorNote());
        record.setStatus(dto.getStatus());
        record.setUpdatedAt(LocalDateTime.now());

        maintenanceRecordRepository.save(record);

        // If status is "Completed", update vehicle status to "Available"
        if ("Completed".equalsIgnoreCase(dto.getStatus())) {
            Vehicle vehicle = record.getVehicle();
            vehicle.setStatus("Available");
            vehicle.setUpdatedAt(LocalDateTime.now());
            vehicleRepository.save(vehicle);
        }

        return convertToResponseDTO(record);
    }

    @Override
    public MaintenanceRecordResponseDTO updateMaintenanceRecordStatus(String id, String status) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();

        Optional<MaintenanceRecord> optRecord = maintenanceRecordRepository.findByIdAndDeletedAtIsNull(id);
        if (optRecord.isEmpty()) {
            return null;
        }

        MaintenanceRecord record = optRecord.get();

        // Check ownership for rental vendor
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            if (!record.getRentalVendorId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses untuk mengubah status maintenance record ini");
            }
        }

        record.setStatus(status);
        record.setUpdatedAt(LocalDateTime.now());
        maintenanceRecordRepository.save(record);

        // If status is "Completed", update vehicle status to "Available"
        if ("Completed".equalsIgnoreCase(status)) {
            Vehicle vehicle = record.getVehicle();
            if (vehicle != null) {
                vehicle.setStatus("Available");
                vehicle.setUpdatedAt(LocalDateTime.now());
                vehicleRepository.save(vehicle);
            }
        }

        return convertToResponseDTO(record);
    }

    @Override
    public MaintenanceRecordResponseDTO deleteMaintenanceRecord(String id) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();

        Optional<MaintenanceRecord> optRecord = maintenanceRecordRepository.findByIdAndDeletedAtIsNull(id);
        if (optRecord.isEmpty()) {
            return null;
        }

        MaintenanceRecord record = optRecord.get();

        // Check ownership for rental vendor
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            if (!record.getRentalVendorId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses untuk menghapus maintenance record ini");
            }
        }

        // Soft delete
        record.setDeletedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        maintenanceRecordRepository.save(record);

        return convertToResponseDTO(record);
    }

    private MaintenanceRecordResponseDTO convertToResponseDTO(MaintenanceRecord record) {
        String vehicleDisplay = record.getVehicle() != null
                ? record.getVehicle().getId() + " - " + record.getVehicle().getLicensePlate()
                : "Unknown Vehicle";

        return MaintenanceRecordResponseDTO.builder()
                .id(record.getId())
                .vehicleId(record.getVehicleId())
                .vehicleDisplay(vehicleDisplay)
                .serviceDate(record.getServiceDate())
                .description(record.getDescription())
                .cost(record.getCost())
                .vendorNote(record.getVendorNote())
                .status(record.getStatus())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
