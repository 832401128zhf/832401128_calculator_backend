package com.example.calculator.model;

public record CalculationHistory(
        long id,
        String expression,
        String result,
        String createdAt
) {
}
