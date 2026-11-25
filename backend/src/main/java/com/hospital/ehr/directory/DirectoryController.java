package com.hospital.ehr.directory;

import com.hospital.ehr.auth.repository.UserRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only lookup lists needed by booking / ordering forms.
 *
 * Doctors and departments were previously only reachable through
 * ADMIN-only endpoints (or not at all), so a patient could not
 * choose who to book an appointment with.
 *
 * Only non-sensitive fields are exposed.
 */
@RestController
@RequestMapping("/api/v1/directory")
@SecurityRequirement(name = "bearerAuth")
public class DirectoryController {

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public DirectoryController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Active doctors: userId (used as doctorId), first and last name.
     */
    @GetMapping("/doctors")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> getDoctors() {

        List<Map<String, Object>> doctors = userRepository.findAll()
                .stream()
                .filter(user -> "ACTIVE".equalsIgnoreCase(user.getAccountStatus()))
                .filter(user -> user.getRoles()
                        .stream()
                        .anyMatch(role -> "DOCTOR".equalsIgnoreCase(role.getName())))
                .map(user -> {
                    Map<String, Object> doctor = new LinkedHashMap<>();
                    doctor.put("userId", user.getId());
                    doctor.put("firstName", user.getFirstName());
                    doctor.put("lastName", user.getLastName() != null ? user.getLastName() : "");
                    doctor.put("departmentId", user.getDepartmentId());
                    return doctor;
                })
                .toList();

        return ResponseEntity.ok(doctors);
    }

    /**
     * Active departments: id, code, name.
     */
    @GetMapping("/departments")
    @PreAuthorize("isAuthenticated()")
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<Map<String, Object>>> getDepartments() {

        List<Object[]> rows = entityManager
                .createNativeQuery(
                        "SELECT id, department_code, name "
                                + "FROM departments "
                                + "WHERE is_active = TRUE "
                                + "ORDER BY name"
                )
                .getResultList();

        List<Map<String, Object>> departments = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> department = new LinkedHashMap<>();
            department.put("id", ((Number) row[0]).longValue());
            department.put("code", row[1]);
            department.put("name", row[2]);
            departments.add(department);
        }

        return ResponseEntity.ok(departments);
    }
}
