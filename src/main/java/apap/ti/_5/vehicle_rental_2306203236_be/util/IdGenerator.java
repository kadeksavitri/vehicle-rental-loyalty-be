package apap.ti._5.vehicle_rental_2306203236_be.util;

import org.springframework.stereotype.Component;

@Component
public class IdGenerator {
    public String generateVehicleId(String lastId) {
        if (lastId == null || lastId.isEmpty()) return "VEH0001";
        int lastNum = Integer.parseInt(lastId.substring(3));
        return String.format("VEH%04d", lastNum + 1);
    }
    public String generateRentalBookingId(String lastId) {
        if (lastId == null || lastId.isEmpty()) return "VR000001";
        int lastNum = Integer.parseInt(lastId.substring(3));
        return String.format("VR%06d", lastNum + 1);
    }
}
