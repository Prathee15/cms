package com.civi.cms.service;
import com.civi.cms.dto.CaseAnalyticsDTO;
import com.civi.cms.model.AssignedCaseWorkerDTO;
import com.civi.cms.model.CaseDetails;
import com.civi.cms.model.CaseHistory;
import com.civi.cms.repository.CaseAttachmentRepository;
import com.civi.cms.repository.CaseDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class CaseDetailService
{
    @Autowired
    private CaseDetailRepository caseDetailRepository;

    @Autowired
    CaseAttachmentRepository repository;

    // Get all cases
    public List<CaseDetails> getAllCases()
    {
        return caseDetailRepository.findAll();
    }

    // Get a case by ID
    public ResponseEntity<?> getCaseById(Long id) {
        Optional<CaseDetails> caseOptional = caseDetailRepository.findById(id);
        if (caseOptional.isPresent()) {
            Long count = repository.countByCaseDetails_CaseId(id);
            CaseDetails details = caseOptional.get();
            details.setAttachmentCount(count);

            // Fetch assigned case workers
            List<AssignedCaseWorkerDTO> assignedCaseWorkers = details.getAssignedCaseWorkers()
                    .stream()
                    .map(ac -> new AssignedCaseWorkerDTO(ac.getCaseWorker()))
                    .collect(Collectors.toList());

            // Convert entity to DTO
           // CaseDetailsDTO caseDetailsDTO = new CaseDetailsDTO(details, count, assignedCaseWorkers);

            // Build response
            Map<String, Object> responseData = new LinkedHashMap<>();
            List<CaseHistory> histories = details.getCaseHistories();
            if(histories.size()>1){
                Collections.sort(histories, (h1, h2) -> h2.getReportDate().compareTo(h1.getReportDate()));
                details.setCaseHistories(histories);
            }
           
            responseData.put("caseDetails", details);
            responseData.put("assignedCaseWorkers", assignedCaseWorkers);
            return ResponseEntity.ok(responseData); // HTTP 200 OK
        } else {
            return ResponseEntity.notFound().build(); // HTTP 404 Not Found
        }
    }

    // Create a new case
    public CaseDetails createCase(CaseDetails caseDetailsObj)
    {
        caseDetailsObj.setDateOpened(LocalDateTime.now());
        caseDetailsObj.setCaseStatus(CaseDetails.CaseStatus.OPEN);
        return caseDetailRepository.save(caseDetailsObj);
    }



    // Delete a case
    public boolean deleteCase(Long id)
    {
//        if (caseDetailRepository.existsById(id)) {
//            //throw new RuntimeException("Case not found with ID: " + id);
//            caseDetailRepository.deleteById(id);
//            return true;
//        }
        caseDetailRepository.deleteById(id);
      return true;
    }


    public CaseDetails updateCase(CaseDetails caseDetailsObj)
    {
        caseDetailsObj.setUpdatedAt(LocalDateTime.now());
        return caseDetailRepository.save(caseDetailsObj);
    }


    public ResponseEntity<String> updateCaseHistory(Long CaseId, CaseHistory caseHistory) {
        Optional<CaseDetails> caseOptional = caseDetailRepository.findById(CaseId);
        if (caseOptional.isPresent()) {
            CaseDetails details= caseOptional.get();
            List<CaseHistory> histories=details.getCaseHistories();
            histories.add(caseHistory);
            caseDetailRepository.save(details);
            return ResponseEntity.ok("updated successfully");
        } else {
            return ResponseEntity.badRequest().body("invalid caseid");
        }
    }

    public ResponseEntity<?> getCaseByStatus(CaseDetails.CaseStatus status) {
        List<CaseDetails> cases = caseDetailRepository.findByCaseStatus(status);
        return ResponseEntity.ok(cases);

    }

    public ResponseEntity<?> getCaseAnalytics() {
        List<CaseDetails> caseDetailsList = caseDetailRepository.findAll();
        long open = 0, closed = 0, inProgress = 0;

        for (CaseDetails caseDetails : caseDetailsList) {
            CaseDetails.CaseStatus status = caseDetails.getCaseStatus(); // adjust method if your field name is different

            if (CaseDetails.CaseStatus.OPEN.equals(status)) {
                open++;
            } else if (CaseDetails.CaseStatus.CLOSED.equals(status)) {
                closed++;
            } else if (CaseDetails.CaseStatus.IN_PROGRESS.equals(status)) {
                inProgress++;
            }
        }
        CaseAnalyticsDTO analytics = new CaseAnalyticsDTO(open, closed, inProgress);
        return ResponseEntity.ok(analytics);
    }

    public ResponseEntity<?> getUnAssignedCase() {
        return new ResponseEntity<>(caseDetailRepository.findByCaseWorkerAssignedFalse(), HttpStatus.OK);
    }

    public ResponseEntity<?> closeCase(Long id) {
        Optional<CaseDetails> caseOptional = caseDetailRepository.findById(id);
        if (caseOptional.isPresent()) {
            CaseDetails details= caseOptional.get();
            details.setCaseStatus(CaseDetails.CaseStatus.CLOSED);
            details.setDateClosed(LocalDateTime.now());
            caseDetailRepository.save(details);
            return ResponseEntity.ok("updated successfully");
        } else {
            return ResponseEntity.badRequest().body("invalid caseid");
        }
    }
}
