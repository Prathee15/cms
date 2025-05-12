package com.civi.cms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Client
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;            // Primary Key
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String phoneNumber;
    private String address;
    private String govIdType;
    private String govIdNumber;

    @OneToMany(mappedBy = "clientDetails", cascade = CascadeType.ALL)
    @JsonIgnore // Prevent infinite recursion during JSON serialization
    private List<CaseDetails> caseDetailsList;
}
