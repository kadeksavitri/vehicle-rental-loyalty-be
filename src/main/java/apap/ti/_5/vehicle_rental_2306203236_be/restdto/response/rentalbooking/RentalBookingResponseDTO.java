package apap.ti._5.vehicle_rental_2306203236_be.restdto.response.rentalbooking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentalBookingResponseDTO {
    private String id;
    private String customerId;
    private String vehicleId;
    private String vehicleBrand;
    private String vehicleType;
    private LocalDateTime pickUpTime;
    private LocalDateTime dropOffTime;
    private String pickUpLocation;
    private String dropOffLocation;
    private Integer capacityNeeded;
    private String transmissionNeeded;
    private boolean includeDriver;
    private String status;
    private String billStatus;
    private Double totalPrice;
    private List<String> listOfAddOns; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
