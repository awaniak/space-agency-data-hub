package com.example.spaceagencydatahub.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationResult {
    private boolean isError;
    private String message;
}
