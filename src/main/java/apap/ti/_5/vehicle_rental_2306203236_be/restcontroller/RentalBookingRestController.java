package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.CreateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.UpdateRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.UpdateRentalBookingStatusRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.DeleteRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.UpdateRentalBookingAddOnRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.rentalbooking.ChartRentalBookingRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.rentalbooking.RentalBookingResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restservice.RentalBookingRestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class RentalBookingRestController {

    @Autowired
    private RentalBookingRestService rentalBookingRestService;

    @GetMapping
    public ResponseEntity<BaseResponseDTO<List<RentalBookingResponseDTO>>> getAllRentalBookings(
            @RequestParam(value = "keyword", required = false) String keyword) {

        var baseResponse = new BaseResponseDTO<List<RentalBookingResponseDTO>>();

        try {
            List<RentalBookingResponseDTO> bookings;
            if (keyword != null && !keyword.isBlank()) {
                bookings = rentalBookingRestService.getAllRentalBookingsByKeyword(keyword);
            } else {
                bookings = rentalBookingRestService.getAllRentalBookings();
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setMessage("Data rental bookings berhasil diambil");
            baseResponse.setData(bookings);
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.ok(baseResponse);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Gagal mengambil data: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.internalServerError().body(baseResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> getRentalBooking(@PathVariable("id") String id) {
        var baseResponse = new BaseResponseDTO<RentalBookingResponseDTO>();

        try {
            RentalBookingResponseDTO booking = rentalBookingRestService.getRentalBooking(id);
            if (booking == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Booking dengan ID " + id + " tidak ditemukan");
                baseResponse.setTimestamp(new Date());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(baseResponse);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(booking);
            baseResponse.setMessage("Data booking berhasil ditemukan");
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.ok(baseResponse);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.internalServerError().body(baseResponse);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> createRentalBooking(
            @Valid @RequestBody CreateRentalBookingRequestDTO requestDTO,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<RentalBookingResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError err : bindingResult.getFieldErrors()) {
                errors.append(err.getDefaultMessage()).append("; ");
            }

            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(errors.toString());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(baseResponse);
        }

        try {
            RentalBookingResponseDTO created = rentalBookingRestService.createRentalBooking(requestDTO);
            if (created == null) {
                baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                baseResponse.setMessage("Gagal membuat booking baru");
                baseResponse.setTimestamp(new Date());
                return ResponseEntity.internalServerError().body(baseResponse);
            }

            baseResponse.setStatus(HttpStatus.CREATED.value());
            baseResponse.setMessage("Booking berhasil dibuat");
            baseResponse.setData(created);
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);

        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("Gagal membuat booking: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(baseResponse);
        }
    }

    @PutMapping("/update-details")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateBookingDetails(
            @Valid @RequestBody UpdateRentalBookingRequestDTO dto,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<RentalBookingResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError err : bindingResult.getFieldErrors()) {
                errors.append(err.getDefaultMessage()).append("; ");
            }

            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(errors.toString());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(baseResponse);
        }

        try {
            RentalBookingResponseDTO updated = rentalBookingRestService.updateRentalBookingDetails(dto);
            if (updated == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Booking tidak ditemukan atau status bukan Upcoming");
                baseResponse.setTimestamp(new Date());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(baseResponse);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setMessage("Booking berhasil diperbarui");
            baseResponse.setData(updated);
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.ok(baseResponse);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.internalServerError().body(baseResponse);
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateBookingStatus(
            @Valid @RequestBody UpdateRentalBookingStatusRequestDTO dto,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<RentalBookingResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError err : bindingResult.getFieldErrors()) {
                errors.append(err.getDefaultMessage()).append("; ");
            }
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(errors.toString());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(baseResponse);
        }

        try {
            RentalBookingResponseDTO updated = rentalBookingRestService.updateRentalBookingStatus(dto.getId(), dto.getNewStatus());
            if (updated == null) {
                baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                baseResponse.setMessage("Gagal memperbarui status booking. Pastikan status valid dan booking masih aktif.");
                baseResponse.setTimestamp(new Date());
                return ResponseEntity.badRequest().body(baseResponse);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setMessage("Status booking berhasil diubah menjadi " + dto.getNewStatus());
            baseResponse.setData(updated);
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.ok(baseResponse);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.internalServerError().body(baseResponse);
        }
    }

@PutMapping("/update-addons")
public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> updateAddOns(
        @Valid @RequestBody UpdateRentalBookingAddOnRequestDTO dto) {

    var baseResponse = new BaseResponseDTO<RentalBookingResponseDTO>();

    try {
        RentalBookingResponseDTO updated = rentalBookingRestService.updateRentalBookingAddOn(dto);
        if (updated == null) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("Gagal memperbarui add-ons (pastikan booking Upcoming)");
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.badRequest().body(baseResponse);
        }

        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Add-ons berhasil diperbarui");
        baseResponse.setData(updated);
        baseResponse.setTimestamp(new Date());
        return ResponseEntity.ok(baseResponse);

    } catch (Exception e) {
        baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
        baseResponse.setTimestamp(new Date());
        return ResponseEntity.internalServerError().body(baseResponse);
    }
}

@DeleteMapping("{id}/delete")
public ResponseEntity<BaseResponseDTO<RentalBookingResponseDTO>> deleteBooking(
        @Valid @RequestBody DeleteRentalBookingRequestDTO dto) {

    var baseResponse = new BaseResponseDTO<RentalBookingResponseDTO>();

    try {
        RentalBookingResponseDTO deleted = rentalBookingRestService.deleteRentalBooking(dto);
        if (deleted == null) {
            baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponse.setMessage("Booking tidak ditemukan atau bukan Upcoming");
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(baseResponse);
        }

        baseResponse.setStatus(HttpStatus.OK.value());
        baseResponse.setMessage("Booking berhasil dihapus");
        baseResponse.setData(deleted);
        baseResponse.setTimestamp(new Date());
        return ResponseEntity.ok(baseResponse);

    } catch (Exception e) {
        baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
        baseResponse.setTimestamp(new Date());
        return ResponseEntity.internalServerError().body(baseResponse);
    }
}


    @PostMapping("/chart")
    public ResponseEntity<BaseResponseDTO<List<Object[]>>> getBookingChartData(
            @RequestBody ChartRentalBookingRequestDTO chartRequest) {

        var baseResponse = new BaseResponseDTO<List<Object[]>>();

        try {
            List<Object[]> chartData = rentalBookingRestService.getRentalBookingStatistics(chartRequest);
            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setMessage("Data statistik booking berhasil diambil");
            baseResponse.setData(chartData);
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.ok(baseResponse);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Gagal mengambil data chart: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return ResponseEntity.internalServerError().body(baseResponse);
        }
    }
}
