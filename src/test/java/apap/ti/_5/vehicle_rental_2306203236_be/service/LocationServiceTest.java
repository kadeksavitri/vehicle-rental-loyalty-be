package apap.ti._5.vehicle_rental_2306203236_be.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocationServiceTest {

    private LocationService locationService;

    @BeforeEach
    void setup() {
        locationService = new LocationService();
    }

    @Test
    void testGetAllProvinces_Success() {
        // Dummy data seperti respons dari API
        Map<String, Object> dummyItem1 = new HashMap<>();
        dummyItem1.put("name", "Jawa Barat");
        Map<String, Object> dummyItem2 = new HashMap<>();
        dummyItem2.put("name", "Bali");

        List<Map<String, Object>> dataList = List.of(dummyItem1, dummyItem2);
        Map<String, Object> dummyResponse = new HashMap<>();
        dummyResponse.put("data", dataList);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> when(mock.getForObject(anyString(), eq(Map.class))).thenReturn(dummyResponse))) {

            List<String> provinces = locationService.getAllProvinces();

            assertThat(provinces).hasSize(2);
            assertThat(provinces).contains("Jawa Barat", "Bali");
            verify(mocked.constructed().get(0)).getForObject(anyString(), eq(Map.class));
        }
    }

    @Test
    void testGetAllProvinces_NullResponse_ThrowsException() {
        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> when(mock.getForObject(anyString(), eq(Map.class))).thenReturn(null))) {

            assertThatThrownBy(() -> locationService.getAllProvinces())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to fetch province data");
        }
    }

    @Test
    void testGetAllProvinces_NoDataKey_ThrowsException() {
        Map<String, Object> dummyResponse = Map.of("invalidKey", "oops");

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> when(mock.getForObject(anyString(), eq(Map.class))).thenReturn(dummyResponse))) {

            assertThatThrownBy(() -> locationService.getAllProvinces())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to fetch province data");
        }
    }

    @Test
    void testGetAllProvinces_EmptyDataList_ReturnsEmptyList() {
        Map<String, Object> dummyResponse = Map.of("data", new ArrayList<>());

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> when(mock.getForObject(anyString(), eq(Map.class))).thenReturn(dummyResponse))) {

            List<String> provinces = locationService.getAllProvinces();
            assertThat(provinces).isEmpty();
        }
    }
}
