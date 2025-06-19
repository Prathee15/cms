package com.civi.cms.controller;

import com.civi.cms.model.CaseAttachment;
import com.civi.cms.model.CaseAttachmentDTO;
import com.civi.cms.service.CaseAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/case")
public class CaseAttachmentController
{
    @Autowired
    private CaseAttachmentService caseAttachmentService;

    @PostMapping(value = "/uploadfile/{caseid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CASEWORKER') or hasRole('ADMIN')")
    public ResponseEntity<?> createCaseAttachment(
            @PathVariable Long caseid,
            @RequestPart("file") MultipartFile file) {

        return caseAttachmentService.createCaseAttachment(caseid,file);
    }

    @GetMapping("/attachments/{id}/download")
    @PreAuthorize("hasRole('CASEWORKER') or hasRole('ADMIN')")
    public ResponseEntity<CaseAttachment> getCaseById(@PathVariable Long id)
    {
        return caseAttachmentService.getCaseAttachmentById(id);
    }

    @DeleteMapping("/attachment/{id}")
    @PreAuthorize("hasRole('CASEWORKER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteCaseAttachment(@PathVariable Long id)
    {
        return ResponseEntity.ok(caseAttachmentService.deleteCaseAttachment(id));
    }

    @GetMapping("/metadata/{caseid}")
    @PreAuthorize("hasRole('CASEWORKER') or hasRole('ADMIN')")
    public ResponseEntity<List<CaseAttachmentDTO>> getFileMetadata(@PathVariable Long caseid){
        return caseAttachmentService.getCaseAttachmentMetaDataByCaseId(caseid);
    }

}
