package com.rbt.vacationtracker.service;

import com.rbt.vacationtracker.entity.*;
import com.rbt.vacationtracker.model.Employee;
import com.rbt.vacationtracker.model.EmployeeVacation;
import com.rbt.vacationtracker.model.Role;
import com.rbt.vacationtracker.repository.EmployeeRepository;
import com.rbt.vacationtracker.repository.VacationRepository;
import com.rbt.vacationtracker.repository.VacationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmployeeService {
   private final EmployeeRepository employeeRepository;
   private final VacationRepository vacationRepository;
    private final VacationRequestRepository vacationRequestRepository;

    public EmployeeEntity findJwtUserByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found."));
    }

    /*
    This function is creating a new employee and inserting it in database
     */
   public void createEmployee(Employee employee) {
        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .email(employee.getEmail())
                .password(employee.getPassword())
                .role(Set.of(Role.ROLE_USER))
                .build();

        employeeRepository.save(employeeEntity);
        log.info("Employee: {} saved in database", employee.getEmail());
    }
    /*
    This function is adding a vacation days for a given year for employee
     */
    public void addVacationDays(Integer year, EmployeeVacation employeeVacation) {
       EmployeeEntity employeeEntity = employeeRepository.findByEmail(employeeVacation.getEmail())
               .orElseThrow(() -> new NoSuchElementException());

        VacationEntity vacationEntity = VacationEntity.builder()
                        .days(employeeVacation.getDays())
                        .usedDays(0)
                        .freeDays(employeeVacation.getDays())
                        .year(year)
                        .employee(employeeEntity)
                        .build();
        vacationRepository.save(vacationEntity);
        employeeEntity.getVacations().add(vacationEntity);
        log.info("Added {} days for user {}", employeeVacation.getDays(), employeeVacation.getEmail());
    }

    /*
        This function is setting a number of used vacation days as well as free days per year.
    */
    public void usedVacationDaysManagement(EmployeeVacationDaysSpent employeeVacationDaysSpent) {
        EmployeeEntity employeeEntity = employeeRepository.findByEmail(employeeVacationDaysSpent.getEmail())
                .orElseThrow(() -> new NoSuchElementException("Employee not found: " + employeeVacationDaysSpent.getEmail()));

        List<VacationEntity> vacations = employeeEntity.getVacations();
        VacationEntity vacationEntity = getVacationWithGivenYear(employeeVacationDaysSpent.getYear(), vacations);

        if (vacationEntity == null) {
            throw new NoSuchElementException("No vacation record for year " + employeeVacationDaysSpent.getYear() +
                    " for employee " + employeeVacationDaysSpent.getEmail());
        }

        // Calculate days requested
        long diffInMillies = Math.abs(employeeVacationDaysSpent.getEndDate().getTime() - employeeVacationDaysSpent.getStartDate().getTime());
        int daysRequested = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1; // +1 for inclusive

        // Check if enough free days exist
        if (vacationEntity.getFreeDays() < daysRequested) {
            log.warn("Employee {} exceeded free days: has {}, requested {}",
                    employeeVacationDaysSpent.getEmail(), vacationEntity.getFreeDays(), daysRequested);
            // Optional: throw an exception or just skip
            throw new IllegalArgumentException("Employee " + employeeVacationDaysSpent.getEmail() +
                    " exceeded free vacation days!");
        }

        // Deduct days
        vacationEntity.setFreeDays(vacationEntity.getFreeDays() - daysRequested);
        vacationEntity.setUsedDays(vacationEntity.getUsedDays() + daysRequested);

        vacationRepository.save(vacationEntity);

        log.info("Employee {} used {} days. Remaining: {}", employeeVacationDaysSpent.getEmail(),
                daysRequested, vacationEntity.getFreeDays());
    }

    /*
        This is a help function that is used in usedVacationDaysManagement function.
        It returns you a VacationEntity for a given year.
    */
    private VacationEntity getVacationWithGivenYear(Integer year, List<VacationEntity> vacationEntities) {
       for (VacationEntity v: vacationEntities) {
           if (Objects.equals(v.getYear(), year)) {
               return v;
           }
       }
       return null;
    }

    @Transactional
    public void approveVacationRequest(Long requestId) {
        VacationRequest request = vacationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Vacation request not found: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is already " + request.getStatus());
        }

        EmployeeEntity employee = request.getEmployee();

        // Find the employee's vacation entity for the requested year
        VacationEntity vacation = getVacationForYear(employee, request.getYear());
        if (vacation == null) {
            throw new IllegalStateException("No vacation record found for year " + request.getYear());
        }

        // Calculate days requested
        long diffInMillis = Math.abs(request.getEndDate().getTime() - request.getStartDate().getTime());
        int daysRequested = (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) + 1;

        // Check if enough free days exist
        if (vacation.getFreeDays() < daysRequested) {
            throw new IllegalArgumentException("Employee " + employee.getEmail() + " does not have enough free days");
        }

        // Deduct days and save
        vacation.setFreeDays(vacation.getFreeDays() - daysRequested);
        vacation.setUsedDays(vacation.getUsedDays() + daysRequested);
        vacationRepository.save(vacation);

        // Mark request as approved
        request.setStatus(RequestStatus.APPROVED);
        vacationRequestRepository.save(request);

        log.info("Approved vacation request {} for employee {}", requestId, employee.getEmail());
    }

    @Transactional
    public void rejectVacationRequest(Long requestId) {
        VacationRequest request = vacationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Vacation request not found: " + requestId));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is already " + request.getStatus());
        }

        request.setStatus(RequestStatus.REJECTED);
        vacationRequestRepository.save(request);

        log.info("Rejected vacation request {} for employee {}", requestId, request.getEmployee().getEmail());
    }

    public List<VacationRequest> getPendingRequestsForDepartment(Long departmentId) {
        return vacationRequestRepository.findByDepartmentIdAndStatus(departmentId, RequestStatus.PENDING);
    }

    private VacationEntity getVacationForYear(EmployeeEntity employee, Integer year) {
        return employee.getVacations().stream()
                .filter(v -> v.getYear().equals(year))
                .findFirst()
                .orElse(null);
    }
}
