package apap.ti._5.vehicle_rental_2306203236_be.controller;

import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.CreateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.UpdateRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.dto.booking.ReadRentalBookingDto;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalAddOn;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalBooking;
import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalAddOnRepository;
import apap.ti._5.vehicle_rental_2306203236_be.service.LocationService;
import apap.ti._5.vehicle_rental_2306203236_be.service.RentalBookingService;
import apap.ti._5.vehicle_rental_2306203236_be.service.VehicleService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bookings")
public class RentalBookingController {

    @Autowired
    private RentalBookingService rentalBookingService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private RentalAddOnRepository rentalAddOnRepository;

    @Autowired
    private LocationService locationService;

    @GetMapping
    public String viewAllRentalBookings(@RequestParam(required = false) String keyword, Model model) {
        List<ReadRentalBookingDto> filteredRentalBookings = rentalBookingService.getAllRentalBookingDto(keyword);

        model.addAttribute("rentalBookings", filteredRentalBookings);
        model.addAttribute("selectedKeyword", keyword);
        return "booking/view-all";
    }

    @GetMapping("/{id}")
    public String detailBooking(@PathVariable("id") String id, Model model) {
        RentalBooking rentalBooking = rentalBookingService.getRentalBooking(id);

        if (rentalBooking == null) {
            model.addAttribute("title", "Rental Booking not found");
            model.addAttribute("message", "Rental Booking with id " + id + " not found");
            return "error/404";
        }

        model.addAttribute("rentalBooking", rentalBooking);
        model.addAttribute("addons", rentalBooking.getListOfAddOns());
        model.addAttribute("canUpdateDetails", "Upcoming".equals(rentalBooking.getStatus()));
        model.addAttribute("canUpdateAddOns", "Upcoming".equals(rentalBooking.getStatus()));
        model.addAttribute("canUpdateStatus", "Upcoming".equals(rentalBooking.getStatus()) || "Ongoing".equals(rentalBooking.getStatus()));
        model.addAttribute("canCancel", "Upcoming".equals(rentalBooking.getStatus()));
        return "booking/detail";
    }

    @GetMapping("/create")
    public String createRentalBookingForm(Model model) {
        CreateRentalBookingDto rentalBooking = new CreateRentalBookingDto();
        rentalBooking.setPickUpTime(LocalDateTime.now().plusHours(1)); // default minimal

        List<String> locations = locationService.getAllProvinces();

        model.addAttribute("locations", locations);
        model.addAttribute("rentalBooking", rentalBooking);
        model.addAttribute("isEdit", false);
        return "booking/form-booking";
    }

