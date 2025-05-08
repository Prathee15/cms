package com.civi.cms.controller;

import com.civi.cms.model.AdminUser;
import com.civi.cms.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/v1")
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;

    // CREATE Admin User
    @PostMapping("/create")
    public ResponseEntity<?> createAdminUser(@RequestBody AdminUser user) {
        return adminUserService.createAdminUser(user);
    }

    // GET all Admin Users
    @GetMapping("/all")
    public ResponseEntity<List<AdminUser>> getAllAdminUsers() {
        List<AdminUser> users = adminUserService.getAllAdminUsers();
        return ResponseEntity.ok(users);
    }

    // GET Admin User by Email
    @GetMapping("/fetch/{email}")
    public ResponseEntity<?> getAdminUserByEmail(@PathVariable String email) {
        return adminUserService.getAdminUserById(email);
    }

    // UPDATE Admin User
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAdminUser(@PathVariable String email, @RequestBody AdminUser updatedUser) {
        return adminUserService.AdminUser(email, updatedUser);
    }
}
