package com.elearning.backend.controller;

import com.elearning.backend.dto.NotificationDTO;
import com.elearning.backend.service.NotificationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationDTO> list(@RequestParam Long userId) {
        return service.listForUser(userId);
    }

    @GetMapping("/unread-count")
    public Long unreadCount(@RequestParam Long userId) {
        return service.unreadCount(userId);
    }

    @PostMapping
    public NotificationDTO create(@RequestBody NotificationDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}/read")
    public NotificationDTO markRead(@PathVariable Long id) {
        return service.markRead(id);
    }
}
