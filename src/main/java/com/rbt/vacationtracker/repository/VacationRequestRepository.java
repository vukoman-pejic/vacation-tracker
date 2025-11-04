package com.rbt.vacationtracker.repository;

import com.rbt.vacationtracker.entity.RequestStatus;
import com.rbt.vacationtracker.entity.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {
    List<VacationRequest> findByDepartmentIdAndStatus(Long departmentId, RequestStatus status);
}
