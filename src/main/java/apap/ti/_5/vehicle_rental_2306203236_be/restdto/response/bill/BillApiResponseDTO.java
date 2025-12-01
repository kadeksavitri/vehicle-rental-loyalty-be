package apap.ti._5.vehicle_rental_2306203236_be.restdto.response.bill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillApiResponseDTO<T> {
    private int status;
    private String message;
    private T data;
}
