// package apap.ti._5.vehicle_rental_2306203236_be.service;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.MockedStatic;
// import org.springframework.web.client.RestTemplate;

// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// class LocationServiceTest {

//     private LocationService locationService;

//     @BeforeEach
//     void setUp() {
//         locationService = new LocationService();
//     }

//     @Test
//     void testGetAllProvinces_Success() {
//         // Mock RestTemplate
//         RestTemplate mockRestTemplate = mock(RestTemplate.class);
//         Map<String, Object> mockResponse = new HashMap<>();

//         List<Map<String, Object>> mockData = new ArrayList<>();
//         mockData.add(Map.of("name", "Jawa Barat"));
//         mockData.add(Map.of("name", "Bali"));

//         mockResponse.put("data", mockData);

//         // Mock static creation of RestTemplate
//         try (MockedStatic<RestTemplate> mocked = mockStatic(RestTemplate.class)) {
//             mocked.when(RestTemplate::new).thenReturn(mockRestTemplate);
//             when(mockRestTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

//             List<String> result = locationService.getAllProvinces();

//             assertEquals(2, result.size());
//             assertTrue(result.contains("Jawa Barat"));
//             assertTrue(result.contains("Bali"));
//         }
//     }

//     @Test
//     void testGetAllProvinces_ResponseNull_ThrowsException() {
//         RestTemplate mockRestTemplate = mock(RestTemplate.class);

//         try (MockedStatic<RestTemplate> mocked = mockStatic(RestTemplate.class)) {
//             mocked.when(RestTemplate::new).thenReturn(mockRestTemplate);
//             when(mockRestTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

//             RuntimeException ex = assertThrows(RuntimeException.class, () -> {
//                 locationService.getAllProvinces();
//             });

//             assertEquals("Failed to fetch province data from wilayah.id API", ex.getMessage());
//         }
//     }

//     @Test
//     void testGetAllProvinces_NoDataKey_ThrowsException() {
//         RestTemplate mockRestTemplate = mock(RestTemplate.class);
//         Map<String, Object> mockResponse = new HashMap<>();
//         mockResponse.put("wrongKey", List.of(Map.of("name", "Sumatera Utara")));

//         try (MockedStatic<RestTemplate> mocked = mockStatic(RestTemplate.class)) {
//             mocked.when(RestTemplate::new).thenReturn(mockRestTemplate);
//             when(mockRestTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

//             RuntimeException ex = assertThrows(RuntimeException.class, () -> {
//                 locationService.getAllProvinces();
//             });

//             assertEquals("Failed to fetch province data from wilayah.id API", ex.getMessage());
//         }
//     }
// }
