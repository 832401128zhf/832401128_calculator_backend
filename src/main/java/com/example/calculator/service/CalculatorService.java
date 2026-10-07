package com.example.calculator.service;

import com.example.calculator.dto.CalculationResponse;
import com.example.calculator.exception.ExpressionException;
import com.example.calculator.exception.ResourceNotFoundException;
import com.example.calculator.model.CalculationHistory;
import com.example.calculator.parser.ExpressionParser;
import com.example.calculator.repository.CalculationHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class CalculatorService {
    private final ExpressionParser expressionParser;
    private final CalculationHistoryRepository historyRepository;

    public CalculatorService(ExpressionParser expressionParser,
                             CalculationHistoryRepository historyRepository) {
        this.expressionParser = expressionParser;
        this.historyRepository = historyRepository;
    }

    public CalculationResponse calculate(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new ExpressionException("表达式不能为空");
        }

        BigDecimal value = expressionParser.evaluate(expression);
        String result = format(value);
        String createdAt = Instant.now().toString();
        String normalizedExpression = expression.trim();
        long id = historyRepository.save(normalizedExpression, result, createdAt);

        return new CalculationResponse(id, normalizedExpression, result, createdAt);
    }

    public List<CalculationResponse> getHistory() {
        return historyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteHistory(long id) {
        int affectedRows = historyRepository.deleteById(id);
        if (affectedRows == 0) {
            throw new ResourceNotFoundException("历史记录不存在，id=" + id);
        }
    }

    public void clearHistory() {
        historyRepository.deleteAll();
    }

    private CalculationResponse toResponse(CalculationHistory history) {
        return new CalculationResponse(
                history.id(),
                history.expression(),
                history.result(),
                history.createdAt()
        );
    }

    private String format(BigDecimal value) {
        BigDecimal normalized = value.stripTrailingZeros();
        if (normalized.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return normalized.toPlainString();
    }
}
