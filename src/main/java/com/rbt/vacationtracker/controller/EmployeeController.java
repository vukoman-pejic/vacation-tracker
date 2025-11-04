
package com.rbt.vacationtracker.controller;

import com.rbt.vacationtracker.entity.VacationRequest;
import com.rbt.vacationtracker.loader.EmployeeLoader;
import com.rbt.vacationtracker.model.Employee;
import com.rbt.vacationtracker.model.EmployeeVacation;
import com.rbt.vacationtracker.model.VacationRequestDTO;
import com.rbt.vacationtracker.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/employee/admin")
@RequiredArgsConstructor
public class EmployeeController {
    private static final String PATH_PREFIX = "src/main/resources/samples/";
    private final EmployeeService employeeService;
    private final EmployeeLoader employeeLoader;

    @PostMapping("/create-employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("api/v1/employee/create-employee")
                        .toUriString());
        employeeService.createEmployee(employee);
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{year}/add-vacation-days")
    public ResponseEntity<?> addVacationDays(@PathVariable Integer year,
                                             @RequestBody EmployeeVacation employeeVacation) {
        employeeService.addVacationDays(year, employeeVacation);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/days-per-year/{filename}")
    public ResponseEntity<?> addVacationDaysFromCSVFile(@PathVariable String filename) {
        String path = PATH_PREFIX + filename;
        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("api/v1/employee/vacations_{year}.csv")
                        .toUriString());
        employeeLoader.loadEmployeesVacationDays(path);
        return ResponseEntity.created(uri).build();
    }
    @PostMapping("/spent-days-per-year/{filename}")
    public ResponseEntity<?> addSpentVacationDaysFromCSVFile(@PathVariable String filename) {
        String path = PATH_PREFIX + filename;
        try {
            employeeLoader.spentVacationDaysCSV(path);
            return ResponseEntity.ok("Vacation days imported successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error importing vacation days: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<List<VacationRequestDTO>> getPendingRequests(@RequestParam Long departmentId) {
        List<VacationRequest> requests = employeeService.getPendingRequestsForDepartment(departmentId);

        List<VacationRequestDTO> dtoList = requests.stream().map(r -> VacationRequestDTO.builder()
                        .id(r.getId())
                        .employeeId(r.getEmployee().getId())
                        .year(r.getYear())
                        .startDate(r.getStartDate())
                        .endDate(r.getEndDate())
                        .status(r.getStatus().name())
                        .reason(r.getReason())
                        .build())
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/approve-request/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        employeeService.approveVacationRequest(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject-request/{id}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        employeeService.rejectVacationRequest(id);
        return ResponseEntity.ok().build();
    }
}
