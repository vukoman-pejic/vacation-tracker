package com.rbt.vacationtracker.entity;

import lombok.*;

import javax.persistence.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vacation")
public class VacationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer year;
    private Integer days;

   @ManyToOne
   private EmployeeEntity employee;
}
