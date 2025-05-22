package com.civi.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
public class CaseHistory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long progressId;
    @Column(columnDefinition = "TEXT")
    private String progressReport;

    private String title;

    private String updatedBy;

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CaseDetails getCaseDetails() {
        return caseDetails;
    }

    public void setCaseDetails(CaseDetails caseDetails) {
        this.caseDetails = caseDetails;
    }

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    @JsonBackReference
    private CaseDetails caseDetails;

    private LocalDateTime reportDate;

    public long getProgressId() {
        return progressId;
    }

    public void setProgressId(long progressId) {
        this.progressId = progressId;
    }

    public String getProgressReport() {
        return progressReport;
    }

    public void setProgressReport(String progressReport) {
        this.progressReport = progressReport;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }
}
