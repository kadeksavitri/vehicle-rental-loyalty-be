package apap.ti._5.vehicle_rental_2306203236_be.restservice;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.CreateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.UpdateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.DeleteRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.UpdateRentalBookingAddOnRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.ChartRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.rentalbooking.RentalBookingResponseDTO;

import java.util.List;

public interface RentalBookingRestService {

    RentalBookingResponseDTO createRentalBooking(CreateRentalBookingRequestDTO createRentalBookingDto);

    List<RentalBookingResponseDTO> getAllRentalBookingsByKeyword(String keyword);

    List<RentalBookingResponseDTO> getAllRentalBookings();

    RentalBookingResponseDTO getRentalBooking(String id);

    RentalBookingResponseDTO updateRentalBookingDetails(UpdateRentalBookingRequestDTO dto);

    RentalBookingResponseDTO updateRentalBookingStatus(String id, String newStatus);


    RentalBookingResponseDTO updateRentalBookingAddOn(UpdateRentalBookingAddOnRequestDTO dto);

    RentalBookingResponseDTO deleteRentalBooking(DeleteRentalBookingRequestDTO dto);

    List<Object[]> getRentalBookingStatistics(ChartRentalBookingRequestDTO chartRequest);
}
