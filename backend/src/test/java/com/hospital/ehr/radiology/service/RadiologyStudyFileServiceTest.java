package com.hospital.ehr.radiology.service;

import com.hospital.ehr.auth.entity.Role;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.radiology.dto.RadiologyOrderResponse;
import com.hospital.ehr.radiology.dto.RadiologyStudyFileResponse;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.service.PatientAccessService;
import com.hospital.ehr.radiology.entity.RadiologyOrder;
import com.hospital.ehr.radiology.entity.RadiologyStudy;
import com.hospital.ehr.radiology.entity.RadiologyStudyFile;
import com.hospital.ehr.radiology.repository.RadiologyStudyFileRepository;
import com.hospital.ehr.radiology.repository.RadiologyStudyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RadiologyStudyFileServiceTest {

    @Mock
    private RadiologyStudyRepository radiologyStudyRepository;

    @Mock
    private RadiologyStudyFileRepository radiologyStudyFileRepository;

    @Mock
    private PatientAccessService patientAccessService;

    @InjectMocks
    private RadiologyStudyFileService radiologyStudyFileService;

    @Test
    void uploadStudyFileStoresMetadataAndSavesFile() throws Exception {
        User technician = new User();
        Field userIdField = User.class.getDeclaredField("id");
        userIdField.setAccessible(true);
        userIdField.set(technician, 4L);
        technician.setEmail("tech@example.com");
        Role role = new Role();
        role.setName("LAB_TECHNICIAN");
        technician.setRoles(Set.of(role));

        Patient patient = new Patient();
        patient.setId(10L);

        RadiologyOrder order = new RadiologyOrder();
        order.setId(12L);
        order.setPatient(patient);

        RadiologyStudy study = new RadiologyStudy();
        study.setId(99L);
        study.setStudyStatus("AVAILABLE");
        study.setRadiologyOrder(order);

        when(radiologyStudyRepository.findById(99L)).thenReturn(Optional.of(study));
        when(radiologyStudyFileRepository.save(any(RadiologyStudyFile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(patientAccessService).assertCanAccess(any(), any());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "scan.png",
                "image/png",
                "pixel-data".getBytes(StandardCharsets.UTF_8)
        );

        RadiologyStudyFileResponse response = radiologyStudyFileService.uploadStudyFile(
                99L,
                file,
                technician
        );

        assertNotNull(response);
        assertEquals("scan.png", response.getOriginalFileName());
        assertEquals("image/png", response.getContentType());
        assertTrue(Files.exists(Path.of("uploads", "radiology", "99")));
        assertNotNull(response.getUploadedById());
    }

    @Test
    void radiologyOrderResponseIncludesCanonicalPatientName() throws Exception {
        User patientUser = new User();
        patientUser.setFirstName("Jamie");
        patientUser.setLastName("Doe");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUser(patientUser);

        User doctor = new User();
        Field doctorIdField = User.class.getDeclaredField("id");
        doctorIdField.setAccessible(true);
        doctorIdField.set(doctor, 7L);
        doctor.setFirstName("Dr.");
        doctor.setLastName("Smith");

        RadiologyOrder order = new RadiologyOrder();
        order.setId(21L);
        order.setPatient(patient);
        order.setOrderingDoctor(doctor);
        order.setModality("MRI");
        order.setClinicalIndication("Headache");
        order.setUrgency("ROUTINE");
        order.setStatus("ORDERED");

        RadiologyOrderResponse response = RadiologyOrderResponse.fromEntity(order);

        assertEquals("Jamie Doe", response.getPatientName());
        assertEquals("Dr. Smith", response.getOrderingDoctorName());
    }
}
