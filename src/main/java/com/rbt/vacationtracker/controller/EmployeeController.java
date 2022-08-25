package com.rbt.vacationtracker.controller;

import com.rbt.vacationtracker.model.Employee;
import com.rbt.vacationtracker.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/create-employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee){
        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("api/v1/employee/create-employee")
                        .toUriString());
        return ResponseEntity.created(uri).build();
    }
}
