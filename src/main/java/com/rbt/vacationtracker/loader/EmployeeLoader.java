package com.rbt.vacationtracker.loader;

import com.rbt.vacationtracker.entity.EmployeeEntity;
import com.rbt.vacationtracker.entity.EmployeeVacationDaysSpent;
import com.rbt.vacationtracker.model.EmployeeVacation;
import com.rbt.vacationtracker.model.Role;
import com.rbt.vacationtracker.repository.EmployeeRepository;
import com.rbt.vacationtracker.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeLoader {
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private String[] fields;

    private final PasswordEncoder passwordEncoder;

    @Value("${data.location}")
    private String path;

    /*
    This function loads all employees from a given csv file into the database.
     */
    @PostConstruct
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

    /*
    This function loads all vacation days per employee per year from a given csv file.
     */
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
    /*
        This is a help function that is used in loadEmployeesVacationDays function.
        It processes the given csv file and returns EmployeeVacation.
         */
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
    /*
            This is a help function that is used in loadEmployees function.
            It processes the given csv file and returns EmployeeEntity.
             */
    private EmployeeEntity generateEmployee(final String line) {
        if (line != null) {
            fields = line.split(",");
        } else {
            return null;
        }
        return EmployeeEntity.builder()
                .email(fields[0])
                .password(passwordEncoder.encode(fields[1]))
                .role(Set.of(Role.ROLE_USER))
                .build();
    }
    /*
    * This function is sets used vacation days per year per employee from a given csv file.
    * */
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
    /*
            This is a help function that is used in spentVacationDaysCSV function.
            It processes the given csv file and return EmployeeVacationDaysSpent.
             */
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
