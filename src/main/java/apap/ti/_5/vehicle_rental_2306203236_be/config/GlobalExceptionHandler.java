package apap.ti._5.vehicle_rental_2306203236_be.config;

import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.BaseResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponseDTO<String>> handleAccessDeniedException(AccessDeniedException ex) {
        var response = new BaseResponseDTO<String>();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setMessage("Akses ditolak: Anda tidak memiliki permission untuk mengakses resource ini");
        response.setData("Role Anda tidak sesuai dengan requirement endpoint ini");
        response.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponseDTO<String>> handleAuthenticationException(AuthenticationException ex) {
        var response = new BaseResponseDTO<String>();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Authentication gagal: " + ex.getMessage());
        response.setData("Silakan login terlebih dahulu atau periksa token Anda");
        response.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponseDTO<String>> handleBadCredentialsException(BadCredentialsException ex) {
        var response = new BaseResponseDTO<String>();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Username atau password salah");
        response.setData(null);
        response.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponseDTO<String>> handleNotFoundException(NoHandlerFoundException ex) {
        var response = new BaseResponseDTO<String>();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage("Endpoint tidak ditemukan: " + ex.getRequestURL());
        response.setData(null);
        response.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponseDTO<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        var response = new BaseResponseDTO<String>();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Invalid request: " + ex.getMessage());
        response.setData(null);
        response.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO<String>> handleGeneralException(Exception ex) {
        var response = new BaseResponseDTO<String>();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage("Terjadi kesalahan server: " + ex.getMessage());
        response.setData(ex.getClass().getSimpleName());
        response.setTimestamp(new Date());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
