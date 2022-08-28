
package com.rbt.vacationtracker.controller;

import com.rbt.vacationtracker.loader.EmployeeLoader;
import com.rbt.vacationtracker.model.Employee;
import com.rbt.vacationtracker.model.EmployeeVacation;
import com.rbt.vacationtracker.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private static final String PATH_PREFIX = "src/main/resources/samples/";
    private final EmployeeService employeeService;
    private final EmployeeLoader employeeLoader;

    /*

     */
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
        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("api/v1/employee/vacations_{year}.csv")
                        .toUriString());
        employeeLoader.spentVacationDaysCSV(path);
        return ResponseEntity.created(uri).build();
    }
}