    @PostMapping("/create")
        public String createBookingSubmit( @Valid @ModelAttribute("rentalBooking") CreateRentalBookingDto dto, BindingResult bindingResult, Model model) {

            // if (bindingResult.hasErrors()) {
            //     model.addAttribute("errorMessage", "Validation failed. Please check your input.");
            //     model.addAttribute("bindingResult", bindingResult);
            //     return "booking/form-booking";
            // }

            // Validasi waktu
            if (dto.getPickUpTime().isBefore(LocalDateTime.now())) {
                model.addAttribute("errorMessage", "Waktu pengambilan harus setelah waktu saat ini.");
                model.addAttribute("rentalBooking", dto);
                model.addAttribute("locations", locationService.getAllProvinces());
                model.addAttribute("isEdit", false);
                return "booking/form-booking";
            }
            if (!dto.getDropOffTime().isAfter(dto.getPickUpTime())) {
                model.addAttribute("errorMessage", "Waktu pengembalian harus setelah waktu pengambilan.");
                model.addAttribute("rentalBooking", dto);
                model.addAttribute("locations", locationService.getAllProvinces());
                model.addAttribute("isEdit", false);
                return "booking/form-booking";
            }

            // Filter kendaraan yang memenihi kriteria
            List<Vehicle> availableVehicles = vehicleService.getAllVehicle()
                    .stream()
                    .filter(v -> {
                        RentalVendor vendor = v.getRentalVendor();
                        boolean basicMatch = v.getCapacity() >= dto.getCapacityNeeded()
                            && v.getTransmission().equalsIgnoreCase(dto.getTransmissionNeeded())
                            && v.getStatus().equalsIgnoreCase("Available")
                            && v.getLocation().equalsIgnoreCase(dto.getPickUpLocation())
                            && vendor != null
                            && vendor.getListOfLocations() != null
                            && vendor.getListOfLocations().contains(dto.getPickUpLocation())
                            && vendor.getListOfLocations().contains(dto.getDropOffLocation());

                        boolean timeAvailable = rentalBookingService.isVehicleAvailableDuringPeriod(v, dto.getPickUpTime(), dto.getDropOffTime());
                        return basicMatch && timeAvailable;
                    })
                    .toList();

            // Filter kendaraan berdasarkan lokasi
            List<Vehicle> matchingLocationVehicles = availableVehicles.stream()
                    .filter(v -> {
                        RentalVendor vendor = v.getRentalVendor();
                        boolean basicMatch2 = vendor != null
                            && vendor.getListOfLocations() != null
                            && vendor.getListOfLocations().contains(dto.getPickUpLocation())
                            && vendor.getListOfLocations().contains(dto.getDropOffLocation());
                        return basicMatch2;
                        }) 
                    .toList();

            // Filter kendaraan berdasarkan jadwal
            List<Vehicle> matchingScheduleVehicles = availableVehicles.stream()
                .filter(v -> rentalBookingService.isVehicleAvailableDuringPeriod(
                    v,
                    dto.getPickUpTime(),
                    dto.getDropOffTime()
                ))
                .toList();

            if (matchingLocationVehicles.isEmpty()) {
                model.addAttribute("errorMessage", "Tidak ada vehicle yang tersedia di lokasi tersebut.");
                model.addAttribute("rentalBooking", dto);
                model.addAttribute("locations", locationService.getAllProvinces());
                model.addAttribute("isEdit", false);
                return "booking/form-booking";
            }  
            
            if (matchingScheduleVehicles.isEmpty()) {
                model.addAttribute("errorMessage", "Tidak ada vehicle yang tersedia pada jadwal tersebut.");
                model.addAttribute("rentalBooking", dto);
                model.addAttribute("locations", locationService.getAllProvinces());
                model.addAttribute("isEdit", false);
                return "booking/form-booking";
            } 
            
            if (availableVehicles.isEmpty()) {
                model.addAttribute("errorMessage", "Tidak ada vehicle yang memenuhi kriteria yang dipilih.");
                model.addAttribute("rentalBooking", dto);
                model.addAttribute("locations", locationService.getAllProvinces());
                model.addAttribute("isEdit", false);
                return "booking/form-booking";
            }


            long days = Math.max(1, (long) Math.ceil((double) java.time.Duration.between(dto.getPickUpTime(), dto.getDropOffTime()).toHours() / 24));

            List<Vehicle> updatedVehicles = availableVehicles.stream()
                    .peek(v -> {
                        double base = v.getPrice() * days;
                        v.setPrice(base); 
                    })
                    .sorted((v1, v2) -> Double.compare(v1.getPrice(), v2.getPrice())) 
                    .toList();

            if (!availableVehicles.isEmpty()) {
                model.addAttribute("showPriceSection", true);
            }

            model.addAttribute("availableVehicles", updatedVehicles);
            model.addAttribute("rentalBooking", dto);
            model.addAttribute("locations", locationService.getAllProvinces());
            model.addAttribute("days", days);
            model.addAttribute("isEdit", false);

            return "booking/form-booking";
    }

    @PostMapping("/create/addons")
    public String createBookingAddOnsForm(@ModelAttribute("rentalBooking") CreateRentalBookingDto dto,Model model) {
        List<RentalAddOn> addOns = rentalAddOnRepository.findAll();

        model.addAttribute("rentalBooking", dto);
        model.addAttribute("addons", addOns);
        return "booking/form-addons";
    }


