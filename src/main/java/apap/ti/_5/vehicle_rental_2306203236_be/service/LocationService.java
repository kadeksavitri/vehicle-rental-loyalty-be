package apap.ti._5.vehicle_rental_2306203236_be.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private static final String PROVINCES_API_URL = "https://wilayah.id/api/provinces.json";

    public List<String> getAllProvinces() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(PROVINCES_API_URL, Map.class);

        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("Failed to fetch province data from wilayah.id API");
        }

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        return data.stream()
                .map(item -> (String) item.get("name"))
                .collect(Collectors.toList());
    }
}
