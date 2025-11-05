package apap.ti._5.vehicle_rental_2306203236_be.dto.booking;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateRentalBookingDto {

    @NotNull (message = "id is required")
    private String id;

    @NotNull (message = "vehicleId is required")
    private String vehicleId;

    @NotNull (message = "pickupTime is required")
    private LocalDateTime pickUpTime;

    @NotNull (message = "dropOffTime is required")
    private LocalDateTime dropOffTime;

    @NotNull (message = "Pick up location is required")
    private String pickUpLocation;

    @NotNull (message = "Drop off location is required")
    private String dropOffLocation;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 100, message = "Capacity cannot exceed 20")
    private Integer capacityNeeded;

    @NotNull (message = "Transmission required")
    private String transmissionNeeded;

    @NotNull (message = "Total price is required")
    private Double totalPrice;

    private List<String> ListOfAddOns;

    private boolean includeDriver;

    @NotNull (message = "Status is required")
    private String status;
}