    @PostMapping("/create/save")
    public String createBookingSave(@ModelAttribute CreateRentalBookingDto dto, Model model, RedirectAttributes redirectAttributes) {
        RentalBooking newBooking = rentalBookingService.createRentalBooking(dto);

        if(newBooking == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create new booking. Null");
            return "booking/form-booking";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Booking berhasil dibuat!");
        return "redirect:/bookings";
    }

    @GetMapping("/{id}/update-details")
    public String updateDetailsForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        RentalBooking rentalBooking = rentalBookingService.getRentalBooking(id);

        if (rentalBooking == null ) {
            model.addAttribute("title", "Rental Booking not found");
            model.addAttribute("message", "Rental Booking with id " + id + " not found");
            return "error/404";
        }  
        
        if (!"Upcoming".equals(rentalBooking.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot update details, only upcoming booking.");
            return "redirect:/bookings/" + id;
        }
        
        if (rentalBooking.getVehicle() != null) {
            rentalBooking.setVehicleId(rentalBooking.getVehicle().getId());
        }

        List<String> locations = locationService.getAllProvinces();

        model.addAttribute("locations", locations);
        model.addAttribute("rentalBooking", rentalBooking);
        model.addAttribute("isEdit", true);
        model.addAttribute("bookingId", id);
        return "booking/form-booking";
    }

    @PutMapping("/update-details/search")
    public String searchAvailableVehiclesForUpdate(@ModelAttribute("rentalBooking") UpdateRentalBookingDto dto,Model model) {

        // validasi waktu
        if (dto.getPickUpTime().isBefore(LocalDateTime.now())) {
            model.addAttribute("errorMessage", "Waktu pengambilan harus setelah waktu saat ini.");
            model.addAttribute("rentalBooking", dto);
            model.addAttribute("locations", locationService.getAllProvinces());
            model.addAttribute("isEdit", true);
            return "booking/form-booking";
        }
        if (!dto.getDropOffTime().isAfter(dto.getPickUpTime())) {
            model.addAttribute("errorMessage", "Waktu pengembalian harus setelah waktu pengambilan.");
            model.addAttribute("rentalBooking", dto);
            model.addAttribute("locations", locationService.getAllProvinces());
            model.addAttribute("isEdit", true);
            return "booking/form-booking";
        }

        // Filter kendaraan yang memenihi kriteria
        List<Vehicle> availableVehicles = vehicleService.getAllVehicle()
                .stream()
                .filter(v -> {
                    RentalVendor vendor = v.getRentalVendor();
                    boolean basicMatch = v.getCapacity() >= dto.getCapacityNeeded()
                        && v.getTransmission().equalsIgnoreCase(dto.getTransmissionNeeded())
                        && v.getStatus().equalsIgnoreCase("Available")
                        && v.getLocation().equalsIgnoreCase(dto.getPickUpLocation())
                        && vendor != null
                        && vendor.getListOfLocations() != null
                        && vendor.getListOfLocations().contains(dto.getPickUpLocation())
                        && vendor.getListOfLocations().contains(dto.getDropOffLocation());

                    boolean timeAvailable = rentalBookingService.isVehicleAvailableDuringPeriod(v, dto.getPickUpTime(), dto.getDropOffTime());
                    return basicMatch && timeAvailable;
                })
                .toList();

        // Filter kendaraan berdasarkan lokasi
        List<Vehicle> matchingLocationVehicles = availableVehicles.stream()
                .filter(v -> {
                    RentalVendor vendor = v.getRentalVendor();
                    boolean basicMatch2 = vendor != null
                        && vendor.getListOfLocations() != null
                        && vendor.getListOfLocations().contains(dto.getPickUpLocation())
                        && vendor.getListOfLocations().contains(dto.getDropOffLocation());
                    return basicMatch2;
                    }) 
                .toList();

        // Filter kendaraan berdasarkan jadwal
        List<Vehicle> matchingScheduleVehicles = availableVehicles.stream()
            .filter(v -> rentalBookingService.isVehicleAvailableDuringPeriod(
                v,
                dto.getPickUpTime(),
                dto.getDropOffTime()
            ))
            .toList();

        if (matchingLocationVehicles.isEmpty()) {
            model.addAttribute("errorMessage", "Tidak ada vehicle yang tersedia di lokasi tersebut.");
            model.addAttribute("rentalBooking", dto);
            model.addAttribute("locations", locationService.getAllProvinces());
            model.addAttribute("isEdit", true);
            return "booking/form-booking";
        }  
        
        if (matchingScheduleVehicles.isEmpty()) {
            model.addAttribute("errorMessage", "Tidak ada vehicle yang tersedia pada jadwal tersebut.");
            model.addAttribute("rentalBooking", dto);
            model.addAttribute("locations", locationService.getAllProvinces());
            model.addAttribute("isEdit", true);
            return "booking/form-booking";
        } 
        
        if (availableVehicles.isEmpty()) {
            model.addAttribute("errorMessage", "Tidak ada vehicle yang memenuhi kriteria yang dipilih.");
            model.addAttribute("rentalBooking", dto);
            model.addAttribute("locations", locationService.getAllProvinces());
            model.addAttribute("isEdit", true);
            return "booking/form-booking";
        }

        long days = Math.max(1, (long) Math.ceil((double) java.time.Duration.between(dto.getPickUpTime(), dto.getDropOffTime()).toHours() / 24));

        List<Vehicle> updatedVehicles = availableVehicles.stream()
                .peek(v -> {
                    double base = v.getPrice() * days;
                    v.setPrice(base); 
                })
                .sorted((v1, v2) -> Double.compare(v1.getPrice(), v2.getPrice())) 
                .toList();

        if (!availableVehicles.isEmpty()) {
            model.addAttribute("showPriceSection", true);
        }
        
        model.addAttribute("availableVehicles", updatedVehicles);
        model.addAttribute("rentalBooking", dto);
        model.addAttribute("locations", locationService.getAllProvinces());
        model.addAttribute("days", days);
        model.addAttribute("isEdit", true); 

        return "booking/form-booking";       
    }

    @PutMapping("/update-details")
    public String updateDetailsSave(@ModelAttribute("rentalBooking") UpdateRentalBookingDto dto, RedirectAttributes redirectAttributes) {
        RentalBooking updated = rentalBookingService.updateRentalBookingDetails(dto);

        if (updated == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memperbarui booking.");
            return "redirect:/bookings/" + dto.getId();
        }

        redirectAttributes.addFlashAttribute("successMessage", "Booking berhasil diperbarui!");
        return "redirect:/bookings/" + dto.getId();
    }

    @GetMapping("/{id}/update-status")
    public String updateStatusForm(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        var booking = rentalBookingService.getRentalBooking(id);

        if (booking == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking tidak ditemukan");
            return "redirect:/bookings";
        }

        // Jika status Done, tidak bisa ubah
        if ("Done".equals(booking.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Status sudah selesai, tidak dapat diubah.");
            return "redirect:/bookings/" + id;
        }

        model.addAttribute("booking", booking);
        return "booking/form-status";
    }

    @PutMapping("/update-status")
    public String updateStatusSubmit(@RequestParam("id") String id,
                                    @RequestParam("newStatus") String newStatus,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        RentalBooking updated = rentalBookingService.updateRentalBookingStatus(id, newStatus);

        if (updated == null) {
            redirectAttributes.addFlashAttribute("errorMessage","Gagal mengubah status. Pastikan waktu dan kondisi kendaraan sesuai kriteria.");
            return "redirect:/bookings/" + id + "/update-status";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Status pesanan berhasil diperbarui menjadi " + newStatus + "!");
        return "redirect:/bookings/" + id;
    }

    @GetMapping("/{id}/update-addons")
    public String updateAddOnsForm(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        var booking = rentalBookingService.getRentalBooking(id);

        if (booking == null || !"Upcoming".equals(booking.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found");
            return "redirect:/bookings/" + id;
        }

        if (!"Upcoming".equals(booking.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot update add-ons, only upcoming booking.");
            return "redirect:/bookings/" + id;
        }

        var addons = rentalAddOnRepository.findAll();
        UpdateRentalBookingDto dto = new UpdateRentalBookingDto();
        dto.setId(booking.getId());
        dto.setListOfAddOns(
            booking.getListOfAddOns() != null ?
                booking.getListOfAddOns().stream().map(a -> a.getId().toString()).collect(Collectors.toList()) :
                new ArrayList<>() // mutable list
        );

        model.addAttribute("rentalBooking", dto);
        model.addAttribute("bookingId", id);
        model.addAttribute("addons", addons);
        model.addAttribute("isEdit", true);
        return "booking/form-addons";
    }

    @PutMapping("/update-addons")
    public String updateAddOnsSubmit(@ModelAttribute("rentalBooking") UpdateRentalBookingDto dto, Model model, RedirectAttributes redirectAttributes) {
        rentalBookingService.updateRentalBookingAddOn(dto);

        redirectAttributes.addFlashAttribute("successMessage", "Add-ons berhasil diperbarui!");
        return "redirect:/bookings/" + dto.getId();
    }

    @GetMapping("/{id}/delete")
    public String deleteBooking(@PathVariable String id, Model model) {
        RentalBooking booking = rentalBookingService.getRentalBooking(id);

        if (booking == null || !"Upcoming".equals(booking.getStatus())) {
            model.addAttribute("errorMessage", "Hanya booking berstatus Upcoming yang dapat dibatalkan.");
            return "error/403";
        }

        model.addAttribute("booking", booking);
        return "booking/confirm-delete";
    }

    @DeleteMapping("/{id}/delete")
    public String deleteBookingConfirmed(@PathVariable String id, RedirectAttributes redirectAttributes) {
        rentalBookingService.deleteRentalBooking(id);

        redirectAttributes.addFlashAttribute("successMessage", "Pesanan berhasil dibatalkan.");
        return "redirect:/bookings";
    }

    @GetMapping("/chart")
    public String viewBookingChart(
            @RequestParam(defaultValue = "Monthly") String period,
            @RequestParam(defaultValue = "2025") int year,
            Model model){
        model.addAttribute("period", period);
        model.addAttribute("year", year);
        return "booking/booking-chart";
    }

    // Fetch data JSON Chart.js
    @GetMapping("/api/chart")
    @ResponseBody
    public List<Object[]> getBookingChartData(
            @RequestParam(defaultValue = "Quarterly") String period,
            @RequestParam(defaultValue = "2025") int year) {

        return rentalBookingService.getBookingStatistics(period, year);
    }

}
