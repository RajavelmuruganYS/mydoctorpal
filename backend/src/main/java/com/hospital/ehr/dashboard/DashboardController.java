package com.hospital.ehr.dashboard;

import com.hospital.ehr.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final AnalyticsService analyticsService;

    public DashboardController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Map<String, Object>> patientDashboard() {

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "role", "PATIENT",
                        "dashboard", "PATIENT_DASHBOARD"
                )
        );
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Map<String, Object>> doctorDashboard() {

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "role", "DOCTOR",
                        "dashboard", "DOCTOR_DASHBOARD"
                )
        );
    }

    @GetMapping("/nurse")
    @PreAuthorize("hasAuthority('ROLE_NURSE')")
    public ResponseEntity<Map<String, Object>> nurseDashboard() {

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "role", "NURSE",
                        "dashboard", "NURSE_DASHBOARD"
                )
        );
    }

    @GetMapping("/lab")
    @PreAuthorize("hasAuthority('ROLE_LAB_TECHNICIAN')")
    public ResponseEntity<Map<String, Object>> labDashboard() {

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "role", "LAB_TECHNICIAN",
                        "dashboard", "LAB_DASHBOARD"
                )
        );
    }

    @GetMapping("/pharmacy")
    @PreAuthorize("hasAuthority('ROLE_PHARMACIST')")
    public ResponseEntity<Map<String, Object>> pharmacyDashboard() {

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "role", "PHARMACIST",
                        "dashboard", "PHARMACY_DASHBOARD"
                )
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> adminDashboard() {

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "role", "ADMIN",
                        "dashboard", "ADMIN_DASHBOARD",
                        "statistics", analyticsService.getAdminDashboardStatistics()
                )
        );
    }
}