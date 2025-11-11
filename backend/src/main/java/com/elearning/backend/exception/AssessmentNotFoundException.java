package com.elearning.backend.exception;


public class AssessmentNotFoundException extends RuntimeException {
    public AssessmentNotFoundException(Long id) {
        super("Assessment not found: " + id);
    }
}