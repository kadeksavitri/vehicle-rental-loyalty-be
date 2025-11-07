package apap.ti._5.vehicle_rental_2306203236_be.service;

import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.ReadRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.CreateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.UpdateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RentalBookingServiceImpl implements RentalBookingService {

    private final RentalBookingRepository rentalBookingRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalAddOnRepository rentalAddOnRepository;
    private final IdGenerator idGenerator;

    public RentalBookingServiceImpl(RentalBookingRepository rentalBookingRepository, VehicleRepository vehicleRepository, RentalAddOnRepository rentalAddOnRepository, IdGenerator idGenerator) {
        this.rentalBookingRepository = rentalBookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalAddOnRepository = rentalAddOnRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public List<RentalBooking> getAllRentalBooking(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return rentalBookingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
        }

        return rentalBookingRepository.findByIdContainingIgnoreCaseOrVehicle_IdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(
            keyword.trim(), keyword.trim(), keyword.trim()
        );
    }

    @Override
    public List<ReadRentalBookingDto> getAllRentalBookingDto(String keyword) {
        List<RentalBooking> rentalBookings;

        if (keyword == null || keyword.trim().isEmpty()) {
            rentalBookings = rentalBookingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
        } else {
            rentalBookings = rentalBookingRepository.findByIdContainingIgnoreCaseOrVehicle_IdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(
                keyword.trim(), keyword.trim(), keyword.trim()
            );
        }

        return rentalBookings.stream()
                .map(this::convertToReadRentalBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public RentalBooking getRentalBooking(String id) {

        return rentalBookingRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
    }

    @Override
    public RentalBooking createRentalBooking(CreateRentalBookingDto createRentalBookingDto) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(createRentalBookingDto.getVehicleId());

        if (optionalVehicle.isEmpty()) {
            return null;
        }

        Vehicle vehicle = optionalVehicle.get();

        // Generate id
        RentalBooking lastBooking = rentalBookingRepository.findLastestRentalBookingIncludingDeleted();
        String newId = idGenerator.generateRentalBookingId(lastBooking != null ? lastBooking.getId() : null);

        // Hitung price
        long days = Math.max(1, (long) Math.ceil((double)Duration.between(createRentalBookingDto.getPickUpTime(), createRentalBookingDto.getDropOffTime()).toHours()/24));
        double basePrice = vehicle.getPrice() * days;
        double driverFee = createRentalBookingDto.isIncludeDriver() ? (days * 100000) : 0;

        List<RentalAddOn> selectedAddOns = (createRentalBookingDto.getListOfAddOns() != null)
                ? rentalAddOnRepository.findAll().stream()
                        .filter(a -> createRentalBookingDto.getListOfAddOns().contains(a.getId().toString()))
                        .toList()
                :List.of();

        double addOnTotal = selectedAddOns.stream().mapToDouble(RentalAddOn::getPrice).sum();

        RentalBooking booking = RentalBooking.builder()
                .id(newId)
                .vehicleId(vehicle.getId())
                .vehicle(vehicle)
                .pickUpTime(createRentalBookingDto.getPickUpTime())
                .dropOffTime(createRentalBookingDto.getDropOffTime())
                .pickUpLocation(createRentalBookingDto.getPickUpLocation())
                .dropOffLocation(createRentalBookingDto.getDropOffLocation())
                .capacityNeeded(createRentalBookingDto.getCapacityNeeded())
                .transmissionNeeded(createRentalBookingDto.getTransmissionNeeded())
                .includeDriver(createRentalBookingDto.isIncludeDriver())
                .status("Upcoming")
                .totalPrice(basePrice + driverFee + addOnTotal)
                .listOfAddOns(selectedAddOns)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Memastikan lokasi ada di list lokasi vendor 
        RentalVendor vendor = vehicle.getRentalVendor();

        if (vendor == null || vendor.getListOfLocations() == null) {
            throw new IllegalArgumentException("Vendor tidak memiliki daftar lokasi operasional");
        }

        if (!vendor.getListOfLocations().contains(createRentalBookingDto.getPickUpLocation()) ||
            !vendor.getListOfLocations().contains(createRentalBookingDto.getDropOffLocation())) {
            throw new IllegalArgumentException("Lokasi pengambilan atau pengembalian tidak tersedia di vendor ini");
        }

        if (createRentalBookingDto.getVehicleId() == null) {
            throw new IllegalArgumentException("Vehicle ID belum dipilih, tidak bisa membuat booking.");
        }

        return rentalBookingRepository.save(booking);
    }

    @Override
    public RentalBooking updateRentalBookingDetails(UpdateRentalBookingDto updateRentalBookingDto) {
        Optional<RentalBooking> optionalRentalBooking = rentalBookingRepository.findByIdAndDeletedAtIsNull(updateRentalBookingDto.getId());
        if (optionalRentalBooking.isEmpty()) return null;

        RentalBooking existingRentalBooking = optionalRentalBooking.get();
        Optional<Vehicle> optionalExistingVehicle = vehicleRepository.findById(updateRentalBookingDto.getVehicleId());
        if (optionalExistingVehicle.isEmpty()) return null;

        if (existingRentalBooking == null || !"Upcoming".equals(existingRentalBooking.getStatus())) return null;

        existingRentalBooking.setVehicle(optionalExistingVehicle.get());
        existingRentalBooking.setVehicleId(updateRentalBookingDto.getVehicleId());
        existingRentalBooking.setPickUpLocation(updateRentalBookingDto.getPickUpLocation());
        existingRentalBooking.setDropOffLocation(updateRentalBookingDto.getDropOffLocation());
        existingRentalBooking.setPickUpTime(updateRentalBookingDto.getPickUpTime());
        existingRentalBooking.setDropOffTime(updateRentalBookingDto.getDropOffTime());
        existingRentalBooking.setCapacityNeeded(updateRentalBookingDto.getCapacityNeeded());
        existingRentalBooking.setTransmissionNeeded(updateRentalBookingDto.getTransmissionNeeded());
        existingRentalBooking.setIncludeDriver(updateRentalBookingDto.isIncludeDriver());

        long days = Math.max(1, (long) Math.ceil(
                (double) Duration.between(updateRentalBookingDto.getPickUpTime(), updateRentalBookingDto.getDropOffTime()).toHours() / 24
        ));
        double basePrice = existingRentalBooking.getVehicle().getPrice() * days;
        double driverFee = updateRentalBookingDto.isIncludeDriver() ? (days * 100000) : 0;

        existingRentalBooking.setTotalPrice(basePrice + driverFee);
        existingRentalBooking.setUpdatedAt(LocalDateTime.now());

        if (updateRentalBookingDto.getVehicleId() == null) {
            throw new IllegalArgumentException("Vehicle belum dipilih, tidak bisa update booking.");
        }

        return rentalBookingRepository.save(existingRentalBooking);
    }

    @Override
    public RentalBooking updateRentalBookingStatus(String id, String newStatus) {
        RentalBooking booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (booking == null) return null;

        Vehicle vehicle = booking.getVehicle();
        LocalDateTime now = LocalDateTime.now();

        boolean hasOngoing = rentalBookingRepository
            .findAllByVehicleAndDeletedAtIsNull(vehicle)
            .stream()
            .anyMatch(b -> "Ongoing".equals(b.getStatus()) && !b.getId().equals(booking.getId()));
        if (hasOngoing) return null;

        if ("Done".equals(booking.getStatus())) {
            return null;
        }

        if ("Upcoming".equals(booking.getStatus()) && "Done".equals(newStatus)) {
            return null;
        }

        // Ubah dari Upcoming ke Ongoing
        if ("Upcoming".equals(booking.getStatus()) && "Ongoing".equals(newStatus)) {
            boolean isTimeValid = now.isAfter(booking.getPickUpTime()) && now.isBefore(booking.getDropOffTime());
            boolean isVehicleAvailable = "Available".equalsIgnoreCase(vehicle.getStatus());
            boolean isLocationMatch = vehicle.getLocation().equalsIgnoreCase(booking.getPickUpLocation());

            if (isTimeValid && isVehicleAvailable && isLocationMatch) {
                booking.setStatus("Ongoing");
                vehicle.setStatus("In Use");
            } else {
                return null; 
            }
        }

        // Ubah Ongoing ke Done
        else if ("Ongoing".equals(booking.getStatus()) && "Done".equals(newStatus)) {
            long hoursLate = Math.max(0,
                (long) Math.ceil((double) Duration.between(booking.getDropOffTime(), now).toMinutes() / 60)
            );
            double penalty = hoursLate > 0 ? hoursLate * 20000 : 0;

            booking.setStatus("Done");
            booking.setTotalPrice(booking.getTotalPrice() + penalty);
            vehicle.setStatus("Available");
            vehicle.setLocation(booking.getDropOffLocation());
        }

        booking.setUpdatedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
        return rentalBookingRepository.save(booking);
    }


    @Override
    public RentalBooking updateRentalBookingAddOn(UpdateRentalBookingDto updateRentalBookingAddOnDto) {
        RentalBooking booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(updateRentalBookingAddOnDto.getId()).orElse(null);
        if (booking == null || !"Upcoming".equals(booking.getStatus())) return null;

        List<RentalAddOn> addons = rentalAddOnRepository.findAll().stream()
            .filter(a -> updateRentalBookingAddOnDto.getListOfAddOns().contains(a.getId().toString()))
            .collect(Collectors.toList()); // mutable list

        booking.setListOfAddOns(addons);

        long days = Math.max(1, (long) Math.ceil(
                (double) Duration.between(booking.getPickUpTime(), booking.getDropOffTime()).toHours() / 24
        ));
        double basePrice = booking.getVehicle().getPrice() * days;
        double driverFee = booking.isIncludeDriver() ? (days * 100000) : 0;
        double addOnTotal = addons.stream().mapToDouble(RentalAddOn::getPrice).sum();

        booking.setTotalPrice(basePrice + driverFee + addOnTotal);
        booking.setUpdatedAt(LocalDateTime.now());
        return rentalBookingRepository.save(booking);
    }

    @Override
    public RentalBooking deleteRentalBooking(String id) {
        RentalBooking existingRentalBooking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (existingRentalBooking == null || !"Upcoming".equals(existingRentalBooking.getStatus())) return null;

        Vehicle vehicle = existingRentalBooking.getVehicle();
        vehicle.setStatus("Available");
        existingRentalBooking.setStatus("Done");

        if (LocalDateTime.now().isBefore(existingRentalBooking.getPickUpTime())) {
            existingRentalBooking.setTotalPrice(0.0);
        }

        existingRentalBooking.setDeletedAt(LocalDateTime.now());
        rentalBookingRepository.save(existingRentalBooking);
        vehicleRepository.save(vehicle);
        return existingRentalBooking;
    }

    private ReadRentalBookingDto convertToReadRentalBookingDto(RentalBooking rentalBooking) {
        String vehicleBrand = "Unknown Vehicle Brand";
        String vehicleType = "Unknown Vehicle Type";

        if (rentalBooking.getVehicle() != null) {
            vehicleBrand = rentalBooking.getVehicle().getBrand();
            vehicleType = rentalBooking.getVehicle().getType();
        }

        return ReadRentalBookingDto.builder()
                .id(rentalBooking.getId())
                .vehicleId(rentalBooking.getVehicleId())                
                .vehicleBrand(vehicleBrand)
                .vehicleType(vehicleType)
                .pickUpTime(rentalBooking.getPickUpTime())
                .dropOffTime(rentalBooking.getDropOffTime())
                .pickUpLocation(rentalBooking.getPickUpLocation())
                .dropOffLocation(rentalBooking.getDropOffLocation())
                .capacityNeeded(rentalBooking.getCapacityNeeded())
                .transmissionNeeded(rentalBooking.getTransmissionNeeded())
                .includeDriver(rentalBooking.isIncludeDriver())
                .status(rentalBooking.getStatus())
                .totalPrice(rentalBooking.getTotalPrice())
                .ListOfAddOns(
                        rentalBooking.getListOfAddOns() != null ?
                        rentalBooking.getListOfAddOns().stream().map(RentalAddOn::getName).collect(Collectors.toList()) :
                        List.of()
                )
                .build();
    }

    // Mengecek kendaraan sedang dibooking di rentang waktu booking
    public boolean isVehicleAvailableDuringPeriod(Vehicle vehicle, LocalDateTime requestedPickUp, LocalDateTime requestedDropOff) {
        List<RentalBooking> activeBookings = rentalBookingRepository
            .findAllByVehicleAndDeletedAtIsNull(vehicle)
            .stream()
            .filter(b -> !"Done".equalsIgnoreCase(b.getStatus()))
            .toList();

        for (RentalBooking booking : activeBookings) {
            boolean overlap =
                requestedPickUp.isBefore(booking.getDropOffTime()) &&
                requestedDropOff.isAfter(booking.getPickUpTime());
            if (overlap) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Object[]> getBookingStatistics(String period, int year) {
        List<RentalBooking> bookings = rentalBookingRepository.findAllByDeletedAtIsNull();

        int[] counts;
        String[] labels;

        if ("Quarterly".equalsIgnoreCase(period)) {
            counts = new int[4];
            labels = new String[]{"Q1", "Q2", "Q3", "Q4"};

            for (RentalBooking booking : bookings) {
                if (booking.getCreatedAt().getYear() == year) {
                    int month = booking.getCreatedAt().getMonthValue();
                    int quarterIndex = (month - 1) / 3;
                    counts[quarterIndex]++;
                }
            }
        } else { // Monthly
            counts = new int[12];
            labels = new String[]{
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            };

            for (RentalBooking booking : bookings) {
                if (booking.getCreatedAt().getYear() == year) {
                    int monthIndex = booking.getCreatedAt().getMonthValue() - 1;
                    counts[monthIndex]++;
                }
            }
        }

        List<Object[]> result = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            result.add(new Object[]{labels[i], counts[i]});
        }
        return result;
    }


}
