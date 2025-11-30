package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.AddVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.SearchAvailableVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.vehicle.UpdateVehicleRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle.AvailableVehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.vehicle.VehicleResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restservice.VehicleRestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    @Autowired VehicleRestService vehicleRestService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN','RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<List<VehicleResponseDTO>>> getAllVehicles(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false) String type) {

        var baseResponse = new BaseResponseDTO<List<VehicleResponseDTO>>();
        List<VehicleResponseDTO> vehicles;

        try {
            if (keyword != null && type != null) {
                vehicles = vehicleRestService.getAllVehicleByKeywordAndType(keyword, type);
            } else if (keyword != null) {
                vehicles = vehicleRestService.getAllVehicleByKeywordAndType(keyword, "");
            } else {
                vehicles = vehicleRestService.getAllVehicle();
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(vehicles);
            baseResponse.setMessage("Data kendaraan berhasil diambil");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Gagal mengambil data kendaraan: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','RENTAL_VENDOR', 'CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> getVehicle(@PathVariable("id") String id) {
        var baseResponse = new BaseResponseDTO<VehicleResponseDTO>();

        try {
            VehicleResponseDTO vehicle = vehicleRestService.getVehicle(id);
            if (vehicle == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Kendaraan dengan ID " + id + " tidak ditemukan");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(vehicle);
            baseResponse.setMessage("Data kendaraan berhasil ditemukan");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN','RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> createVehicle(
            @Valid @RequestBody AddVehicleRequestDTO addVehicleRequestDTO,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<VehicleResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(errorMessages.toString());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            VehicleResponseDTO createdVehicle = vehicleRestService.createVehicle(addVehicleRequestDTO);
            if (createdVehicle == null) {
                baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                baseResponse.setMessage("Gagal membuat kendaraan baru");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            baseResponse.setStatus(HttpStatus.CREATED.value());
            baseResponse.setData(createdVehicle);
            baseResponse.setMessage("Kendaraan berhasil dibuat");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.CREATED);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> updateVehicle(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateVehicleRequestDTO updateVehicleRequestDTO,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<VehicleResponseDTO>();

        if (!id.equals(updateVehicleRequestDTO.getId())) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("ID kendaraan tidak sesuai dengan data body");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            }

            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(errorMessages.toString());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            VehicleResponseDTO updatedVehicle = vehicleRestService.updateVehicle(updateVehicleRequestDTO);
            if (updatedVehicle == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Kendaraan dengan ID " + id + " tidak ditemukan");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(updatedVehicle);
            baseResponse.setMessage("Data kendaraan berhasil diperbarui");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("Gagal memperbarui: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<VehicleResponseDTO>> deleteVehicle(@PathVariable("id") String id) {
        var baseResponse = new BaseResponseDTO<VehicleResponseDTO>();

        try {
            VehicleResponseDTO deletedVehicle = vehicleRestService.deleteVehicle(id);
            if (deletedVehicle == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Kendaraan dengan ID " + id + " tidak ditemukan");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(deletedVehicle);
            baseResponse.setMessage("Kendaraan berhasil dihapus");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("Tidak dapat menghapus kendaraan: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('SUPERADMIN','CUSTOMER')")
    public ResponseEntity<BaseResponseDTO<List<AvailableVehicleResponseDTO>>> searchAvailableVehicles(
            @Valid @RequestBody SearchAvailableVehicleRequestDTO searchRequest,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<List<AvailableVehicleResponseDTO>>();

        if (bindingResult.hasFieldErrors()) {
            StringBuilder errors = new StringBuilder();
            for (FieldError err : bindingResult.getFieldErrors()) {
                errors.append(err.getDefaultMessage()).append("; ");
            }
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(errors.toString());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            List<AvailableVehicleResponseDTO> availableVehicles = vehicleRestService.searchAvailableVehicles(searchRequest);

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(availableVehicles);
            baseResponse.setMessage("Pencarian kendaraan tersedia berhasil");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
