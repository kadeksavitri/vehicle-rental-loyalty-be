package apap.ti._5.vehicle_rental_2306203236_be.util;

import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PriceCalculator {

    public double calculateTotal(double pricePerDay, LocalDateTime start, LocalDateTime end, boolean includeDriver, List<RentalAddOn> addOns) {
        long days = Duration.between(start, end).toDays();
        if (days <= 0) days = 1;
        double base = pricePerDay * days;
        double driver = includeDriver ? 100000 * days : 0;
        double addOnTotal = addOns != null ? addOns.stream().mapToDouble(RentalAddOn::getPrice).sum() : 0;
        return base + driver + addOnTotal;
    }
}
