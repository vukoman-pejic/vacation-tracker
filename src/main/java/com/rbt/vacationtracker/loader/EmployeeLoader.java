package com.rbt.vacationtracker.loader;

import com.rbt.vacationtracker.entity.EmployeeEntity;
import com.rbt.vacationtracker.model.EmployeeVacation;
import com.rbt.vacationtracker.repository.EmployeeRepository;
import com.rbt.vacationtracker.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeLoader {
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private String[] fields;

    @Value("${data.location}")
    private String path;

//    @PostConstruct
    public void loadEmployees() {
        try (Scanner sc = new Scanner(new File(path))) {
            sc.nextLine();
            sc.nextLine();
            while (sc.hasNextLine()) {
                EmployeeEntity employeeEntity = generateEmployee(sc.nextLine());
                employeeRepository.save(employeeEntity);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void loadEmployeesVacationDays (String path) {
        try (Scanner sc = new Scanner(new File(path))) {
            String line = sc.nextLine();
            Integer year = Integer.parseInt(line);
            sc.nextLine();
            while (sc.hasNextLine()) {
                EmployeeVacation employeeVacation = generateEmployeeVacation(sc.nextLine());
                employeeService.addVacationDays(year, employeeVacation);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private EmployeeVacation generateEmployeeVacation(String line) {
        if (line != null) {
            fields = line.split(",");
        } else {
            return null;
        }
        return EmployeeVacation.builder()
                .email(fields[0])
                .days(Integer.parseInt(fields[1]))
                .build();
    }

    private EmployeeEntity generateEmployee(final String line) {
        if (line != null) {
            fields = line.split(",");
        } else {
            return null;
        }
        return EmployeeEntity.builder()
                .email(fields[0])
                .password(fields[1])
                .build();
    }
}
