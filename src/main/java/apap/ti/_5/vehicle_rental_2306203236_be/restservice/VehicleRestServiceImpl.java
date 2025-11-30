package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.config.security.CurrentUser;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.AddVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.SearchAvailableVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle.AvailableVehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.service.RentalVendorService;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleRestServiceImpl implements VehicleRestService {

    private final RentalBookingRepository rentalBookingRepository;

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final RentalVendorService rentalVendorService;
    private final IdGenerator idGenerator;

    public VehicleRestServiceImpl(VehicleRepository vehicleRepository,
                                  RentalVendorRepository rentalVendorRepository, 
                                  RentalBookingRepository rentalBookingRepository,
                                  RentalVendorService rentalVendorService,
                                  IdGenerator idGenerator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
        this.rentalBookingRepository = rentalBookingRepository;
        this.rentalVendorService = rentalVendorService;
        this.idGenerator = idGenerator;
    }

    @Override
    public VehicleResponseDTO createVehicle(AddVehicleRequestDTO dto) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        
        // Get or create vendor for rental vendor role
        RentalVendor vendor;
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            vendor = rentalVendorService.getOrCreateVendor(email);
            // Force vendor ID to be the logged-in vendor's ID
            dto.setRentalVendorId(vendor.getId());
        } else {
            // Superadmin can specify any vendor
            Optional<RentalVendor> optVendor = rentalVendorRepository.findById(dto.getRentalVendorId());
            if (optVendor.isEmpty()) throw new IllegalArgumentException("Rental vendor not found");
            vendor = optVendor.get();
        }

        int currentYear = LocalDateTime.now().getYear();

        if (dto.getProductionYear() > currentYear)
            throw new IllegalArgumentException("Production year cannot exceed current year");

        if (!vendor.getListOfLocations().contains(dto.getLocation()))
            throw new IllegalArgumentException("Selected location is not in vendor's operational area");

        if (vehicleRepository.existsByLicensePlate(dto.getLicensePlate()))
            throw new IllegalArgumentException("License plate already exists");

        // generate new ID
        Vehicle lastVehicle = vehicleRepository.findLatestVehicleIncludingDeleted();
        String newId = idGenerator.generateVehicleId(lastVehicle != null ? lastVehicle.getId() : null);

        Vehicle vehicle = Vehicle.builder()
                .id(newId)
                .rentalVendorId(vendor.getId())
                .rentalVendor(vendor)
                .type(dto.getType())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .productionYear(dto.getProductionYear())
                .location(dto.getLocation())
                .licensePlate(dto.getLicensePlate())
                .capacity(dto.getCapacity())
                .transmission(dto.getTransmission())
                .fuelType(dto.getFuelType())
                .price(dto.getPrice())
                .status("Available")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        vehicleRepository.save(vehicle);
        return convertToVehicleResponseDto(vehicle);
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicle() {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        
        List<Vehicle> vehicles;
        if ("ROLE_SUPERADMIN".equals(role)) {
            // Superadmin sees all vehicles
            vehicles = vehicleRepository.findAllByDeletedAtIsNull();
        } else if ("ROLE_RENTAL_VENDOR".equals(role)) {
            // Rental vendor sees only their vehicles
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            vehicles = vehicleRepository.findAllByRentalVendorIdAndDeletedAtIsNull(vendor.getId());
        } else {
            throw new IllegalArgumentException("Role tidak valid untuk mengakses data kendaraan");
        }
        
        return vehicles.stream()
                .map(this::convertToVehicleResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicleByKeywordAndType(String keyword, String type) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        
        List<Vehicle> vehicles;

        if ("ROLE_SUPERADMIN".equals(role)) {
            // Superadmin searches across all vehicles
            if ((keyword == null || keyword.isBlank()) && (type == null || type.isBlank())) {
                vehicles = vehicleRepository.findAllByDeletedAtIsNull();
            } else if (type != null && !type.isBlank()) {
                vehicles = vehicleRepository.findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase(
                        type, keyword != null ? keyword.trim() : "", type, keyword != null ? keyword.trim() : "", type, keyword != null ? keyword.trim() : "");
            } else {
                vehicles = vehicleRepository.findByIdContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCase(
                        keyword.trim(), keyword.trim(), keyword.trim());
            }
        } else if ("ROLE_RENTAL_VENDOR".equals(role)) {
            // Rental vendor searches only their vehicles
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            vehicles = vehicleRepository.findAllByRentalVendorIdAndDeletedAtIsNull(vendor.getId());
            
            // Apply keyword and type filtering
            if (keyword != null && !keyword.isBlank()) {
                String lowerKeyword = keyword.trim().toLowerCase();
                vehicles = vehicles.stream()
                    .filter(v -> v.getId().toLowerCase().contains(lowerKeyword) ||
                                v.getBrand().toLowerCase().contains(lowerKeyword) ||
                                v.getModel().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
            }
            
            if (type != null && !type.isBlank()) {
                vehicles = vehicles.stream()
                    .filter(v -> v.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
            }
        } else {
            throw new IllegalArgumentException("Role tidak valid untuk mengakses data kendaraan");
        }

        return vehicles.stream()
                .map(this::convertToVehicleResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponseDTO getVehicle(String id) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        
        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);
        
        if (vehicle == null) return null;
        
        // Check ownership for rental vendor
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            if (!vehicle.getRentalVendorId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses ke kendaraan ini");
            }
        }
        
        return convertToVehicleResponseDto(vehicle);
    }

    @Override
    public VehicleResponseDTO updateVehicle(UpdateVehicleRequestDTO dto) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        
        Optional<Vehicle> optVehicle = vehicleRepository.findById(dto.getId());
        if (optVehicle.isEmpty()) return null;

        Vehicle vehicle = optVehicle.get();
        
        // Check ownership for rental vendor
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            if (!vehicle.getRentalVendorId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses untuk mengubah kendaraan ini");
            }
            // Force vendor ID to remain the same for rental vendor
            dto.setRentalVendorId(vendor.getId());
        }
        
        Optional<RentalVendor> optVendor = rentalVendorRepository.findById(dto.getRentalVendorId());
        if (optVendor.isEmpty()) throw new IllegalArgumentException("Vendor not found");

        vehicle.setRentalVendor(optVendor.get());
        vehicle.setRentalVendorId(dto.getRentalVendorId());
        vehicle.setType(dto.getType());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setProductionYear(dto.getProductionYear());
        vehicle.setLocation(dto.getLocation());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setCapacity(dto.getCapacity());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setPrice(dto.getPrice());
        vehicle.setStatus(dto.getStatus());
        vehicle.setUpdatedAt(LocalDateTime.now());

        vehicleRepository.save(vehicle);
        return convertToVehicleResponseDto(vehicle);
    }

    @Override
    public VehicleResponseDTO deleteVehicle(String id) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        
        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);
        if (vehicle == null) return null;
        
        // Check ownership for rental vendor
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            if (!vehicle.getRentalVendorId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses untuk menghapus kendaraan ini");
            }
        }

        boolean hasActiveBooking = rentalBookingRepository.existsByVehicleAndStatusIn(
            vehicle,
            List.of("Upcoming", "Ongoing")
        );

        if (hasActiveBooking) {
            throw new IllegalArgumentException("Cannot delete vehicle that still has active bookings");
        }
        if ("In Use".equalsIgnoreCase(vehicle.getStatus()))
            throw new IllegalArgumentException("Cannot delete vehicle that is currently rented");

        vehicle.setDeletedAt(LocalDateTime.now());
        vehicle.setStatus("Unavailable");
        vehicleRepository.save(vehicle);

        return convertToVehicleResponseDto(vehicle);
    }

    private VehicleResponseDTO convertToVehicleResponseDto(Vehicle vehicle) {
        String vendorName = vehicle.getRentalVendor() != null
                ? vehicle.getRentalVendor().getName()
                : "Unknown Vendor";

        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .rentalVendorId(vehicle.getRentalVendorId())
                .rentalVendorName(vendorName)
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .productionYear(vehicle.getProductionYear())
                .location(vehicle.getLocation())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .transmission(vehicle.getTransmission())
                .fuelType(vehicle.getFuelType())
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .build();
    }

    @Override
    public List<AvailableVehicleResponseDTO> searchAvailableVehicles(SearchAvailableVehicleRequestDTO searchRequest) {
        // Get all vehicles
        List<Vehicle> allVehicles = vehicleRepository.findAll();

        // Calculate days for the requested period
        long days = Math.max(1, Duration.between(searchRequest.getPickUpTime(), searchRequest.getDropOffTime()).toDays());

        // Filter and map vehicles
        return allVehicles.stream()
                .filter(vehicle -> {
                    // Get vendor
                    RentalVendor vendor = vehicle.getRentalVendor();
                    
                    // Check if vendor has both pickup and dropoff locations
                    boolean vendorHasLocations = vendor != null 
                            && vendor.getListOfLocations() != null 
                            && vendor.getListOfLocations().contains(searchRequest.getPickUpLocation())
                            && vendor.getListOfLocations().contains(searchRequest.getDropOffLocation());

                    // Filter conditions
                    return vehicle.getCapacity() >= searchRequest.getCapacityNeeded()
                            && vehicle.getTransmission().equalsIgnoreCase(searchRequest.getTransmissionNeeded())
                            && "Available".equals(vehicle.getStatus())
                            && vehicle.getLocation().equals(searchRequest.getPickUpLocation())
                            && vendorHasLocations;
                })
                .map(vehicle -> convertToAvailableVehicleResponseDTO(vehicle, days))
                .collect(Collectors.toList());
    }

    private AvailableVehicleResponseDTO convertToAvailableVehicleResponseDTO(Vehicle vehicle, long days) {
        String vendorName = vehicle.getRentalVendor() != null
                ? vehicle.getRentalVendor().getName()
                : "Unknown Vendor";

        return AvailableVehicleResponseDTO.builder()
                .id(vehicle.getId())
                .rentalVendorId(vehicle.getRentalVendorId())
                .rentalVendorName(vendorName)
                .type(vehicle.getType())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .productionYear(vehicle.getProductionYear())
                .location(vehicle.getLocation())
                .licensePlate(vehicle.getLicensePlate())
                .capacity(vehicle.getCapacity())
                .transmission(vehicle.getTransmission())
                .fuelType(vehicle.getFuelType())
                .price(vehicle.getPrice())
                .status(vehicle.getStatus())
                .calculatedPrice(vehicle.getPrice() * days)
                .createdAt(vehicle.getCreatedAt())
                .build();
    }
}
