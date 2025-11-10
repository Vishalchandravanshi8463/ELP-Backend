
package com.elearning.backend.controller;

import com.elearning.backend.dto.*;
import com.elearning.backend.service.AssessmentAttemptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessment-attempts")
public class AssessmentAttemptController {

    private final AssessmentAttemptService service;

    public AssessmentAttemptController(AssessmentAttemptService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentAttemptResponse submit(@Valid @RequestBody AssessmentAttemptRequest request,
                                            Authentication auth) {
        return service.submitAttempt(request, auth);
    }

    @GetMapping
    public List<StudentAttemptSummaryDto> myAttempts(Authentication auth) {
        return service.getMyAttempts(auth);
    }

    @GetMapping("/by-assessment/{assessmentId}")
    public List<AssessmentAttemptSummaryDto> byAssessment(@PathVariable Long assessmentId) {
        return service.getAttemptsByAssessment(assessmentId);
    }
}
