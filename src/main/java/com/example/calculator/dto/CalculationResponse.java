package com.example.calculator.dto;

public record CalculationResponse(
        long id,
        String expression,
        String result,
        String createdAt
) {
}
