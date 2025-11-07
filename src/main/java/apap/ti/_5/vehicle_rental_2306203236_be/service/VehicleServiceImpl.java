package apap.ti._5.vehicle_rental_2306203236_be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.CreateVehicleDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.UpdateVehicleDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.ReadVehicleDto;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final IdGenerator idGenerator;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, RentalVendorRepository rentalVendorRepository, IdGenerator idGenerator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalVendorRepository = rentalVendorRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public Vehicle createVehicle(CreateVehicleDto createVehicleDto) {
        Optional<RentalVendor> optionalRentalVendor = rentalVendorRepository.findById(createVehicleDto.getRentalVendorId());
        if (optionalRentalVendor.isEmpty()) {
            return null;
        }

        RentalVendor vendor = optionalRentalVendor.get();
        int currentYear = LocalDateTime.now().getYear();

        if (createVehicleDto.getProductionYear() > currentYear) {
            throw new IllegalArgumentException("Production year cannot exceed current year");
        }

        if (!vendor.getListOfLocations().contains(createVehicleDto.getLocation())) {
            throw new IllegalArgumentException("Selected location is not in vendor's operational area");
        }

        if (vehicleRepository.existsByLicensePlate(createVehicleDto.getLicensePlate())) {
            throw new IllegalArgumentException("License plate already exists");
        }
        if (!vendor.getListOfLocations().contains(createVehicleDto.getLocation())) {
            throw new IllegalArgumentException("Selected location is not in vendor's operational area");
        }

        // Ambil ID terakhir di database
        Vehicle lastVehicle = vehicleRepository.findLatestVehicleIncludingDeleted();
        String newId = idGenerator.generateVehicleId(lastVehicle != null ? lastVehicle.getId() : null);

        Vehicle vehicle = Vehicle.builder()
                .id(newId)
                .rentalVendorId(vendor.getId())
                .rentalVendor(vendor)
                .type(createVehicleDto.getType())
                .brand(createVehicleDto.getBrand())
                .model(createVehicleDto.getModel())
                .productionYear(createVehicleDto.getProductionYear())
                .location(createVehicleDto.getLocation())
                .licensePlate(createVehicleDto.getLicensePlate())
                .capacity(createVehicleDto.getCapacity())
                .transmission(createVehicleDto.getTransmission())
                .fuelType(createVehicleDto.getFuelType())
                .price(createVehicleDto.getPrice())
                .status("Available")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> getAllVehicle(String keyword, String type) {
        if ((keyword == null || keyword.trim().isEmpty()) && (type == null || type.trim().isEmpty())) {
            return vehicleRepository.findAllByDeletedAtIsNull();
        }

        if (type != null && !type.trim().isEmpty()) {
            return vehicleRepository.findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase(
                type, keyword.trim(), type, keyword.trim(), type, keyword.trim()
            );
        }

        return vehicleRepository.findByIdContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCase(
            keyword.trim(), keyword.trim(), keyword.trim()
        );
    }


    @Override
    public List<ReadVehicleDto> getAllVehicleDto(String keyword, String type) {
        List<Vehicle> vehicles;

        if ((keyword == null || keyword.trim().isEmpty()) && (type == null || type.trim().isEmpty())) {
            vehicles = vehicleRepository.findAllByDeletedAtIsNull();
        } else if (type != null && !type.trim().isEmpty()) {
            vehicles = vehicleRepository.findByTypeAndIdContainingIgnoreCaseOrTypeAndBrandContainingIgnoreCaseOrTypeAndModelContainingIgnoreCase(
                type, keyword.trim(), type, keyword.trim(), type, keyword.trim()
            );
        } else {
            vehicles = vehicleRepository.findByIdContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCase(
                keyword.trim(), keyword.trim(), keyword.trim()
            );
        }

        return vehicles.stream()
                .map(this::convertToReadVehicleDto)
                .collect(Collectors.toList());
    }


    @Override 
    public Vehicle getVehicle(String id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    @Override
    public Vehicle updateVehicle(UpdateVehicleDto updateVehicleDto) {
        Optional<Vehicle> existingVehicleOption = vehicleRepository.findById(updateVehicleDto.getId());
        if (existingVehicleOption.isEmpty()) {
            return null;
        }
        Vehicle existingVehicle = existingVehicleOption.get();
        Optional<RentalVendor> optionalRentalVendor = rentalVendorRepository.findById(updateVehicleDto.getRentalVendorId());

        if(optionalRentalVendor.isEmpty()) {
            return null;
        }

        existingVehicle.setRentalVendor(optionalRentalVendor.get());
        existingVehicle.setRentalVendorId(updateVehicleDto.getRentalVendorId());
        existingVehicle.setBrand(updateVehicleDto.getBrand());
        existingVehicle.setModel(updateVehicleDto.getModel());
        existingVehicle.setProductionYear(updateVehicleDto.getProductionYear());
        existingVehicle.setLocation(updateVehicleDto.getLocation());
        existingVehicle.setLicensePlate(updateVehicleDto.getLicensePlate());
        existingVehicle.setCapacity(updateVehicleDto.getCapacity());
        existingVehicle.setTransmission(updateVehicleDto.getTransmission());
        existingVehicle.setFuelType(updateVehicleDto.getFuelType());
        existingVehicle.setPrice(updateVehicleDto.getPrice());
        existingVehicle.setStatus(updateVehicleDto.getStatus());
        existingVehicle.setUpdatedAt(LocalDateTime.now());
        
        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public Vehicle deleteVehicle(String id) {
        Vehicle vehicle = getVehicle(id);
        if (vehicle == null) return null;
        if ("In Use".equalsIgnoreCase(vehicle.getStatus()))
            throw new IllegalArgumentException("Cannot delete vehicle that is currently rented");

        vehicle.setDeletedAt(LocalDateTime.now());
        vehicle.setStatus("Unavailable");
        return vehicleRepository.save(vehicle);
    }


    private ReadVehicleDto convertToReadVehicleDto(Vehicle vehicle) {
        String rentalVendorName = "Unknown User";
        if (vehicle.getRentalVendor() != null) {
            rentalVendorName = vehicle.getRentalVendor().getName();
        }

        String timeAgo = "Unknown time";
        if (vehicle.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            long hours = ChronoUnit.HOURS.between(vehicle.getCreatedAt(), now);
            long days = ChronoUnit.DAYS.between(vehicle.getCreatedAt(), now);
            long weeks = days / 7;
            long months = days / 30;
            long years = days / 365;
            
            if (hours < 1) {
                timeAgo = "Just Now";
            } else if (hours < 24) {
                timeAgo = hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else if (days < 7) {
                timeAgo = days + " day" + (days > 1 ? "s" : "") + " ago";
            } else if (weeks < 4) {
                timeAgo = weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
            } else if (months < 12) {
                timeAgo = months + " month" + (months > 1 ? "s" : "") + " ago";
            } else {
                timeAgo = years + " year" + (years > 1 ? "s" : "") + " ago";
            }
        }

        return ReadVehicleDto.builder()
                .id(vehicle.getId())
                .rentalVendorId(vehicle.getRentalVendorId())
                .rentalVendorName(rentalVendorName)
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
                .timeAgo(timeAgo)
                .build();
    }

}