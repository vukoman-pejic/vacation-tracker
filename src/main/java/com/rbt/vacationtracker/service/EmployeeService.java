package com.rbt.vacationtracker.service;

import com.rbt.vacationtracker.model.Employee;
import com.rbt.vacationtracker.repository.EmployeeEntity;
import com.rbt.vacationtracker.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {
    private EmployeeRepository employeeRepository;

    public void createEmployee(Employee employee){
        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .email(employee.getEmail())
                .password(employee.getPassword())
                .build();
        employeeRepository.save(employeeEntity);
        log.info("Employee: {} saved in database", employee.getEmail());
    }

}
