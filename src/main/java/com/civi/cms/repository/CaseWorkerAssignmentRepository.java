package com.civi.cms.repository;

import com.civi.cms.model.CaseDetails;
import com.civi.cms.model.CaseWorker;
import com.civi.cms.model.CaseWorkerAssignment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CaseWorkerAssignmentRepository extends JpaRepository<CaseWorkerAssignment, Long> {
    boolean existsByCaseDetailsAndCaseWorker(CaseDetails caseDetails, CaseWorker caseWorker);

    @Modifying
    @Transactional
    @Query("DELETE FROM CaseWorkerAssignment cwa WHERE cwa.caseDetails.caseId = :caseId")
    void deleteByCaseId(@Param("caseId") Long caseId);

    @Query("SELECT cwa FROM CaseWorkerAssignment cwa WHERE cwa.caseDetails.caseId = :caseId")
    List<CaseWorkerAssignment> findByCaseId(@Param("caseId") Long caseId);
}

