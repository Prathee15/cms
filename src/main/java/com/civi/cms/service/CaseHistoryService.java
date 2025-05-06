package com.civi.cms.service;

import com.civi.cms.model.CaseDetails;
import com.civi.cms.model.CaseHistory;
import com.civi.cms.repository.CaseHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CaseHistoryService {

    @Autowired
    CaseHistoryRepository repository;

    @Autowired
    CaseDetailService service;

    public ResponseEntity<?> addCaseHistory(CaseHistory history){

        if(history!=null && history.getCaseDetails()!=null &&
                history.getCaseDetails().getCaseId()!=null){
            ResponseEntity<?> c1=service.getCaseById(history.getCaseDetails().getCaseId());
            if(c1!=null){
                history.setReportDate(LocalDateTime.now());
                Map<String, Object> responseData = (Map<String, Object>) c1.getBody();
                history.setCaseDetails(responseData.get("caseDetails")!=null?
                        (CaseDetails)responseData.get("caseDetails"):null);
                return ResponseEntity.ok().body(repository.save(history));
            }
            else{
                return ResponseEntity.badRequest().body("caseId does not exists.");
            }

        }
        return ResponseEntity.badRequest().body("caseId is missing in the request.");
    }
}
