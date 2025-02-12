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
    private String progressReport;

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

    //public long getCaseId() {
        //return caseId;
    //}

    //public void setCaseId(long caseId) {
      //  this.caseId = caseId;
    //}

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
