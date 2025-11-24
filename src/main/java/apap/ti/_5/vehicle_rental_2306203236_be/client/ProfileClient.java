package apap.ti._5.vehicle_rental_2306203236_be.client;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.BaseResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.UUID;

@Service
public class ProfileClient {

    @Value("${profile.service.url}")
    private String profileBaseUrl; // contoh: http://localhost:8085/api/users/detail

    @Autowired
    private RestTemplate restTemplate;

    public String getCustomerName(UUID customerId) {
        String url = profileBaseUrl + "?search=" + customerId.toString();

        BaseResponseDTO response =
                restTemplate.getForObject(url, BaseResponseDTO.class);

        LinkedHashMap data = (LinkedHashMap) response.getData();

        return (String) data.get("name");
    }
}
