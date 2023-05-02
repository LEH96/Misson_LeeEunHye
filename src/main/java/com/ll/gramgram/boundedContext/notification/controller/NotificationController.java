package com.ll.gramgram.boundedContext.notification.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usr/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final Rq rq;
    private final NotificationService notificationService;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showList(Model model) {
        if (!rq.getMember().hasConnectedInstaMember()) {
            return rq.redirectWithMsg("/usr/instaMember/connect", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
        }

        List<Notification> notifications = notificationService.findByToInstaMember(rq.getMember().getInstaMember())
                                            .stream()
                                            .filter(notification -> notification.getReadDate() == null)
                                            .sorted(Comparator.comparing(Notification::getId).reversed()).collect(Collectors.toList());
        model.addAttribute("notifications", notifications);

        return "usr/notification/list";
    }

    @PostMapping("/read/{id}")
    @PreAuthorize("isAuthenticated()")
    public String read(@PathVariable Long id, Model model) {
        notificationService.readNotification(id);
        return "redirect:/usr/notification/list";
    }
}
