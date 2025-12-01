package apap.ti._5.vehicle_rental_2306203236_be.service;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.bill.CreateBillRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.bill.BillResponseDTO;

import java.util.List;

public interface BillService {
    BillResponseDTO createBill(CreateBillRequestDTO request);
    
    List<BillResponseDTO> getCustomerBills(String customerId);
    
    BillResponseDTO getBillByServiceReference(String serviceReferenceId);
}
