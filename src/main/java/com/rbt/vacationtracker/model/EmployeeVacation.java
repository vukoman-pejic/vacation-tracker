package com.rbt.vacationtracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmployeeVacation {
    private String email;
    private Integer days;
}
