package com.rbt.vacationtracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "vacation_request")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private EmployeeEntity employee;

    @ManyToOne
    private DepartmentEntity department;

    private Integer year;

    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING, APPROVED, REJECTED

    private String reason; // Optional
}



