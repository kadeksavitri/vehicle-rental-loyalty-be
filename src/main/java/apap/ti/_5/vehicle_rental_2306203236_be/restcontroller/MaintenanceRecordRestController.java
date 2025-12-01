package apap.ti._5.vehicle_rental_2306203236_be.restcontroller;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance.CreateMaintenanceRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance.UpdateMaintenanceRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.maintenance.UpdateMaintenanceStatusRequestDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.BaseResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.maintenance.MaintenanceRecordResponseDTO;
import apap.ti._5.vehicle_rental_2306203236_be.restservice.MaintenanceRestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceRecordRestController {

    @Autowired
    private MaintenanceRestService maintenanceRestService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<List<MaintenanceRecordResponseDTO>>> getAllMaintenanceRecords() {
        var baseResponse = new BaseResponseDTO<List<MaintenanceRecordResponseDTO>>();

        try {
            List<MaintenanceRecordResponseDTO> records = maintenanceRestService.getAllMaintenanceRecords();

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(records);
            baseResponse.setMessage("Data maintenance record berhasil diambil");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Gagal mengambil data maintenance record: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> getMaintenanceRecordById(
            @PathVariable("id") String id) {
        var baseResponse = new BaseResponseDTO<MaintenanceRecordResponseDTO>();

        try {
            MaintenanceRecordResponseDTO record = maintenanceRestService.getMaintenanceRecordById(id);
            if (record == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Maintenance record dengan ID " + id + " tidak ditemukan");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(record);
            baseResponse.setMessage("Data maintenance record berhasil ditemukan");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.FORBIDDEN.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.FORBIDDEN);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> createMaintenanceRecord(
            @Valid @RequestBody CreateMaintenanceRequestDTO createDTO,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<MaintenanceRecordResponseDTO>();

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
            MaintenanceRecordResponseDTO createdRecord = maintenanceRestService.createMaintenanceRecord(createDTO);
            if (createdRecord == null) {
                baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                baseResponse.setMessage("Gagal membuat maintenance record");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            baseResponse.setStatus(HttpStatus.CREATED.value());
            baseResponse.setData(createdRecord);
            baseResponse.setMessage("Maintenance record berhasil dibuat");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage(e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> updateMaintenanceRecord(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateMaintenanceRequestDTO updateDTO,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<MaintenanceRecordResponseDTO>();

        if (!id.equals(updateDTO.getId())) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("ID maintenance record tidak sesuai dengan data body");
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
            MaintenanceRecordResponseDTO updatedRecord = maintenanceRestService.updateMaintenanceRecord(updateDTO);
            if (updatedRecord == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Maintenance record dengan ID " + id + " tidak ditemukan");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(updatedRecord);
            baseResponse.setMessage("Maintenance record berhasil diperbarui");
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

    @PutMapping("/update-status/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> updateMaintenanceRecordStatus(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateMaintenanceStatusRequestDTO statusDTO,
            BindingResult bindingResult) {

        var baseResponse = new BaseResponseDTO<MaintenanceRecordResponseDTO>();

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
            MaintenanceRecordResponseDTO updatedRecord = maintenanceRestService.updateMaintenanceRecordStatus(id, statusDTO.getStatus());
            if (updatedRecord == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Maintenance record dengan ID " + id + " tidak ditemukan");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(updatedRecord);
            baseResponse.setMessage("Status maintenance record berhasil diperbarui");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("Gagal memperbarui status: " + e.getMessage());
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
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'RENTAL_VENDOR')")
    public ResponseEntity<BaseResponseDTO<MaintenanceRecordResponseDTO>> deleteMaintenanceRecord(
            @PathVariable("id") String id) {

        var baseResponse = new BaseResponseDTO<MaintenanceRecordResponseDTO>();

        try {
            MaintenanceRecordResponseDTO deletedRecord = maintenanceRestService.deleteMaintenanceRecord(id);
            if (deletedRecord == null) {
                baseResponse.setStatus(HttpStatus.NOT_FOUND.value());
                baseResponse.setMessage("Maintenance record dengan ID " + id + " tidak ditemukan");
                baseResponse.setTimestamp(new Date());
                return new ResponseEntity<>(baseResponse, HttpStatus.NOT_FOUND);
            }

            baseResponse.setStatus(HttpStatus.OK.value());
            baseResponse.setData(deletedRecord);
            baseResponse.setMessage("Maintenance record berhasil dihapus");
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            baseResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponse.setMessage("Tidak dapat menghapus maintenance record: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            baseResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponse.setMessage("Terjadi kesalahan server: " + e.getMessage());
            baseResponse.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}