package com.hospital.ehr.security;

import com.hospital.ehr.auth.entity.Role;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleAuthorityMappingTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void customUserDetailsServiceMapsLabTechnicianToRoleAuthority() {
        User user = new User();
        user.setEmail("lab@hospital.local");
        user.setPasswordHash("hashed-password");
        user.setAccountStatus("ACTIVE");

        Role role = new Role();
        role.setName("LAB_TECHNICIAN");
        user.setRoles(Set.of(role));

        when(userRepository.findByEmail("lab@hospital.local"))
                .thenReturn(Optional.of(user));

        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

        UserDetails details = service.loadUserByUsername("lab@hospital.local");

        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_LAB_TECHNICIAN".equals(authority.getAuthority())));
        assertTrue(details.getAuthorities().stream()
                .noneMatch(authority -> "LAB_TECHNICIAN".equals(authority.getAuthority())));
    }

    @Test
    void jwtAuthenticationFilterNormalizesLabTechnicianRoleToRoleAuthority() throws Exception {
        String token = "mock-jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("lab@hospital.local");
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractRoles(token)).thenReturn(List.of("LAB_TECHNICIAN"));

        UserDetailsService realUserDetailsService = username ->
                org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("pass")
                        .authorities("ROLE_LAB_TECHNICIAN")
                        .build();

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, realUserDetailsService);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(authentication != null);
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_LAB_TECHNICIAN".equals(authority.getAuthority())));
        assertEquals("lab@hospital.local", authentication.getName());
        verify(filterChain).doFilter(request, response);
    }
}
