package com.civi.cms.controller;

import com.civi.cms.model.CaseHistory;
import com.civi.cms.service.CaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CaseHistoryController {

    @Autowired
    CaseHistoryService service;

    @PostMapping("/addhistory")
    @PreAuthorize("hasRole('CASEWORKER') or hasRole('ADMIN')")
    public ResponseEntity<?> addCaseHistory(@RequestBody CaseHistory history){
        return service.addCaseHistory(history);
    }



}
