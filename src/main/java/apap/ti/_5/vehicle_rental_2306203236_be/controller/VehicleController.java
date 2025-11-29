// package apap.ti._5.vehicle_rental_2306203236_be.controller;

// import java.util.List;
// import java.util.stream.Collectors;

// import java.util.Map;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;
// import jakarta.validation.Valid;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.ReadVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.model.Vehicle;
// import apap.ti._5.vehicle_rental_2306203236_be.model.RentalVendor;
// import apap.ti._5.vehicle_rental_2306203236_be.repository.RentalVendorRepository;
// import apap.ti._5.vehicle_rental_2306203236_be.service.VehicleService;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.CreateVehicleDto;
// import apap.ti._5.vehicle_rental_2306203236_be.dto.vehicle.UpdateVehicleDto;

// @Controller
// @RequestMapping("/vehicles")
// public class VehicleController {

//     @Autowired
//     private VehicleService vehicleService;

//     @Autowired
//     private RentalVendorRepository rentalVendorRepository;

//     @GetMapping
//     public String viewAllVehicles(
//             @RequestParam(value = "keyword", required = false) String keyword,
//             @RequestParam(value = "type", required = false) String type,
//             Model model) {

//         List<ReadVehicleDto> filteredVehicles = vehicleService.getAllVehicleDto(keyword, type);
        
//         model.addAttribute("vehicles", filteredVehicles);
//         model.addAttribute("selectedKeyword", keyword);
//         model.addAttribute("selectedType", type);

//         return "vehicle/view-all";
//     }

//     @GetMapping("/{id}")
//     public String viewVehicle(@PathVariable ("id") String id, Model model) {
//         Vehicle vehicle = vehicleService.getVehicle(id);

//         if (vehicle == null) {
//             model.addAttribute("title", "Vehicle not found");
//             model.addAttribute("message", "Vehicle with id " + id + " not found");
//             return "error/404";
//         }

//         model.addAttribute("vehicle", vehicle);
//         return "vehicle/detail";
//     }

//     @GetMapping("/create")
//     public String createVehicleForm(Model model) {
//         var rentalVendors = rentalVendorRepository.findAll();

//         Map<Integer, List<String>> rentalVendorLocations = rentalVendors.stream()
//         .collect(Collectors.toMap(
//             RentalVendor::getId,
//             RentalVendor::getListOfLocations
//         ));

//         model.addAttribute("vehicle", new CreateVehicleDto());
//         model.addAttribute("vendors", rentalVendors);
//         model.addAttribute("rentalVendorLocations", rentalVendorLocations);
//         model.addAttribute("isEdit", false);
//         return "vehicle/form";
//     }

//     @PostMapping("/create")
//     public String createVehicle(@Valid @ModelAttribute CreateVehicleDto createVehicleDto, 
//             BindingResult bindingResult,
//             RedirectAttributes redirectAttributes, Model model) {

//         if(bindingResult.hasErrors()) {
//             model.addAttribute("errorMessage", "Validation failed. Please check your input.");
//             model.addAttribute("bindingResult", bindingResult);
//             return "vehicle/form";
//         }

//         Vehicle newVehicle = vehicleService.createVehicle(createVehicleDto);
//         if(newVehicle == null) {
//             model.addAttribute("errorMessage", "Failed to create new vehicle.");
//             return "vehicle/form";
//         }

//         redirectAttributes.addFlashAttribute("successMessage", "Successfully created new vehicle.");
//         return "redirect:/vehicles";
//     }

//     @GetMapping("/update/{id}")
//     public String updateForm(@PathVariable("id") String id, Model model) {
//         Vehicle vehicle = vehicleService.getVehicle(id);

//         if (vehicle == null) {
//             model.addAttribute("title", "Vehicle not found");
//             model.addAttribute("message", "Vehicle with id " + id + " not found");
//             return "error/404";
//         }

//         if (vehicle.getStatus().equals("In Use")) {
//             throw new IllegalArgumentException("Cannot update vehicle that is currently rented");
//         }

//         var rentalVendors = rentalVendorRepository.findAll();
//         Map<Integer, List<String>> rentalVendorLocations = rentalVendors.stream()
//             .collect(Collectors.toMap(
//                 RentalVendor::getId,
//                 RentalVendor::getListOfLocations
//             ));

//         model.addAttribute("vehicle", vehicle);
//         model.addAttribute("vendors", rentalVendors);
//         model.addAttribute("rentalVendorLocations", rentalVendorLocations);
//         model.addAttribute("isEdit", true);
//         model.addAttribute("vehicleId", id);

//         return "vehicle/form";
//     }


//     @PutMapping("/{id}/update")
//     public String updateVehicle(
//             @PathVariable("id") String id,
//             @Valid @ModelAttribute UpdateVehicleDto updateVehicleDto,
//             BindingResult bindingResult,
//             RedirectAttributes redirectAttributes,
//             Model model) {

//         if (!id.equals(updateVehicleDto.getId())) {
//             redirectAttributes.addFlashAttribute("errorMessage", "Invalid vehicle id.");
//             return "redirect:/vehicles";
//         }

//         if (bindingResult.hasErrors()) {
//             redirectAttributes.addFlashAttribute("errorMessage", "Validation failed. Please check your input.");
//             return "redirect:/vehicles/update/" + id;
//         }

//         Vehicle updatedVehicle = vehicleService.updateVehicle(updateVehicleDto);
//         if (updatedVehicle == null) {
//             redirectAttributes.addFlashAttribute("errorMessage", "Vehicle with id " + id + " not found.");
//             return "redirect:/vehicles";
//         }

//         redirectAttributes.addFlashAttribute("successMessage", "Successfully updated vehicle with id " + id + ".");
//         return "redirect:/vehicles";
//     }


//     @DeleteMapping("/{id}/delete")
//     public String deleteVehicle(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
//         Vehicle vehicle = vehicleService.getVehicle(id);

//         if (vehicle == null) {
//             redirectAttributes.addFlashAttribute("errorMessage", "Vehicle with id " + id + " not found.");
//             return "redirect:/vehicles";
//         }

//         if ("In Use".equalsIgnoreCase(vehicle.getStatus())) {
//             redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete vehicle that is currently rented or in use.");
//             return "redirect:/vehicles";
//         }

//         Vehicle removedVehicle = vehicleService.deleteVehicle(id);
//         if (removedVehicle != null) {
//             redirectAttributes.addFlashAttribute("successMessage", "Successfully delete vehicle with ID " + id);
//             return "redirect:/vehicles";
//         } else {
//             redirectAttributes.addFlashAttribute("errorMessage",
//                     "Vehicle with ID " + id + " not found.");
//         }

//         return "redirect:/vehicles";
//     }

// }
