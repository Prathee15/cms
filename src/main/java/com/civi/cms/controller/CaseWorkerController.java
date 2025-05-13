package com.civi.cms.controller;

import com.civi.cms.model.CaseWorker;
import com.civi.cms.service.CaseWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/case-worker")
public class CaseWorkerController {

    @Autowired
    private CaseWorkerService caseWorkerService;

    // Create a new case worker
    @PostMapping("/create")
    public ResponseEntity<?> createCaseWorker(@RequestBody CaseWorker caseWorker) {
        return caseWorkerService.createCaseWorker(caseWorker);
    }

    @PostMapping("/assign-caseworker/caseid/{caseid}/worker/{workerid}")
    public ResponseEntity<?> createCaseWorker(@PathVariable Long caseid, @PathVariable Integer workerid) {
        return caseWorkerService.assignCaseWorkerToCase(caseid, workerid,false);
    }

    @PostMapping("/re-assign-caseworker/caseid/{caseid}/worker/{workerid}")
    public ResponseEntity<?> reAssignCaseWorker(@PathVariable Long caseid, @PathVariable Integer workerid) {
        return caseWorkerService.assignCaseWorkerToCase(caseid, workerid,true);
    }


    // Get all case workers
    @GetMapping("/retrieve-all")
    public ResponseEntity<List<CaseWorker>> getAllCaseWorkers() {
        return (caseWorkerService.getAllActiveCaseWorkers());
    }

    // Get all case workers
    @GetMapping("/deactivated-caseworkers")
    public ResponseEntity<List<CaseWorker>> getDeactivatedCaseWorkers() {
        return (caseWorkerService.getAllDeactivatedCaseWorkers());
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<?> getCaseWorkerById(@PathVariable Long id) {
        return (caseWorkerService.getCaseWorkerById(id));
    }

    // Update a case worker
    @PutMapping("/update")
    public ResponseEntity<?> updateCaseWorker(@RequestBody CaseWorker updatedCaseWorker) {
        return (caseWorkerService.updateCaseWorker(updatedCaseWorker));
    }

    // Delete a case worker
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCaseWorker(@PathVariable Long id) {
        return caseWorkerService.softDeleteCaseWorker(id);
    }

    @GetMapping("/assigned-cases/{email}")
    public ResponseEntity<?> getCaseByCaseWorker(@PathVariable String email) {
        return caseWorkerService.getCaseByCaseWorker(email);
    }
}
