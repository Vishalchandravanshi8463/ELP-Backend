package com.elearning.backend.controller;

import com.elearning.backend.dto.*;
import com.elearning.backend.service.AssessmentAttemptService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessment-attempts")
@Slf4j
public class AssessmentAttemptController {

    private final AssessmentAttemptService service;

    @Autowired
    public AssessmentAttemptController(AssessmentAttemptService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentAttemptResponse submit(@Valid @RequestBody AssessmentAttemptRequest request,
                                            Authentication auth) {
        log.info("Submitting assessment attempt for user: {}, request: {}",
                auth != null ? auth.getName() : "anonymous", request);
        return service.submitAttempt(request, auth);
    }

    @GetMapping
    public List<StudentAttemptSummaryDto> myAttempts(Authentication auth) {
        log.info("Fetching attempts for user: {}", auth != null ? auth.getName() : "anonymous");
        return service.getMyAttempts(auth);
    }

    @GetMapping("/by-assessment/{assessmentId}")
    public List<AssessmentAttemptSummaryDto> byAssessment(@PathVariable Long assessmentId) {
        log.info("Fetching attempts for assessmentId: {}", assessmentId);
        return service.getAttemptsByAssessment(assessmentId);
    }
}