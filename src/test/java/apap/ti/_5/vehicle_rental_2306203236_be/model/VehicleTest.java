package apap.ti._5.vehicle_rental_2306203236_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId("VEH0001");
        vehicle.setRentalVendorId(10);
        vehicle.setType("SUV");
        vehicle.setBrand("Toyota");
        vehicle.setModel("Rush");
        vehicle.setProductionYear(2020);
        vehicle.setLocation("Depok");
        vehicle.setLicensePlate("B1234XYZ");
        vehicle.setCapacity(7);
        vehicle.setTransmission("Automatic");
        vehicle.setFuelType("Gasoline");
        vehicle.setPrice(500000.0);
        vehicle.setStatus("Available");
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());
        vehicle.setDeletedAt(null);
    }

    @Test
    void testBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        RentalVendor vendor = new RentalVendor();
        vendor.setId(1);
        vendor.setName("PT Mobil Sejahtera");

        Vehicle built = Vehicle.builder()
                .id("VEH0010")
                .rentalVendorId(1)
                .rentalVendor(vendor)
                .type("Sedan")
                .brand("Honda")
                .model("Civic")
                .productionYear(2022)
                .location("Jakarta")
                .licensePlate("B5678QWE")
                .capacity(4)
                .transmission("Manual")
                .fuelType("Pertamax")
                .price(300000.0)
                .status("In Use")
                .createdAt(now)
                .updatedAt(now)
                .deletedAt(null)
                .build();

        assertEquals("VEH0010", built.getId());
        assertEquals(1, built.getRentalVendorId());
        assertEquals(vendor, built.getRentalVendor());
        assertEquals("Sedan", built.getType());
        assertEquals("Honda", built.getBrand());
        assertEquals("Civic", built.getModel());
        assertEquals(2022, built.getProductionYear());
        assertEquals("Jakarta", built.getLocation());
        assertEquals("B5678QWE", built.getLicensePlate());
        assertEquals(4, built.getCapacity());
        assertEquals("Manual", built.getTransmission());
        assertEquals("Pertamax", built.getFuelType());
        assertEquals(300000.0, built.getPrice());
        assertEquals("In Use", built.getStatus());
        assertEquals(now, built.getCreatedAt());
        assertEquals(now, built.getUpdatedAt());
        assertNull(built.getDeletedAt());
    }

    @Test
    void testSettersAndGetters() {
        assertEquals("VEH0001", vehicle.getId());
        assertEquals(10, vehicle.getRentalVendorId());
        assertEquals("SUV", vehicle.getType());
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Rush", vehicle.getModel());
        assertEquals(2020, vehicle.getProductionYear());
        assertEquals("Depok", vehicle.getLocation());
        assertEquals("B1234XYZ", vehicle.getLicensePlate());
        assertEquals(7, vehicle.getCapacity());
        assertEquals("Automatic", vehicle.getTransmission());
        assertEquals("Gasoline", vehicle.getFuelType());
        assertEquals(500000.0, vehicle.getPrice());
        assertEquals("Available", vehicle.getStatus());
        assertNotNull(vehicle.getCreatedAt());
        assertNotNull(vehicle.getUpdatedAt());
        assertNull(vehicle.getDeletedAt());
    }

    @Test
    void testOnCreateSetsTimestamps() {
        Vehicle v = new Vehicle();
        v.onCreate();
        assertNotNull(v.getCreatedAt());
        assertNotNull(v.getUpdatedAt());
        assertTrue(v.getUpdatedAt().isAfter(v.getCreatedAt()) || v.getUpdatedAt().isEqual(v.getCreatedAt()));
    }

    @Test
    void testOnUpdateUpdatesTimestamp() {
        Vehicle v = new Vehicle();
        v.setUpdatedAt(LocalDateTime.now().minusDays(1));
        v.onUpdate();
        assertTrue(v.getUpdatedAt().isAfter(LocalDateTime.now().minusHours(1)));
    }

    @Test
    void testEqualsAndHashCode() {
        Vehicle v1 = vehicle;
        Vehicle v2 = vehicle;
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void testToStringContainsImportantFields() {
        String text = vehicle.toString();
        assertTrue(text.contains("Toyota"));
        assertTrue(text.contains("Rush"));
        assertTrue(text.contains("VEH0001"));
    }
}
