package com.civi.cms.service;

import com.civi.cms.model.AdminUser;
import com.civi.cms.model.CaseWorker;
import com.civi.cms.model.PasswordResetToken;
import com.civi.cms.model.UserLogin;
import com.civi.cms.repository.AdminRepository;
import com.civi.cms.repository.CaseWorkerRepository;
import com.civi.cms.repository.PasswordResetTokenRepository;
import com.civi.cms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService
{

    UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    CaseWorkerRepository caseWorkerRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    EmailService  emailService;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Initialize password encoder
    }

    public ResponseEntity<?> save(UserLogin userlogin)
    {
        Optional<UserLogin> userLoginOptional = userRepository.findById(userlogin.getEmail().toLowerCase());
        if(userLoginOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
        }
        userlogin.setPassword(passwordEncoder.encode(userlogin.getPassword()));
        userlogin.setCreationDate(LocalDateTime.now());
        userlogin.setEmail(userlogin.getEmail().toLowerCase());
        UserLogin login = userRepository.save(userlogin);
        return ResponseEntity.status(HttpStatus.CREATED).body(login);
    }

    public ResponseEntity<?> validateUsernameAndPassword(String username, String password) {
        Optional<UserLogin> userOptional = userRepository.findById(username.toLowerCase());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username. Please check and try again.");
        }

        UserLogin user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password. Please check and try again.");
        }
        else{
            if(!user.isWorking())
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not part of company!");
            }
            else if(user.isLocked())
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Account is locked!");
            }

        }
        user.setPassword(null); // Remove password from response
        String name="";
        if(user.getRole() == UserLogin.Role.ADMIN)
        {
            Optional<AdminUser> adminUser = adminRepository.findById(user.getEmail());
            if (adminUser.isPresent()) {
                name = adminUser.get().getFirstName();
            }

        } else if (user.getRole() == UserLogin.Role.CASEWORKER) {
            Optional<CaseWorker> caseWorker = caseWorkerRepository.findByEmail(user.getEmail());
            if (caseWorker.isPresent()) {
                name = caseWorker.get().getFirstName();
            }
        }
        user.setFirstName(name);
        return ResponseEntity.ok(user);
    }


    public boolean lockUserAccount(String userId) {
        Optional<UserLogin> userOpt = userRepository.findById(userId.toLowerCase());
        if (userOpt.isPresent()) {
            UserLogin user = userOpt.get();
            user.setLocked(true);  // Assume there's a 'locked' field
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean unlockUserAccount(String emailId) {
        Optional<UserLogin> userOpt = userRepository.findById(emailId.toLowerCase()); // Assuming emailId is the user ID in the repository
        if (userOpt.isPresent()) {
            UserLogin user = userOpt.get();
            user.setLocked(false);  // Assume 'locked' is a boolean field in UserLogin
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public ResponseEntity<?> getAllUser() {
        //remove password
        return ResponseEntity.ok(userRepository.findAll().stream()
                .map(user -> {
                    user.setPassword(null); // Remove password from each user
                    return user;
                }).toList());

    }

    public boolean deleteUser(String emailId) {
        //set is working as false and islocaked true
        Optional<UserLogin> userOpt = userRepository.findById(emailId.toLowerCase());
        if (userOpt.isPresent()) {
            UserLogin user = userOpt.get();
            user.setWorking(false); // Assuming there's a 'working' field
            user.setLocked(true);   // Assuming 'locked' is a boolean field in UserLogin
            userRepository.save(user);
            return true;
        }
        return false;

    }
    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
    }

    public ResponseEntity<String> sendOTP(String email) {
        Optional<UserLogin> userOpt = userRepository.findById(email.toLowerCase());

        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with given email not found.");
        }

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setEmail(email);
        String otp = generateOtp();
        passwordResetToken.setOtp(otp);
        passwordResetToken.setCreatedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(passwordResetToken);

        emailService.sendEmail(email, "Password Reset OTP",
                "Your OTP for resetting password is: " + otp + "\nIt is valid for 10 minutes.");

        return ResponseEntity.ok("OTP sent to your email.");
    }

    public ResponseEntity<?> verifyOTP(String email, String otp) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findById(email.toLowerCase());

        if (tokenOpt.isPresent()) {
            PasswordResetToken token = tokenOpt.get();
            if(token.getOtp().equals(otp) && !token.isExpired()){
                return ResponseEntity.ok("OTP verified successfully.");
            }
            else {
                return ResponseEntity.badRequest().body("Invalid OTP or OTP expired.");
            }

        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No OTP found for the given email.");
        }
    }

    public ResponseEntity<?> setNewPassword(Map<String, String> payload) {
        String  email = payload.get("email");
        String password = payload.get("password");
        if(email == null || password == null){
            return ResponseEntity.badRequest().body("Invalid request.");
        } else if (password.length() < 8) {
            ResponseEntity.badRequest().body("Password must be at least 8 characters long.");
        }
        userRepository.findById(email.toLowerCase()).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        });
        return ResponseEntity.ok("Password changed successfully.");
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsById(email.toLowerCase());
    }
}


