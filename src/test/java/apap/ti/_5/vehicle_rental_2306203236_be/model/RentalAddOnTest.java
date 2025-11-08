package apap.ti._5.vehicle_rental_2306203236_be.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RentalAddOnTest {

    private RentalAddOn addOn;
    private RentalBooking booking;

    @BeforeEach
    void setUp() {
        booking = RentalBooking.builder()
                .id("BK001")
                .vehicleId("VH001")
                .pickUpLocation("Depok")
                .dropOffLocation("Jakarta")
                .pickUpTime(LocalDateTime.of(2025, 11, 10, 8, 0))
                .dropOffTime(LocalDateTime.of(2025, 11, 12, 8, 0))
                .status("Upcoming")
                .totalPrice(1500000.0)
                .build();

        addOn = RentalAddOn.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Car Seat")
                .price(100000.0)
                .listOfBookings(List.of(booking))
                .createdAt(LocalDateTime.of(2025, 11, 7, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 11, 7, 12, 0))
                .build();
    }

    @Test
    void testBuilderAndGetters() {
        assertThat(addOn.getId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(addOn.getName()).isEqualTo("Car Seat");
        assertThat(addOn.getPrice()).isEqualTo(100000.0);
        assertThat(addOn.getListOfBookings()).hasSize(1);
        assertThat(addOn.getCreatedAt()).isNotNull();
        assertThat(addOn.getUpdatedAt()).isNotNull();
    }

    @Test
    void testSettersMutability() {
        UUID newId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        addOn.setId(newId);
        addOn.setName("GPS");
        addOn.setPrice(50000.0);
        addOn.setListOfBookings(Collections.emptyList());
        LocalDateTime now = LocalDateTime.now();
        addOn.setCreatedAt(now);
        addOn.setUpdatedAt(now);

        assertThat(addOn.getId()).isEqualTo(newId);
        assertThat(addOn.getName()).isEqualTo("GPS");
        assertThat(addOn.getPrice()).isEqualTo(50000.0);
        assertThat(addOn.getListOfBookings()).isEmpty();
        assertThat(addOn.getCreatedAt()).isEqualTo(now);
        assertThat(addOn.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testNoArgsConstructor() {
        RentalAddOn empty = new RentalAddOn();
        assertThat(empty).isNotNull();
        empty.setName("Test AddOn");
        empty.setPrice(12345.0);
        assertThat(empty.getName()).isEqualTo("Test AddOn");
        assertThat(empty.getPrice()).isEqualTo(12345.0);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        RentalAddOn full = new RentalAddOn(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "WiFi",
                75000.0,
                List.of(booking),
                now,
                now
        );

        assertThat(full.getId()).isEqualTo(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        assertThat(full.getName()).isEqualTo("WiFi");
        assertThat(full.getPrice()).isEqualTo(75000.0);
        assertThat(full.getListOfBookings()).contains(booking);
        assertThat(full.getCreatedAt()).isEqualTo(now);
        assertThat(full.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testPrePersistSetsCreatedAndUpdatedAt() {
        RentalAddOn newAddOn = new RentalAddOn();
        newAddOn.onCreate();

        assertThat(newAddOn.getCreatedAt()).isNotNull();
        assertThat(newAddOn.getUpdatedAt()).isNotNull();
        assertThat(newAddOn.getCreatedAt()).isEqualTo(newAddOn.getUpdatedAt());
    }

    @Test
    void testPreUpdateSetsUpdatedAtLater() throws InterruptedException {
        addOn.onCreate();
        LocalDateTime before = addOn.getUpdatedAt();
        Thread.sleep(5);
        addOn.onUpdate();

        assertThat(addOn.getUpdatedAt()).isAfter(before);
    }

    @Test
    void testEqualsAndHashCode() {
        RentalAddOn same = RentalAddOn.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Car Seat")
                .price(100000.0)
                .build();

        RentalAddOn different = RentalAddOn.builder()
                .id(UUID.fromString("99999999-9999-9999-9999-999999999999"))
                .name("Different")
                .price(50000.0)
                .build();

        assertThat(addOn).isEqualTo(addOn);
        assertThat(addOn).isNotEqualTo(null);
        assertThat(addOn).isNotEqualTo(different);
        assertThat(addOn.hashCode()).isNotZero();
        assertThat(same.hashCode()).isEqualTo(same.hashCode());
    }

    @Test
    void testToStringContainsKeyFields() {
        String result = addOn.toString();
        assertThat(result).contains("Car Seat");
        assertThat(result).contains("100000");
        assertThat(result).contains("11111111");
    }

    @Test
    void testListOfBookingsSetterGetter() {
        addOn.setListOfBookings(Collections.emptyList());
        assertThat(addOn.getListOfBookings()).isEmpty();

        addOn.setListOfBookings(List.of(booking));
        assertThat(addOn.getListOfBookings()).contains(booking);
    }
}
