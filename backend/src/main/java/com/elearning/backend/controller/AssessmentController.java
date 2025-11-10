package com.elearning.backend.controller;

import com.elearning.backend.dto.AssessmentDto;
import com.elearning.backend.dto.ScoreResponse;
import com.elearning.backend.repository.AssessmentRepository;
import com.elearning.backend.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessments")
@Tag(name="Assessment APIs", description = "POST, GET, PUT, DELETE Assessments")
public class AssessmentController {

    private final AssessmentService service;


    public AssessmentController(AssessmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AssessmentDto> getAll() {
        return service.findAll();
    }

    @Operation(description = "GETs Assessments by ID")
    @GetMapping("/{id}")
    public AssessmentDto getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentDto create(@Valid @RequestBody AssessmentDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public AssessmentDto update(@PathVariable Long id, @Valid @RequestBody AssessmentDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


    @PostMapping("/{id}/evaluate")
    public ScoreResponse evaluate(@PathVariable Long id, @RequestBody Map<Integer, Integer> answers) {
        return service.evaluateAnswers(id, answers);
    }

}
