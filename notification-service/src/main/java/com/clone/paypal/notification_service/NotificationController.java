package com.clone.paypal.notification_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/user")
    public ResponseEntity<?> getNotificationsByUserId(@RequestParam Long id) {
        try {
            List<Notification> notifications = notificationRepository.findByUserIdOrderByTimestampDesc(id);
            return ResponseEntity.ok(Map.of("notifications", notifications));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch notifications"));
        }
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<?> getUnreadNotificationCount(@PathVariable Long userId) {
        try {
            long unreadCount = notificationRepository.countByUserIdAndReadIsFalse(userId);
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch unread notification count"));
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long id) {
        try {
            Optional<Notification> notificationOptional = notificationRepository.findById(id);
            if (notificationOptional.isPresent()) {
                Notification notification = notificationOptional.get();
                notification.setRead(true);
                notificationRepository.save(notification);
                return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to mark notification as read"));
        }
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationRepository.findByUserIdOrderByTimestampDesc(userId);
            notifications.forEach(notification -> notification.setRead(true));
            notificationRepository.saveAll(notifications);
            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to mark all notifications as read"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            if (notificationRepository.existsById(id)) {
                notificationRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete notification"));
        }
    }

    @DeleteMapping("/user/{userId}")
    @Transactional
    public ResponseEntity<Void> deleteNotificationsByUserId(@PathVariable Long userId) {
        notificationRepository.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }
}