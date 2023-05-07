package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> findByToInstaMember(InstaMember toInstaMember) {
        return notificationRepository.findByToInstaMember(toInstaMember);
    }
  
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    public void readNotification(Long id) {
        Optional<Notification> OptNoti = notificationRepository.findById(id);
        if(OptNoti.isPresent()) {
            Notification notification = OptNoti.get();
            notification.setReadDate(LocalDateTime.now());
            save(notification);
        }
    }
}
