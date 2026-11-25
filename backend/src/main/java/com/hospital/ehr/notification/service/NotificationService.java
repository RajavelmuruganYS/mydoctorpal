package com.hospital.ehr.notification.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.notification.dto.CreateNotificationRequest;
import com.hospital.ehr.notification.dto.NotificationResponse;
import com.hospital.ehr.notification.entity.Notification;
import com.hospital.ehr.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(request.getType().toUpperCase());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);

        return new NotificationResponse(savedNotification);
    }

    @Transactional
    public List<NotificationResponse> getMyNotifications(String email) {

        User user = getUserByEmail(email);

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    @Transactional
    public List<NotificationResponse> getMyUnreadNotifications(String email) {

        User user = getUserByEmail(email);

        return notificationRepository
                .findByUserAndReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }

    @Transactional
    public long getUnreadCount(String email) {

        User user = getUserByEmail(email);

        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId, String email) {

        User user = getUserByEmail(email);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notification not found with ID: " + notificationId));

        // Important: users can only modify their own notifications.
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new SecurityException(
                    "You are not authorized to modify this notification");
        }

        notification.setRead(true);

        Notification updatedNotification =
                notificationRepository.save(notification);

        return new NotificationResponse(updatedNotification);
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"));
    }
}