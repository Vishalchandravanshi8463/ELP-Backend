package com.elearning.backend.exception;

public class InvalidAssessmentException extends RuntimeException {
    public InvalidAssessmentException(String message) {
        super(message);
    }
}