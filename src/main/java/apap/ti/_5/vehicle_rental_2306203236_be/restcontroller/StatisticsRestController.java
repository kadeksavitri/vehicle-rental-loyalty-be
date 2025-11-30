package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalBookingRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.StatisticsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/public/statistics")
@RequiredArgsConstructor
public class StatisticsRestController {

    private final VehicleRepository vehicleRepository;
    private final RentalVendorRepository rentalVendorRepository;
    private final RentalBookingRepository rentalBookingRepository;

    @GetMapping
    public ResponseEntity<BaseResponseDTO<StatisticsResponseDTO>> getStatistics() {
        var baseResponse = new BaseResponseDTO<StatisticsResponseDTO>();

        try {
            StatisticsResponseDTO statistics = StatisticsResponseDTO.builder()
                    .registeredVehicles(vehicleRepository.count())
                    .registeredVendors(rentalVendorRepository.count())
                    .bookingsMade(rentalBookingRepository.count())
                    .build();

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setMessage("Data statistik berhasil diambil");
            baseResponse.setData(statistics);
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.ok(baseResponse);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Gagal mengambil data statistik: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.internalServerError().body(baseResponse);
        }
    }
}
