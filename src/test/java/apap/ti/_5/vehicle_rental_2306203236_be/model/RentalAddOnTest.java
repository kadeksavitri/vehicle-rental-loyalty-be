package apap.ti._5.vehicle_rental_2306203236_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RentalAddOnTest {

    private RentalAddOn addOn;

    @BeforeEach
    void setUp() {
        addOn = new RentalAddOn();
        addOn.setId(UUID.randomUUID());
        addOn.setName("GPS Navigation");
        addOn.setPrice(150000.0);
        addOn.setCreatedAt(LocalDateTime.now());
        addOn.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testBuilderAndGetters() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        RentalAddOn built = RentalAddOn.builder()
                .id(id)
                .name("Child Seat")
                .price(200000.0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(id, built.getId());
        assertEquals("Child Seat", built.getName());
        assertEquals(200000.0, built.getPrice());
        assertEquals(now, built.getCreatedAt());
        assertEquals(now, built.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        UUID newId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        RentalAddOn newAddOn = new RentalAddOn();
        newAddOn.setId(newId);
        newAddOn.setName("Wi-Fi Hotspot");
        newAddOn.setPrice(300000.0);
        newAddOn.setCreatedAt(now);
        newAddOn.setUpdatedAt(now.plusHours(1));

        assertEquals(newId, newAddOn.getId());
        assertEquals("Wi-Fi Hotspot", newAddOn.getName());
        assertEquals(300000.0, newAddOn.getPrice());
        assertEquals(now, newAddOn.getCreatedAt());
        assertEquals(now.plusHours(1), newAddOn.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        RentalAddOn a1 = addOn;
        RentalAddOn a2 = addOn;

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void testToStringContainsImportantFields() {
        String s = addOn.toString();
        assertTrue(s.contains("GPS Navigation"));
        assertTrue(s.contains("price"));
    }
}
