package com.hospital.ehr.admin.service;

import com.hospital.ehr.admin.dto.CreateStaffRequest;
import com.hospital.ehr.audit.service.AuditLogService;
import com.hospital.ehr.auth.entity.Role;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.RoleRepository;
import com.hospital.ehr.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class StaffManagementService {
    private static final Set<String> STAFF_ROLES = Set.of(
            "DOCTOR", "NURSE", "LAB_TECHNICIAN", "PHARMACIST",
            "RADIOLOGY_TECHNICIAN");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public StaffManagementService(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder,
                                   AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public User createStaff(CreateStaffRequest request, Long adminId) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String roleName = request.getRole().trim().toUpperCase(Locale.ROOT);
        if (!STAFF_ROLES.contains(roleName)) {
            throw new IllegalArgumentException("Role must be DOCTOR, NURSE, LAB_TECHNICIAN, PHARMACIST, or RADIOLOGY_TECHNICIAN.");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        Role role = roleRepository.findByName(roleName).orElseThrow(() ->
                new IllegalStateException("Role is not configured: " + roleName));
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getInitialPassword()));
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName() == null ? "" : request.getLastName().trim());
        user.setPhone(request.getPhone());
        user.setDepartmentId(request.getDepartmentId());
        user.setEmailVerified(true);
        user.setAccountStatus("ACTIVE");
        user.setPasswordChangedAt(LocalDateTime.now());
        user.getRoles().add(role);
        User saved = userRepository.save(user);
        auditLogService.log(adminId, null, "STAFF_CREATED", "USER", String.valueOf(saved.getId()),
                "Created staff account with role " + roleName, null, null);
        auditLogService.log(adminId, null, "ROLE_ASSIGNED", "USER", String.valueOf(saved.getId()),
                "Assigned role " + roleName, null, null);
        return saved;
    }

    @Transactional
    public User updateStatus(Long userId, String status, Long adminId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + userId));
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!Set.of("ACTIVE", "SUSPENDED", "DEACTIVATED").contains(normalized)) {
            throw new IllegalArgumentException("Status must be ACTIVE, SUSPENDED, or DEACTIVATED.");
        }
        user.setAccountStatus(normalized);
        User saved = userRepository.save(user);
        auditLogService.log(adminId, null, "ACCOUNT_STATUS_CHANGED", "USER", String.valueOf(userId),
                "Changed account status to " + normalized, null, null);
        return saved;
    }

    public List<User> getStaff() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> STAFF_ROLES.contains(role.getName())))
                .toList();
    }
}
