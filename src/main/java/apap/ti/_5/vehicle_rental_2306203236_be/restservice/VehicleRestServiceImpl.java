package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.AddVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleRestServiceImpl implements VehicleRestService {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final IdGenerator idGenerator;

    public VehicleRestServiceImpl(VehicleRepository vehicleRepository,
                                  RentalVendorRepository rentalVendorRepository,
                                  IdGenerator idGenerator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public VehicleResponseDTO createVehicle(AddVehicleRequestDTO dto) {
        Optional<RentalVendor> optVendor = rentalVendorRepository.findById(dto.getRentalVendorId());
        if (optVendor.isEmpty()) throw new IllegalArgumentException("Rental vendor not found");

        RentalVendor vendor = optVendor.get();
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
        return vehicleRepository.findAllByDeletedAtIsNull().stream()
                .map(this::convertToVehicleResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicleByKeywordAndType(String keyword, String type) {
        List<Vehicle> vehicles;

        if ((keyword == null || keyword.isBlank()) && (type == null || type.isBlank())) {
            vehicles = vehicleRepository.findAllByDeletedAtIsNull();
        } else if (type != null && !type.isBlank()) {
            vehicles = vehicleRepository.findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase(
                    type, keyword.trim(), type, keyword.trim(), type, keyword.trim());
        } else {
            vehicles = vehicleRepository.findByIdContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCase(
                    keyword.trim(), keyword.trim(), keyword.trim());
        }

        return vehicles.stream()
                .map(this::convertToVehicleResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponseDTO getVehicle(String id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);
        
        if (vehicle == null) return null;
        return convertToVehicleResponseDto(vehicle);
    }

    @Override
    public VehicleResponseDTO updateVehicle(UpdateVehicleRequestDTO dto) {
        Optional<Vehicle> optVehicle = vehicleRepository.findById(dto.getId());
        if (optVehicle.isEmpty()) return null;

        Vehicle vehicle = optVehicle.get();
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
        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);
        if (vehicle == null) return null;

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
}
