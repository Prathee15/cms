package com.civi.cms.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "caseId") // Prevents circular reference
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
public class CaseDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseId;

    private String caseTitle;
    @Column(columnDefinition = "TEXT")
    private String caseDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id") // foreign key column in CaseDetails table
    @NotFound(action = NotFoundAction.IGNORE) // Add this
    private Client clientDetails;
    private CaseStatus caseStatus;
    private String priorityLevel;
    private String riskLevel;
    private String caseCategory;
    private String legalInvolvement;
    private String referralSource;
    @Column(columnDefinition = "boolean default false",name = "case_worker_assigned")
    private boolean caseWorkerAssigned;

    private LocalDateTime dateOpened;
    private LocalDateTime dateClosed;
    private LocalDateTime nextReviewDate;

    private String createdBy;
    private String updatedBy;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "c", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowUp> followUps;

    @ElementCollection
    @OneToMany(mappedBy = "caseDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Member> membersInvolved;

    // Relationships
    @JsonIgnore
    @OneToMany(mappedBy = "caseDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CaseAttachment> attachments;

    private Long attachmentCount;
    @OneToMany(mappedBy = "caseDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CaseHistory> caseHistories;

    @ManyToMany
    @JoinTable(
            name = "serviceProvider",
            joinColumns = @JoinColumn(name = "case_id"),
            inverseJoinColumns = @JoinColumn(name = "provider_id")
    )
    private List<ServiceProvider> serviceProviders;

//    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinTable(
//            name = "case_worker_assignment",
//            joinColumns = @JoinColumn(name = "case_id"),
//            inverseJoinColumns = @JoinColumn(name = "case_worker_id")
//    )
//    private List<CaseWorker> assignedCaseWorkers = new ArrayList<>();
//    public void addCaseWorker(CaseWorker caseWorker) {
//        assignedCaseWorkers.add(caseWorker);
//        caseWorker.getCaseDetails().add(this);  // Ensure bidirectional update
//    }
    @JsonIgnore
    @OneToMany(mappedBy = "caseDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CaseWorkerAssignment> assignedCaseWorkers = new ArrayList<>();

    public List<CaseWorkerAssignment> getAssignedCaseWorkers() {
        return assignedCaseWorkers;
    }

    public void setAssignedCaseWorkers(List<CaseWorkerAssignment> assignedCaseWorkers) {
        this.assignedCaseWorkers = assignedCaseWorkers;
    }

    public enum CaseStatus{
        OPEN,
        IN_PROGRESS,
        HOLD,
        CLOSED
    }
}
