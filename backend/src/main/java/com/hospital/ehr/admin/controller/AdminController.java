package com.hospital.ehr.admin.controller;

import com.hospital.ehr.auth.entity.Role;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.admin.dto.CreateStaffRequest;
import com.hospital.ehr.admin.service.StaffManagementService;
import com.hospital.ehr.patient.entity.PatientDoctorAssignment;
import com.hospital.ehr.patient.service.PatientDoctorAssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
        private final StaffManagementService staffManagementService;
        private final PatientDoctorAssignmentService assignmentService;

        public AdminController(UserRepository userRepository,
                                                   StaffManagementService staffManagementService,
                                                   PatientDoctorAssignmentService assignmentService) {
        this.userRepository = userRepository;
                this.staffManagementService = staffManagementService;
                this.assignmentService = assignmentService;
    }

    /**
     * Get all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {

        List<Map<String, Object>> users = userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    /**
     * Get a user by ID.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with ID: " + userId));

        return ResponseEntity.ok(toUserResponse(user));
    }

    /**
     * Get users by role.
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<Map<String, Object>>> getUsersByRole(
            @PathVariable String role) {

        String requestedRole = role.toUpperCase();

        List<Map<String, Object>> users = userRepository.findAll()
                .stream()
                .filter(user -> user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .anyMatch(userRole ->
                                userRole.equalsIgnoreCase(requestedRole)))
                .map(this::toUserResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/staff")
    public ResponseEntity<List<Map<String, Object>>> getStaff() {
        return ResponseEntity.ok(staffManagementService.getStaff().stream()
                .map(this::toUserResponse).toList());
    }

    @PostMapping("/staff")
    public ResponseEntity<Map<String, Object>> createStaff(
            @Valid @RequestBody CreateStaffRequest request,
            Authentication authentication) {
        User admin = currentUser(authentication);
        return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "message", "Staff account created successfully.",
                "user", toUserResponse(staffManagementService.createStaff(request, admin.getId()))));
    }

    @PatchMapping("/staff/{userId}/status")
    public ResponseEntity<Map<String, Object>> updateStaffStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        User admin = currentUser(authentication);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "user", toUserResponse(staffManagementService.updateStatus(
                        userId, request.getOrDefault("status", ""), admin.getId()))));
    }

    @GetMapping("/patients/{patientId}/doctors")
    public ResponseEntity<List<Map<String, Object>>> getAssignedDoctors(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(assignmentService.getForPatient(patientId).stream()
                .map(this::toAssignmentResponse).toList());
    }

    @PostMapping("/patients/{patientId}/doctors/{doctorId}")
    public ResponseEntity<Map<String, Object>> assignDoctor(
            @PathVariable Long patientId,
            @PathVariable Long doctorId,
            Authentication authentication) {
        User admin = currentUser(authentication);
        return ResponseEntity.ok(Map.of("success", true, "assignment",
                toAssignmentResponse(assignmentService.assign(patientId, doctorId, admin.getId()))));
    }

    @DeleteMapping("/patients/{patientId}/doctors/{doctorId}")
    public ResponseEntity<Map<String, Object>> revokeDoctor(
            @PathVariable Long patientId,
            @PathVariable Long doctorId,
            Authentication authentication) {
        assignmentService.revoke(patientId, doctorId, currentUser(authentication).getId());
        return ResponseEntity.ok(Map.of("success", true, "message", "Doctor assignment revoked."));
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow(() ->
                new IllegalArgumentException("Authenticated user not found."));
    }

    private Map<String, Object> toAssignmentResponse(PatientDoctorAssignment assignment) {
        return Map.of(
                "id", assignment.getId(),
                "patientId", assignment.getPatient().getId(),
                "doctorId", assignment.getDoctor().getId(),
                "doctorName", assignment.getDoctor().getFirstName() + " " + assignment.getDoctor().getLastName(),
                "status", assignment.getStatus(),
                "assignedAt", assignment.getAssignedAt());
    }

    private Map<String, Object> toUserResponse(User user) {

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "phone", user.getPhone() != null
                        ? user.getPhone()
                        : "",
                "emailVerified", user.isEmailVerified(),
                "accountStatus", user.getAccountStatus(),
                "roles", roles
        );
    }
}