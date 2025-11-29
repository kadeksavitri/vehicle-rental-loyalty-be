package apap.ti._5.vehicle_rental_2306203236_be;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;



// import apap.ti._5.vehicle_rental_2306203236_be.restdto.request.*;
// import apap.ti._5.vehicle_rental_2306203236_be.restdto.response.*;
// import apap.ti._5.vehicle_rental_2306203236_be.restservice.RentalBookingRestService;
// import apap.ti._5.vehicle_rental_2306203236_be.restservice.VehicleRestService;


@SpringBootApplication
public class VehicleRental2306203236BeApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicleRental2306203236BeApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner createDummyVehicleAndRentalBooking(VehicleRestService vehicleRestService, RentalBookingRestService rentalBookingRestService){
	// 	return args -> {
	// 		System.out.println("Generating dummy airlines from IATA dataset...");
	// };

	// }
}
