// package apap.ti._5.vehicle_rental_2306203236_be.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;
// import jakarta.validation.Valid;
// import org.springframework.web.bind.annotation.ModelAttribute;

// @Controller
// @RequiredArgsConstructor
// @RequestMapping("/booking")
// public class BookingController {
    
//     @GetMapping("/chart")
//     public String showChart(Model model) {
//         model.addAttribute("bookings", bookingService.getAllBookings());
//         return "booking/chart";
//     }
// }