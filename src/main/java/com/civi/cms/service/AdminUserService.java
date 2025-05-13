package com.civi.cms.service;

import com.civi.cms.model.AdminUser;
import com.civi.cms.model.CaseWorker;
import com.civi.cms.model.UserLogin;
import com.civi.cms.repository.AdminRepository;
import com.civi.cms.utility.Utility;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminUserService {

    @Autowired
    AdminRepository adminUserRepository;

    @Autowired
    UserService  userService;

    @Autowired
    EmailService emailService;

    // CREATE
    @Transactional
    public ResponseEntity<?> createAdminUser(AdminUser user) {

        Optional<AdminUser> existingByEmail = adminUserRepository.findById(user.getEmail().toLowerCase());
        if(userService.isEmailExists(user.getEmail())||existingByEmail.isPresent()){
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }

        Optional<AdminUser> existingByPhone = adminUserRepository.findByPhoneNumber(user.getPhoneNumber());
        if (existingByPhone.isPresent()) {
            return new ResponseEntity<>("Phone number already exists", HttpStatus.CONFLICT);
        }
        AdminUser adminUser = adminUserRepository.save(user);

        // Step 2: Save login info

        UserLogin login = new UserLogin();
        String password = Utility.generateRandomPassword(8);
        login.setEmail(adminUser.getEmail());
        login.setPassword(password); // store encoded if needed (BCrypt)
        login.setRole(UserLogin.Role.ADMIN);
        ResponseEntity<?> response = userService.save(login);
        if(response.getStatusCode() != HttpStatus.CREATED){
            return response;
        }


        // Step 3: Send email
        String subject = "Welcome to the Case Management Portal";
        String body = Utility.getWelcomeEmailBoday(adminUser.getFirstName(),adminUser.getEmail(), password);
        emailService.sendEmail(adminUser.getEmail(), subject, body);
        return new ResponseEntity<>(adminUser, HttpStatus.CREATED);
    }

    // READ - Get All
    public List<AdminUser> getAllAdminUsers() {
        return adminUserRepository.findAll();
    }

    // READ - Get by ID
    public ResponseEntity<?> getAdminUserById(String email) {

        Optional<AdminUser> existingByEmail = adminUserRepository.findById(email.toLowerCase());
        if(existingByEmail.isEmpty()){
            return new ResponseEntity<>("user does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(existingByEmail.get(), HttpStatus.OK);
    }

    // UPDATE
    public ResponseEntity<?> AdminUser (String email, AdminUser updatedUser) {
        return new ResponseEntity<>(adminUserRepository.save(updatedUser),HttpStatus.OK);
    }

}
