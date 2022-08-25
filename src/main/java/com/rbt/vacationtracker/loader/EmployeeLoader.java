package com.rbt.vacationtracker.loader;

import com.rbt.vacationtracker.repository.EmployeeEntity;
import com.rbt.vacationtracker.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeLoader {
    private final EmployeeRepository employeeRepository;
    private String[] fields;

    @Value("${data.location}")
    private String path;

    @PostConstruct
    public void loadEmployees() {
        try (Scanner sc = new Scanner(new File(path))) {
            //skip first line
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
