package com.hospital.ehr.radiology.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.service.PatientAccessService;
import com.hospital.ehr.radiology.dto.RadiologyStudyFileResponse;
import com.hospital.ehr.radiology.entity.RadiologyStudy;
import com.hospital.ehr.radiology.entity.RadiologyStudyFile;
import com.hospital.ehr.radiology.repository.RadiologyStudyFileRepository;
import com.hospital.ehr.radiology.repository.RadiologyStudyRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class RadiologyStudyFileService {

    private final RadiologyStudyRepository radiologyStudyRepository;
    private final RadiologyStudyFileRepository radiologyStudyFileRepository;
    private final UserRepository userRepository;
    private final PatientAccessService patientAccessService;

    public RadiologyStudyFileService(
            RadiologyStudyRepository radiologyStudyRepository,
            RadiologyStudyFileRepository radiologyStudyFileRepository,
            UserRepository userRepository,
            PatientAccessService patientAccessService
    ) {
        this.radiologyStudyRepository = radiologyStudyRepository;
        this.radiologyStudyFileRepository = radiologyStudyFileRepository;
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    @Transactional
    public RadiologyStudyFileResponse uploadStudyFile(
            Long studyId,
            MultipartFile file,
            User authenticatedUser
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required.");
        }

        RadiologyStudy study = radiologyStudyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("Radiology study not found"));

        validateStudyAccess(study, authenticatedUser);

        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Unsupported file type. Upload JPG, PNG, PDF, or text files.");
        }

        String safeName = sanitizeFilename(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + "_" + safeName;
        Path destinationDir = Path.of("uploads", "radiology", String.valueOf(studyId));

        try {
            Files.createDirectories(destinationDir);
            Path destination = destinationDir.resolve(storedName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            RadiologyStudyFile entity = new RadiologyStudyFile();
            entity.setStudy(study);
            entity.setOriginalFileName(safeName);
            entity.setStoredFileName(storedName);
            entity.setContentType(contentType);
            entity.setSizeBytes(file.getSize());
            entity.setStoragePath(destination.toAbsolutePath().toString());
            entity.setUploadedBy(authenticatedUser);

            RadiologyStudyFile saved = radiologyStudyFileRepository.save(entity);
            return RadiologyStudyFileResponse.fromEntity(saved);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to store radiology file.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<RadiologyStudyFileResponse> getStudyFiles(Long studyId, User authenticatedUser) {
        RadiologyStudy study = radiologyStudyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("Radiology study not found"));

        validateStudyAccess(study, authenticatedUser);

        return radiologyStudyFileRepository.findByStudyOrderByUploadedAtDesc(study)
                .stream()
                .map(RadiologyStudyFileResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RadiologyStudyFileResponse getStudyFile(Long studyId, Long fileId, User authenticatedUser) {
        RadiologyStudy study = radiologyStudyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("Radiology study not found"));

        validateStudyAccess(study, authenticatedUser);

        RadiologyStudyFile file = radiologyStudyFileRepository.findByStudyAndId(study, fileId)
                .orElseThrow(() -> new RuntimeException("Radiology study file not found"));

        return RadiologyStudyFileResponse.fromEntity(file);
    }

    @Transactional(readOnly = true)
    public Resource getFileResource(Long studyId, Long fileId, User authenticatedUser) {
        RadiologyStudy study = radiologyStudyRepository.findById(studyId)
                .orElseThrow(() -> new RuntimeException("Radiology study not found"));

        validateStudyAccess(study, authenticatedUser);

        RadiologyStudyFile file = radiologyStudyFileRepository.findByStudyAndId(study, fileId)
                .orElseThrow(() -> new RuntimeException("Radiology study file not found"));

        Path path = Path.of(file.getStoragePath());
        if (!Files.exists(path)) {
            throw new IllegalStateException("Stored radiology file no longer exists on disk.");
        }

        return new FileSystemResource(path);
    }

    public User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private void validateStudyAccess(RadiologyStudy study, User authenticatedUser) {
        patientAccessService.assertCanAccess(study.getRadiologyOrder().getPatient(), authenticatedUser);
    }

    private boolean isAllowedContentType(String contentType) {
        String normalized = contentType.toLowerCase(Locale.ROOT);
        return normalized.startsWith("image/")
                || normalized.equals("application/pdf")
                || normalized.equals("text/plain")
                || normalized.equals("application/octet-stream");
    }

    private String sanitizeFilename(String originalFilename) {
        String name = originalFilename == null || originalFilename.isBlank()
                ? "study-file"
                : originalFilename.trim();

        name = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        if (name.length() > 255) {
            name = name.substring(0, 255);
        }
        return name;
    }
}
