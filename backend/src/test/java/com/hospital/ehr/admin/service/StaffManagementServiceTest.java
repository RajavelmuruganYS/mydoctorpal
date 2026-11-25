package com.hospital.ehr.admin.service;

import com.hospital.ehr.admin.dto.CreateStaffRequest;
import com.hospital.ehr.audit.service.AuditLogService;
import com.hospital.ehr.auth.entity.Role;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.RoleRepository;
import com.hospital.ehr.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffManagementServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuditLogService auditLogService;

    @Test
    void createsActiveStaffWithEncodedPasswordAndRole() {
        Role role = new Role();
        role.setName("DOCTOR");
        when(userRepository.existsByEmailIgnoreCase("new.doctor@hospital.local")).thenReturn(false);
        when(roleRepository.findByName("DOCTOR")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("Initial@123")).thenReturn("$2b$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateStaffRequest request = new CreateStaffRequest();
        request.setFirstName("New");
        request.setLastName("Doctor");
        request.setEmail("new.doctor@hospital.local");
        request.setRole("doctor");
        request.setInitialPassword("Initial@123");

        User created = new StaffManagementService(
                userRepository, roleRepository, passwordEncoder, auditLogService)
                .createStaff(request, 1L);

        assertEquals("ACTIVE", created.getAccountStatus());
        assertTrue(created.isEmailVerified());
        assertEquals("$2b$encoded", created.getPasswordHash());
        assertTrue(created.getRoles().contains(role));
        verify(passwordEncoder).encode("Initial@123");
        verify(auditLogService, times(2)).log(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void acceptsRadiologyTechnicianRole() {
        CreateStaffRequest request = new CreateStaffRequest();
        request.setFirstName("Mary");
        request.setLastName("Jones");
        request.setEmail("tech@hospital.local");
        request.setPhone("555-1212");
        request.setRole("RADIOLOGY_TECHNICIAN");
        request.setDepartmentId(10L);
        request.setInitialPassword("Initial@123");

        Role role = new Role();
        role.setName("RADIOLOGY_TECHNICIAN");

        when(userRepository.existsByEmailIgnoreCase("tech@hospital.local")).thenReturn(false);
        when(roleRepository.findByName("RADIOLOGY_TECHNICIAN")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("Initial@123")).thenReturn("$2b$encoded-tech");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = new StaffManagementService(
                userRepository, roleRepository, passwordEncoder, auditLogService)
                .createStaff(request, 1L);

        assertEquals("RADIOLOGY_TECHNICIAN", created.getRoles().iterator().next().getName());
    }
}
