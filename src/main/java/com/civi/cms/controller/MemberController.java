package com.civi.cms.controller;

import com.civi.cms.model.Member;
import com.civi.cms.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member/v1")
public class MemberController {

    @Autowired
    MemberService service;

    @PostMapping("/add")
    @PreAuthorize("hasRole('CASEWORKER') or hasRole('ADMIN')")
    public ResponseEntity<?> addMember(@RequestBody Member m){
        return service.addMember(m);
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('CASEWORKER') or hasRole('ADMIN')")
    public ResponseEntity<?> removeMember(@PathVariable long id ){
        return service.removeMember(id);
    }
}
