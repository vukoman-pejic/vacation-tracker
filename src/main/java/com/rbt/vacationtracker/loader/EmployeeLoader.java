package com.rbt.vacationtracker.loader;

import com.rbt.vacationtracker.entity.EmployeeEntity;
import com.rbt.vacationtracker.entity.EmployeeVacationDaysSpent;
import com.rbt.vacationtracker.model.EmployeeVacation;
import com.rbt.vacationtracker.repository.EmployeeRepository;
import com.rbt.vacationtracker.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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

    public void spentVacationDaysCSV(String path) {
        try (Scanner sc = new Scanner(new File(path))) {
            sc.nextLine();
            while (sc.hasNextLine()) {
                EmployeeVacationDaysSpent employeeVacationDaysSpent = generateDateDifference(sc.nextLine());
                employeeService.usedVacationDaysManagement(employeeVacationDaysSpent);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private EmployeeVacationDaysSpent generateDateDifference(String line) throws ParseException {
        if (line != null) {
            fields = line.split(",");
        } else {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
        String monthDay = fields[2].substring(1);
        String year = fields[3].substring(1, 5);
        String startDate = monthDay + ", " + year;
        Date firstDate = sdf.parse(startDate);
        monthDay = fields[5].substring(1);
        year = fields[6].substring(1, 5);
        String endDate = monthDay + ", " + year;
        Date secondDate = sdf.parse(endDate);

        return EmployeeVacationDaysSpent.builder()
                .email(fields[0])
                .startDate(firstDate)
                .endDate(secondDate)
                .year(Integer.parseInt(year))
                .build();
    }
}
