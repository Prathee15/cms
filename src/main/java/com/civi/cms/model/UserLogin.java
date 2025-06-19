package com.civi.cms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class UserLogin
{
    @Id
    private String email;
    private String password;
    private Role role;
    private boolean isLocked = false;
    private boolean isWorking=true;
    @Transient
    private String jwtToken;

    @Transient
    private String firstName;
    private LocalDateTime creationDate;

    public enum Role{
        ADMIN,
        CASEWORKER,
        USER,
    }
}
