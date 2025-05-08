package com.civi.cms.service;
import com.civi.cms.model.*;
import com.civi.cms.repository.CaseDetailRepository;
import com.civi.cms.repository.CaseWorkerAssignmentRepository;
import com.civi.cms.repository.CaseWorkerRepository;
import com.civi.cms.utility.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseWorkerService {

    @Autowired
    private CaseWorkerRepository caseWorkerRepository;

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    private CaseDetailRepository caseDetailsRepository;

    @Autowired
    CaseWorkerAssignmentRepository caseWorkerAssignmentRepository;

    // Create case worker
    @Transactional
    public ResponseEntity<?> createCaseWorker(CaseWorker caseWorker) {
        try {
            caseWorker.setEmail(caseWorker.getEmail().toLowerCase());
            Optional<CaseWorker> existingByEmail = caseWorkerRepository.findByEmail(caseWorker.getEmail());
            if (existingByEmail.isPresent()) {
                return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
            }

            Optional<CaseWorker> existingByPhone = caseWorkerRepository.findByPhoneNumber(caseWorker.getPhoneNumber());
            if (existingByPhone.isPresent()) {
                return new ResponseEntity<>("Phone number already exists", HttpStatus.CONFLICT);
            }

            CaseWorker savedCaseWorker = caseWorkerRepository.save(caseWorker);
            // Step 1: Generate random password
            String password = Utility.generateRandomPassword(8); // customize length as needed
            // Step 2: Save login info
            UserLogin login = new UserLogin();
            login.setEmail(savedCaseWorker.getEmail());
            login.setPassword(password); // store encoded if needed (BCrypt)
            login.setRole(UserLogin.Role.CASEWORKER);
            ResponseEntity<?> response = userService.save(login);
            if(response.getStatusCode() != HttpStatus.CREATED){
                return response;
            }


            // Step 3: Send email
            String subject = "Welcome to the Case Management Portal";
            String body = Utility.getWelcomeEmailBoday(savedCaseWorker.getFirstName(),savedCaseWorker.getEmail(), password);
            emailService.sendEmail(savedCaseWorker.getEmail(), subject, body);

            return new ResponseEntity<>(savedCaseWorker, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Error creating case worker: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Get all active (non-deleted) case workers
    public ResponseEntity<List<CaseWorker>> getAllActiveCaseWorkers() {
        try{
            List<CaseWorker> caseWorkers = caseWorkerRepository.findByIsDeletedFalse();
            return ResponseEntity.ok(caseWorkers); // HTTP 200 OK
        }catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }

    public ResponseEntity<List<CaseWorker>> getAllDeactivatedCaseWorkers() {
        try{
            List<CaseWorker> caseWorkers = caseWorkerRepository.findByIsDeletedTrue();
            return ResponseEntity.ok(caseWorkers); // HTTP 200 OK
        }catch (Exception e){
            return ResponseEntity.status(500).body(null);
        }
    }


    // Get a case worker by email
    public ResponseEntity<CaseWorker> getCaseWorkerByEmail(Long id) {
        Optional<CaseWorker> caseWorker = caseWorkerRepository.findByCaseWorkerIdAndIsDeletedFalse(id);
        if(caseWorker.isEmpty()){
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(caseWorker.get()); // HTTP 200 OK
    }

    // Update case worker
    public ResponseEntity<?> updateCaseWorker(CaseWorker updatedCaseWorker) {
        Optional<CaseWorker> existingCaseWorker = caseWorkerRepository.
                findByCaseWorkerIdAndIsDeletedFalse(updatedCaseWorker.getCaseWorkerId());
        if (existingCaseWorker.isEmpty()) {
            return ResponseEntity.status(404).body("case worker not found");
        }

        CaseWorker updated = caseWorkerRepository.save(updatedCaseWorker);
        return ResponseEntity.ok(updated); // HTTP 200 OK
    }

    // Soft delete case worker (mark as deleted)
    @Transactional
    public ResponseEntity<String> softDeleteCaseWorker(Long id ) {
        Optional<CaseWorker> existingCaseWorker = caseWorkerRepository.
                findByCaseWorkerIdAndIsDeletedFalse(id);
        if (existingCaseWorker.isEmpty()) {
            return ResponseEntity.status(404).body("case worker not found");
        }
        CaseWorker c = existingCaseWorker.get();
        c.setDeleted(true);
        caseWorkerRepository.save(c);
        return ResponseEntity.ok("case worker deleted successfully"); // HTTP 200 OK
    }

    public ResponseEntity<?> getCaseWorkerById(Long id) {
        Optional<CaseWorker> existingCaseWorker = caseWorkerRepository.
                findByCaseWorkerIdAndIsDeletedFalse(id);
        if (existingCaseWorker.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        CaseWorker caseWorker = existingCaseWorker.get();
        Map<String, Object> response = getCaseWorkerDetails(caseWorker);
        return ResponseEntity.ok(response); // HTTP 200 OK
    }
    private Map<String, Object>  getCaseWorkerDetails(CaseWorker caseWorker) {
        Map<String, Object> response = new LinkedHashMap<>();
        List<AssignedCaseDTO> assignedCases = caseWorker.getAssignedCases()
                .stream()
                .map(ac -> new AssignedCaseDTO( ac.getCaseDetails().getCaseId(), ac.getAssignedDate()))
                .collect(Collectors.toList());
        response.put("caseWorker", caseWorker);
        response.put("assignedCases", assignedCases);
        return response;
    }


public ResponseEntity<?> assignCaseWorkerToCase(Long caseId, int workerId) {
    Optional<CaseDetails> existingCase = caseDetailsRepository.findById(caseId);
    Optional<CaseWorker> existingCaseWorker = caseWorkerRepository.findById(workerId);

    if (existingCase.isPresent() && existingCaseWorker.isPresent()) {
        CaseDetails caseDetails = existingCase.get();
        CaseWorker caseWorker = existingCaseWorker.get();

        // Check if this assignment already exists to prevent duplicates
        boolean alreadyAssigned = caseWorkerAssignmentRepository.existsByCaseDetailsAndCaseWorker(caseDetails, caseWorker);
        if (alreadyAssigned) {
            return ResponseEntity.ok("Case worker already assigned to this case.");
        }

        // Create a new assignment
        CaseWorkerAssignment assignment = new CaseWorkerAssignment();
        assignment.setCaseDetails(caseDetails);
        assignment.setCaseWorker(caseWorker);
        assignment.setAssignedDate(LocalDateTime.now());

        // Save the assignment
        caseWorkerAssignmentRepository.save(assignment);
        existingCase.get().setCaseWorkerAssigned(true);
        caseDetailsRepository.save(existingCase.get());

        return ResponseEntity.ok("Case worker assigned successfully.");
    }

    return ResponseEntity.notFound().build();
}

    public ResponseEntity<?> getCaseByCaseWorker(String email) {
        Optional<CaseWorker> caseWorkerOptional = caseWorkerRepository.findByEmail(email);
        if(caseWorkerOptional.isEmpty()){
            return ResponseEntity.status(404).body("case worker not found");
        }
        CaseWorker caseWorker = caseWorkerOptional.get();
        List<CaseWorkerAssignment> assignments = caseWorker.getAssignedCases();

        if(assignments.isEmpty()){
            return ResponseEntity.status(404).body("no case assigned to this case worker");
        }
        List<CaseDetails>   caseDetailsList = new ArrayList<>();
        for (CaseWorkerAssignment assignment : assignments) {
            caseDetailsList.add(assignment.getCaseDetails());
        }
        return ResponseEntity.ok(caseDetailsList);

    }
}
