package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.config.security.CurrentUser;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.*;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.rentalbooking.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.service.RentalVendorService;
import apap.ti._5.vehicle_rental_2306203236_be.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalBookingRestServiceImpl implements RentalBookingRestService {

    private final RentalBookingRepository rentalBookingRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalAddOnRepository rentalAddOnRepository;
    private final RentalVendorService rentalVendorService;
    private final IdGenerator idGenerator;

    public RentalBookingRestServiceImpl(
            RentalBookingRepository rentalBookingRepository,
            VehicleRepository vehicleRepository,
            RentalAddOnRepository rentalAddOnRepository,
            RentalVendorService rentalVendorService,
            IdGenerator idGenerator
    ) {
        this.rentalBookingRepository = rentalBookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalAddOnRepository = rentalAddOnRepository;
        this.rentalVendorService = rentalVendorService;
        this.idGenerator = idGenerator;
    }

    @Override
    public RentalBookingResponseDTO createRentalBooking(CreateRentalBookingRequestDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
            .orElseThrow(() -> new IllegalArgumentException("Vehicle ID tidak ditemukan atau null"));

        RentalBooking lastBooking = rentalBookingRepository.findLastestRentalBookingIncludingDeleted();
        String newId = idGenerator.generateRentalBookingId(lastBooking != null ? lastBooking.getId() : null);

        List<RentalAddOn> addons = new ArrayList<>();
        if (dto.getListOfAddOns() != null && !dto.getListOfAddOns().isEmpty()) {
            addons = dto.getListOfAddOns().stream()
                .map(addOnId -> rentalAddOnRepository.findById(UUID.fromString(addOnId))
                    .orElseThrow(() -> new IllegalArgumentException("Add-on dengan ID " + addOnId + " tidak ditemukan")))
                .collect(Collectors.toList());
        }

        long days = Math.max(1, (long) Math.ceil(Duration.between(dto.getPickUpTime(), dto.getDropOffTime()).toHours() / 24.0));
        double basePrice = vehicle.getPrice() * days;
        double driverFee = dto.isIncludeDriver() ? (days * 100000) : 0;
        double addOnTotal = addons.stream().mapToDouble(RentalAddOn::getPrice).sum();

        double totalPrice = basePrice + driverFee + addOnTotal;

        RentalBooking booking = RentalBooking.builder()
            .id(newId)
            .customerId(dto.getCustomerId())
            .vehicle(vehicle)
            .vehicleId(dto.getVehicleId())
            .pickUpTime(dto.getPickUpTime())
            .dropOffTime(dto.getDropOffTime())
            .pickUpLocation(dto.getPickUpLocation())
            .dropOffLocation(dto.getDropOffLocation())
            .capacityNeeded(dto.getCapacityNeeded())
            .transmissionNeeded(dto.getTransmissionNeeded())
            .includeDriver(dto.isIncludeDriver())
            .status("Upcoming")
            .totalPrice(totalPrice)  
            .listOfAddOns(addons)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        rentalBookingRepository.save(booking);
        return convertToResponseDTO(booking);
    }

    @Override
    public List<RentalBookingResponseDTO> getAllRentalBookings() {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        String userId = CurrentUser.getUserId();

        List<RentalBooking> bookings;
        
        if ("ROLE_SUPERADMIN".equals(role)) {
            // Superadmin sees all bookings
            bookings = rentalBookingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
        } else if ("ROLE_RENTAL_VENDOR".equals(role)) {
            // Rental vendor sees only their bookings
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            bookings = rentalBookingRepository.findAllByVendorId(vendor.getId());
        } else if ("ROLE_CUSTOMER".equals(role)) {
            // Customer sees only their bookings
            bookings = rentalBookingRepository.findAllByCustomerIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
        } else {
            throw new IllegalArgumentException("Role tidak valid");
        }
        
        return bookings.stream().map(this::convertToResponseDTO).toList();
    }

    @Override
    public List<RentalBookingResponseDTO> getAllRentalBookingsByKeyword(String keyword) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        String userId = CurrentUser.getUserId();

        List<RentalBooking> bookings;
        
        if ("ROLE_SUPERADMIN".equals(role)) {
            // Superadmin sees all bookings with keyword
            if (keyword == null || keyword.trim().isEmpty()) {
                bookings = rentalBookingRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
            } else {
                bookings = rentalBookingRepository.findByIdContainingIgnoreCaseOrVehicle_IdContainingIgnoreCaseOrPickUpLocationContainingIgnoreCase(
                        keyword.trim(), keyword.trim(), keyword.trim());
            }
        } else if ("ROLE_RENTAL_VENDOR".equals(role)) {
            // Rental vendor sees only their bookings
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            bookings = rentalBookingRepository.findAllByVendorId(vendor.getId());
            if (keyword != null && !keyword.trim().isEmpty()) {
                String lowerKeyword = keyword.trim().toLowerCase();
                bookings = bookings.stream()
                    .filter(b -> b.getId().toLowerCase().contains(lowerKeyword) ||
                                b.getVehicleId().toLowerCase().contains(lowerKeyword) ||
                                b.getPickUpLocation().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
            }
        } else if ("ROLE_CUSTOMER".equals(role)) {
            // Customer sees only their bookings
            bookings = rentalBookingRepository.findAllByCustomerIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String lowerKeyword = keyword.trim().toLowerCase();
                bookings = bookings.stream()
                    .filter(b -> b.getId().toLowerCase().contains(lowerKeyword) ||
                                b.getVehicleId().toLowerCase().contains(lowerKeyword) ||
                                b.getPickUpLocation().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
            }
        } else {
            throw new IllegalArgumentException("Role tidak valid");
        }
        
        return bookings.stream().map(this::convertToResponseDTO).toList();
    }

    @Override
    public RentalBookingResponseDTO getRentalBooking(String id) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();
        String userId = CurrentUser.getUserId();

        RentalBooking booking;
        
        if ("ROLE_SUPERADMIN".equals(role)) {
            // Superadmin can access any booking
            booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        } else if ("ROLE_RENTAL_VENDOR".equals(role)) {
            // Rental vendor can only access their bookings
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            booking = rentalBookingRepository.findByIdAndVendorId(id, vendor.getId()).orElse(null);
        } else if ("ROLE_CUSTOMER".equals(role)) {
            // Customer can only access their bookings
            booking = rentalBookingRepository.findByIdAndCustomerIdAndDeletedAtIsNull(id, userId).orElse(null);
        } else {
            throw new IllegalArgumentException("Role tidak valid");
        }
        
        if (booking == null) throw new IllegalArgumentException("Booking tidak ditemukan atau Anda tidak memiliki akses");
        return convertToResponseDTO(booking);
    }

    @Override
    public RentalBookingResponseDTO updateRentalBookingDetails(UpdateRentalBookingRequestDTO dto) {
        String role = CurrentUser.getRole();
        String userId = CurrentUser.getUserId();

        RentalBooking booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking tidak ditemukan"));
        
        // Check ownership - only customer who owns the booking or superadmin can update
        if ("ROLE_CUSTOMER".equals(role) && !booking.getCustomerId().equals(userId)) {
            throw new IllegalArgumentException("Anda tidak memiliki akses untuk mengubah booking ini");
        }
        
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            throw new IllegalArgumentException("Rental vendor tidak dapat mengubah detail booking");
        }
        
        if (!"Upcoming".equals(booking.getStatus()))
            throw new IllegalStateException("Booking tidak bisa diubah, status bukan 'Upcoming'");

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle tidak ditemukan"));

        List<RentalAddOn> addons = new ArrayList<>();

        if (dto.getListOfAddOns() != null && !dto.getListOfAddOns().isEmpty()) {
            addons = dto.getListOfAddOns().stream()
                    .map(addOnId -> rentalAddOnRepository.findById(UUID.fromString(addOnId))
                            .orElseThrow(() -> new IllegalArgumentException("Add-on dengan ID " + addOnId + " tidak ditemukan")))
                    .collect(Collectors.toList());
        }
        booking.setListOfAddOns(addons);

        booking.setCustomerId(dto.getCustomerId());
        booking.setVehicle(vehicle);
        booking.setVehicleId(dto.getVehicleId());
        booking.setPickUpLocation(dto.getPickUpLocation());
        booking.setDropOffLocation(dto.getDropOffLocation());
        booking.setPickUpTime(dto.getPickUpTime());
        booking.setDropOffTime(dto.getDropOffTime());
        booking.setCapacityNeeded(dto.getCapacityNeeded());
        booking.setTransmissionNeeded(dto.getTransmissionNeeded());
        booking.setIncludeDriver(dto.isIncludeDriver());
        booking.setListOfAddOns(addons);

        long days = Math.max(1, (long) Math.ceil(Duration.between(dto.getPickUpTime(), dto.getDropOffTime()).toHours() / 24.0));
        double basePrice = vehicle.getPrice() * days;
        double driverFee = dto.isIncludeDriver() ? (days * 100000) : 0;
        double addOnTotal = addons.stream().mapToDouble(RentalAddOn::getPrice).sum();

        booking.setTotalPrice(basePrice + driverFee + addOnTotal);
        booking.setUpdatedAt(LocalDateTime.now());

        return convertToResponseDTO(rentalBookingRepository.save(booking));
    }

    @Override
    public RentalBookingResponseDTO updateRentalBookingStatus(String id, String newStatus) {
        String role = CurrentUser.getRole();
        String email = CurrentUser.getEmail();

        RentalBooking booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking tidak ditemukan"));
        
        // Check ownership - only vendor who owns the vehicle or superadmin can update status
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            RentalVendor vendor = rentalVendorService.getOrCreateVendor(email);
            if (!booking.getVehicle().getRentalVendor().getId().equals(vendor.getId())) {
                throw new IllegalArgumentException("Anda tidak memiliki akses untuk mengubah status booking ini");
            }
        }
        
        Vehicle vehicle = booking.getVehicle();
        LocalDateTime now = LocalDateTime.now();

        if ("Done".equals(booking.getStatus()))
            throw new IllegalStateException("Booking sudah selesai");
        if ("Upcoming".equals(booking.getStatus()) && "Done".equals(newStatus))
            throw new IllegalStateException("Tidak bisa langsung ubah Upcoming ke Done");

        if ("Upcoming".equals(booking.getStatus()) && "Ongoing".equals(newStatus)) {
            boolean isTimeValid = now.isAfter(booking.getPickUpTime()) && now.isBefore(booking.getDropOffTime());
            boolean isVehicleAvailable = "Available".equalsIgnoreCase(vehicle.getStatus());
            boolean isLocationMatch = vehicle.getLocation().equalsIgnoreCase(booking.getPickUpLocation());
            boolean hasOngoing = rentalBookingRepository.findAllByVehicleAndDeletedAtIsNull(vehicle)
                    .stream().anyMatch(b -> "Ongoing".equals(b.getStatus()) && !b.getId().equals(booking.getId()));

            if (!isTimeValid || !isVehicleAvailable || !isLocationMatch || hasOngoing)
                throw new IllegalStateException("Kendaraan tidak tersedia atau waktu tidak valid");

            booking.setStatus("Ongoing");
            vehicle.setStatus("In Use");
        }

        else if ("Ongoing".equals(booking.getStatus()) && "Done".equals(newStatus)) {
            double penalty = 0;
            if (now.isAfter(booking.getDropOffTime())) {
                long minutesLate = Duration.between(booking.getDropOffTime(), now).toMinutes();
                long hoursLate = (long) Math.ceil(minutesLate / 60.0);
                penalty = hoursLate * 20000;
            }

            booking.setStatus("Done");
            booking.setTotalPrice(booking.getTotalPrice() + penalty);
            vehicle.setStatus("Available");
            vehicle.setLocation(booking.getDropOffLocation());
        }

        booking.setUpdatedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
        return convertToResponseDTO(rentalBookingRepository.save(booking));
    }

    @Override
    public RentalBookingResponseDTO updateRentalBookingAddOn(UpdateRentalBookingAddOnRequestDTO dto){
        String role = CurrentUser.getRole();
        String userId = CurrentUser.getUserId();

        RentalBooking booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking tidak ditemukan"));
        
        // Check ownership - only customer who owns the booking or superadmin can update
        if ("ROLE_CUSTOMER".equals(role) && !booking.getCustomerId().equals(userId)) {
            throw new IllegalArgumentException("Anda tidak memiliki akses untuk mengubah booking ini");
        }
        
        if (!"Upcoming".equals(booking.getStatus()))
            throw new IllegalStateException("Add-ons hanya bisa diubah saat status Upcoming");

        List<RentalAddOn> addons = new ArrayList<>();
        if (dto.getListOfAddOns() != null && !dto.getListOfAddOns().isEmpty()) {
            addons = dto.getListOfAddOns().stream()
                    .map(addOnId -> rentalAddOnRepository.findById(UUID.fromString(addOnId))
                            .orElseThrow(() -> new IllegalArgumentException("Add-on dengan ID " + addOnId + " tidak ditemukan")))
                    .collect(Collectors.toList());
        }

        booking.setListOfAddOns(addons);

        long days = Math.max(1, (long) Math.ceil(Duration.between(booking.getPickUpTime(), booking.getDropOffTime()).toHours() / 24.0));
        double basePrice = booking.getVehicle().getPrice() * days;
        double driverFee = booking.isIncludeDriver() ? (days * 100000) : 0;
        double addOnTotal = addons.stream().mapToDouble(RentalAddOn::getPrice).sum();

        booking.setTotalPrice(basePrice + driverFee + addOnTotal);
        booking.setUpdatedAt(LocalDateTime.now());

        return convertToResponseDTO(rentalBookingRepository.save(booking));
    }

    @Override
    public RentalBookingResponseDTO deleteRentalBooking(DeleteRentalBookingRequestDTO dto) {
        String role = CurrentUser.getRole();
        String userId = CurrentUser.getUserId();

        RentalBooking booking = rentalBookingRepository.findByIdAndDeletedAtIsNull(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking tidak ditemukan"));
        
        // Check ownership - only customer who owns the booking or superadmin can delete
        if ("ROLE_CUSTOMER".equals(role) && !booking.getCustomerId().equals(userId)) {
            throw new IllegalArgumentException("Anda tidak memiliki akses untuk menghapus booking ini");
        }
        
        if ("ROLE_RENTAL_VENDOR".equals(role)) {
            throw new IllegalArgumentException("Rental vendor tidak dapat menghapus booking");
        }
        
        if (!"Upcoming".equals(booking.getStatus()))
            throw new IllegalStateException("Hanya booking Upcoming yang dapat dihapus");

        Vehicle vehicle = booking.getVehicle();
        vehicle.setStatus("Available");
        booking.setStatus("Done");

        if (LocalDateTime.now().isBefore(booking.getPickUpTime())) booking.setTotalPrice(0.0);
        booking.setDeletedAt(LocalDateTime.now());

        rentalBookingRepository.save(booking);
        vehicleRepository.save(vehicle);

        return convertToResponseDTO(booking);
    }

    @Override
    public List<Object[]> getRentalBookingStatistics(ChartRentalBookingRequestDTO chartRequest) {
        String period = chartRequest.getPeriod();
        int year = chartRequest.getYear();
        List<RentalBooking> bookings = rentalBookingRepository.findAllByDeletedAtIsNull();

        int[] counts;
        String[] labels;

        if ("Quarterly".equalsIgnoreCase(period)) {
            counts = new int[4];
            labels = new String[]{"Q1", "Q2", "Q3", "Q4"};
            for (RentalBooking booking : bookings) {
                if (booking.getCreatedAt() == null) continue; 
                if (booking.getCreatedAt().getYear() == year) {
                    int month = booking.getCreatedAt().getMonthValue();
                    int quarterIndex = (month - 1) / 3;
                    counts[quarterIndex]++;
                }
            }
        } else {
            counts = new int[12];
            labels = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
            for (RentalBooking booking : bookings) {
                if (booking.getCreatedAt() == null) continue; 
                if (booking.getCreatedAt().getYear() == year) 
                counts[booking.getCreatedAt().getMonthValue() - 1]++;
            }
        }

        List<Object[]> result = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) result.add(new Object[]{labels[i], counts[i]});
        return result;
    }

    private RentalBookingResponseDTO convertToResponseDTO(RentalBooking booking) {
        if (booking == null) return null;

        return RentalBookingResponseDTO.builder()
                .id(booking.getId())
                .customerId(booking.getCustomerId())
                .vehicleId(booking.getVehicleId())
                .vehicleBrand(booking.getVehicle() != null ? booking.getVehicle().getBrand() : "Unknown")
                .vehicleType(booking.getVehicle() != null ? booking.getVehicle().getType() : "Unknown")
                .pickUpTime(booking.getPickUpTime())
                .dropOffTime(booking.getDropOffTime())
                .pickUpLocation(booking.getPickUpLocation())
                .dropOffLocation(booking.getDropOffLocation())
                .capacityNeeded(booking.getCapacityNeeded())
                .transmissionNeeded(booking.getTransmissionNeeded())
                .includeDriver(booking.isIncludeDriver())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .listOfAddOns(
                        booking.getListOfAddOns() != null ?
                                booking.getListOfAddOns().stream().map(RentalAddOn::getName).toList() : List.of()
                )
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
