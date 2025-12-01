package apap.ti._5.vehicle_rental_2306203236_be.service;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.bill.BillApiResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.bill.BillResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class BillServiceImpl implements BillService {

    private final RestTemplate restTemplate;
    
    @Value("${bill.service.base-url}")
    private String billServiceBaseUrl;

    public BillServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public BillResponseDTO createBill(CreateBillRequestDTO request) {
        String url = billServiceBaseUrl + "/api/bill/create";
        
        try {
            log.info("Creating bill for customer: {} with reference: {}", 
                request.getCustomerId(), request.getServiceReferenceId());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<CreateBillRequestDTO> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<BillApiResponseDTO<BillResponseDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<BillApiResponseDTO<BillResponseDTO>>() {}
            );
            
            if (response.getBody() != null && response.getBody().getData() != null) {
                log.info("Bill created successfully: {}", response.getBody().getData().getId());
                return response.getBody().getData();
            }
            
            throw new RuntimeException("Failed to create bill: Empty response");
            
        } catch (Exception e) {
            log.error("Error creating bill: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create bill: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BillResponseDTO> getCustomerBills(String customerId) {
        String url = billServiceBaseUrl + "/api/bill/customer?customerId=" + customerId;
        
        try {
            log.info("Fetching bills for customer: {}", customerId);
            
            ResponseEntity<BillApiResponseDTO<List<BillResponseDTO>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BillApiResponseDTO<List<BillResponseDTO>>>() {}
            );
            
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }
            
            throw new RuntimeException("Failed to fetch customer bills: Empty response");
            
        } catch (Exception e) {
            log.error("Error fetching customer bills: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch customer bills: " + e.getMessage(), e);
        }
    }

    @Override
    public BillResponseDTO getBillByServiceReference(String serviceReferenceId) {
        try {
            // Get all customer bills and filter by service reference
            // This assumes we have the customerId from the booking
            log.info("Fetching bill for service reference: {}", serviceReferenceId);
            
            // Note: This is a workaround. Ideally, the bill service should have an endpoint
            // to get bill by service reference ID directly
            
            return null; // Will be populated from getCustomerBills
            
        } catch (Exception e) {
            log.error("Error fetching bill by service reference: {}", e.getMessage(), e);
            return null;
        }
    }
}
