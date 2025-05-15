package com.civi.cms.service;

import com.civi.cms.model.CaseDetails;
import com.civi.cms.model.Member;
import com.civi.cms.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MemberService {
    @Autowired
    MemberRepository repository;

    @Autowired
    CaseDetailService service;
    public ResponseEntity<?> addMember(Member m) {

        if(m!=null && m.getCaseDetails()!=null &&
                m.getCaseDetails().getCaseId()!=null){

            ResponseEntity<?> c1=service.getCaseById(m.getCaseDetails().getCaseId());
            if(c1!=null){
                Map<String, Object> responseData = (Map<String, Object>) c1.getBody();
                m.setCaseDetails(responseData.get("caseDetails")!=null?(CaseDetails)responseData.get("caseDetails"):null);
                return ResponseEntity.ok().body(repository.save(m));
            }
            else{
                return ResponseEntity.badRequest().body("caseId does not exists.");
            }

        }
        return ResponseEntity.badRequest().body("caseId is missing in the request.");
    }

    public ResponseEntity<?> removeMember(long id) {
        if(repository.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok("member deleted");
    }
}
