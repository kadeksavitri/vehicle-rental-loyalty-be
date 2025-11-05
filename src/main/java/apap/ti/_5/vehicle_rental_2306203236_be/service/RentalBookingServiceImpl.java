package apap.ti._5.vehicle_rental_2306203236_be.service;

import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.ReadRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.CreateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.UpdateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RentalBookingServiceImpl implements RentalBookingService {

    private final RentalBookingRepository rentalBookingRepository;
    private final VehicleRepository vehicleRepository;
    private final IdGenerator idGenerator;

    public RentalBookingServiceImpl(RentalBookingRepository rentalBookingRepository, VehicleRepository vehicleRepository, IdGenerator idGenerator) {
        this.rentalBookingRepository = rentalBookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public List<RentalBooking> getAllRentalBooking(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return rentalBookingRepository.findAllByDeletedAtIsNull(){
        return rentalBookingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
        };

        return rentalBookingRepository.findByIdContainingIgnoreCaseOrVehicleIdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(
            keyword.trim(), keyword.trim(), keyword.trim()
        );
    }

    @Override
    public List<ReadRentalBookingDto> getAllRentalBookingDto(String keyword) {
        List<RentalBooking> rentalBookings;

        if (keyword == null || keyword.trim().isEmpty()) {
            rentalBookings = rentalBookingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
        } else {
            rentalBookings = rentalBookingRepository.findByIdContainingIgnoreCaseOrVehicleIdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(
                keyword.trim(), keyword.trim(), keyword.trim()
            );
        }

        return rentalBookings.stream()
                .map(this::convertToReadRentalBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    public RentalBooking getRentalBooking(String id) {
        return rentalBookingRepository.findById(id).orElse(null);
    }

    @Override
    public RentalBooking createRentalBooking(CreateRentalBookingDto createRentalBookingDto) {

        Optional<RentalBooking> optional = rentalBookingRepository.findById(null) ;
        Vehicle vehicle = vehicleRepository.findById(createRentalBookingDto.getVehicleId()).orElseThrow();

        RentalBooking lastBooking = rentalBookingRepository.findLastestRentalBookingIncludingDeleted();
        String newId = idGenerator.generateRentalBookingId(lastBooking != null ? lastBooking.getId() : null);

        long days = Math.max(1, Duration.between(createRentalBookingDto.getPickUpTime(), createRentalBookingDto.getDropOffTime()).toDays());
        double basePrice = vehicle.getPrice() * days;
        double driverFee = createRentalBookingDto.isIncludeDriver() ? (days * 100000) : 0;

        RentalBooking booking = RentalBooking.builder()
                .id(newId)
                .vehicleId(vehicle) 
                .pickUpTime(createRentalBookingDto.getPickUpTime())
                .dropOffTime(createRentalBookingDto.getDropOffTime())
                .pickUpLocation(createRentalBookingDto.getPickUpLocation())
                .dropOffLocation(createRentalBookingDto.getDropOffLocation())
                .capacityNeeded(createRentalBookingDto.getCapacityNeeded())
                .transmissionNeeded(createRentalBookingDto.getTransmissionNeeded())
                .includeDriver(createRentalBookingDto.isIncludeDriver())
                .status("Upcoming")
                .totalPrice(basePrice + driverFee)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return rentalBookingRepository.save(booking);
    }

    private ReadRentalBookingDto convertToReadRentalBookingDto(RentalBooking rentalBooking) {
        return rentalBooking.builder()
                .id(rentalBooking.getId())
                .vehicleId(rentalBooking.getVehicleId())
                .pickUpTime(rentalBooking.getPickUpTime())
                .dropOffTime(rentalBooking.getDropOffTime())
                .pickUpLocation(rentalBooking.getPickUpLocation())
                .dropOffLocation(rentalBooking.getDropOffLocation())
                .capacityNeeded(rentalBooking.getCapacityNeeded())
                .transmissionNeeded(rentalBooking.getTransmissionNeeded())
                .includeDriver(rentalBooking.isIncludeDriver())
                .status(rentalBooking.getStatus())
                .totalPrice(rentalBooking.getTotalPrice())
                .listOfAddOns(
                        rentalBooking.getListOfAddOns() != null ?
                        rentalBooking.getListOfAddOns().stream().map(RentalAddOn::getName).collect(Collectors.toList()) :
                        List.of()
                )
                .build();
    }
}
