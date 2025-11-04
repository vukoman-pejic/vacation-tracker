package com.rbt.vacationtracker.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class VacationRequestDTO {
    private Long id;
    private Long employeeId;
    private Integer year;
    private Date startDate;
    private Date endDate;
    private String status;
    private String reason;
}
