// package apap.ti._5.vehicle_rental_2306203236_be.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
// import apap.ti._5.vehicle_rental_2306203236_be.repository.VehicleRepository;
// import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalBookingRepository; 

// @Controller
// public class HomeController {

//     @Autowired
//     private VehicleRepository vehicleRepository;
    
//     @Autowired
//     private RentalVendorRepository rentalVendorRepository;
    
//     @Autowired
//     private RentalBookingRepository rentalBookingRepository; 

//     @GetMapping("/")
//     public String home(Model model) {
//         model.addAttribute("vehicleCount", vehicleRepository.count());
//         model.addAttribute("vendorCount", rentalVendorRepository.count());
//         model.addAttribute("bookingCount", rentalBookingRepository.count());
//         return "home";
//     }
// }
