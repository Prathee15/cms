package com.civi.cms.service;

import com.civi.cms.model.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
@Service
public class AttachmentService {
    @Autowired
    private AttachmentRepository attachmentRepository;

    public Attachment saveAttachment(MultipartFile file, String username, String attachmentType) throws IOException, IOException {
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFileData(file.getBytes());
        attachment.setUsername(username);
        attachment.setAttachmentType(attachmentType);
        // `downloadUrl` can be constructed if you have a frontend or known base path
        attachment.setDownloadUrl(null);
        return attachmentRepository.save(attachment);
    }

    public Optional<Attachment> getAttachmentById(Long id) {
        return attachmentRepository.findById(id);
    }

    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll();
    }
}
