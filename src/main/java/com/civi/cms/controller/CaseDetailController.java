package com.civi.cms.controller;

import com.civi.cms.model.CaseDetails;
import com.civi.cms.model.CaseHistory;
import com.civi.cms.service.CaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases")
public class CaseDetailController {

    @Autowired
    private CaseDetailService caseDetailService;

    // Get all cases
    @GetMapping
    public ResponseEntity<List<CaseDetails>> getAllCases()
    {
        List<CaseDetails> caseDetails = caseDetailService.getAllCases();
        return ResponseEntity.ok(caseDetails);
    }

    // Get case by ID
    @GetMapping("/{id}") //tested already in postman
    public ResponseEntity<?> getCaseById(@PathVariable Long id) {
        return caseDetailService.getCaseById(id);

    }

    @GetMapping("/analytics") //tested already in postman
    public ResponseEntity<?> getCaseAnalytics() {
        return caseDetailService.getCaseAnalytics();

    }

    // Create a new case
    @PostMapping("/create") //tested already in postman
    public ResponseEntity<CaseDetails> createCase(@RequestBody CaseDetails caseDetailsObj) {
        CaseDetails createdCaseDetails = caseDetailService.createCase(caseDetailsObj);
        return ResponseEntity.ok(createdCaseDetails);
    }


    //updated put - for updating cases
    @PutMapping("/update") //already tested in postman
    public ResponseEntity<CaseDetails> updateCase(@RequestBody CaseDetails caseDetailsObj)
    {

        CaseDetails updatedCaseDetails = caseDetailService.updateCase(caseDetailsObj);
        return ResponseEntity.ok(updatedCaseDetails);
    }

    @PutMapping("/updatecasehistory/{caseid}") //already tested in postman
    public ResponseEntity<?> updateCase(@PathVariable Long caseid,@RequestBody CaseHistory caseHistory)
    {
         return caseDetailService.updateCaseHistory(caseid,caseHistory);

    }



    // Delete a case
    @DeleteMapping("/{id}") //tested in postman already, its working
    public ResponseEntity<Boolean> deleteCase(@PathVariable Long id)
    {
        return ResponseEntity.ok(caseDetailService.deleteCase(id));
    }

    //get all case with clientId
    @GetMapping("/clientId/{id}") //tested already in postman
    public ResponseEntity<?> getCaseByClientId(@PathVariable Long id) {
        return caseDetailService.getCaseById(id);
    }

    @GetMapping("/status/{status}") //tested already in postman
    public ResponseEntity<?> getCaseByStatus(@PathVariable CaseDetails.CaseStatus status) {
        return caseDetailService.getCaseByStatus(status);
    }

    @GetMapping("/un-assigned") //tested already in postman
    public ResponseEntity<?> getUnAssignedCase() {
        return caseDetailService.getUnAssignedCase();
    }

    @PutMapping("/close/{caseId}")
    public ResponseEntity<?> closeCase(@PathVariable  Long caseId) {
        return caseDetailService.closeCase(caseId);
    }


}
