package com.civi.cms.service;

import com.civi.cms.model.AttachmentMetadata;
import com.civi.cms.model.CaseAttachment;
import com.civi.cms.model.CaseAttachmentDTO;
import com.civi.cms.model.CaseDetails;
import com.civi.cms.repository.CaseAttachmentRepository;
import com.civi.cms.repository.CaseDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CaseAttachmentService 
{
   @Autowired
   private  CaseAttachmentRepository caseAttachmentRepository;

   @Autowired
    CaseDetailRepository  caseDetailRepository;

    public ResponseEntity<?> createCaseAttachment(Long caseId, MultipartFile file)
    {
        CaseAttachment caseAttachment = new CaseAttachment();
        try {
            Optional<CaseDetails> optionalCaseDetails = caseDetailRepository.findById(caseId);
            if (optionalCaseDetails.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Case not found");
            CaseDetails details = new CaseDetails();
            details.setCaseId(caseId);
            caseAttachment.setCaseDetails(details);
            caseAttachment.setFileName(file.getOriginalFilename());
            caseAttachment.setFileType(file.getContentType());
            caseAttachment.setFileData(file.getBytes());
            caseAttachmentRepository.save(caseAttachment);
            //details.setAttachmentCount(details.getAttachmentCount()+1);
            //caseDetailRepository.save(details);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok("file uploaded successfully");
    }

    public ResponseEntity<CaseAttachment> getCaseAttachmentById(Long id)
    {
        Optional<CaseAttachment> caseAttachmentOptional = caseAttachmentRepository.findById(id);
        if (caseAttachmentOptional.isPresent()) {
            return ResponseEntity.ok(caseAttachmentOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    public ResponseEntity<List<CaseAttachmentDTO>> getCaseAttachmentMetaDataByCaseId(Long id)
    {
        List<AttachmentMetadata> metadata = caseAttachmentRepository.findByCaseDetails_CaseId(id);
        if(metadata.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        List<CaseAttachmentDTO> metadataList = metadata.stream().map(att -> {
            CaseAttachmentDTO dto = new CaseAttachmentDTO();
            dto.setAttachmentId(att.getAttachmentId());
            dto.setFileName(att.getFileName());
            dto.setDownloadUrl("/api/case/attachments/" + att.getAttachmentId() + "/download");
            dto.setFileType(att.getFileType());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(metadataList);
    }
    public ResponseEntity<?> deleteCaseAttachment(Long id)
    {
        if (caseAttachmentRepository.existsById(id)) {
            caseAttachmentRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
