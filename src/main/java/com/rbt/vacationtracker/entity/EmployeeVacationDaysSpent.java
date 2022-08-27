package com.rbt.vacationtracker.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class EmployeeVacationDaysSpent {
    private final String email;
    private Date startDate;
    private Date endDate;
    private Integer year;
}
