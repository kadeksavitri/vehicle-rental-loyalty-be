package apap.ti._5.vehicle_rental_2306203236_be.restdto.request.bill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillRequestDTO {
    private String customerId;
    private String serviceName;
    private String serviceReferenceId;
    private String description;
    private BigDecimal amount;
}
