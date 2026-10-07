package com.example.calculator.parser;

import com.example.calculator.exception.ExpressionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionParserTest {
    private final ExpressionParser parser = new ExpressionParser();

    @Test
    void shouldRespectOperatorPrecedence() {
        assertEquals(0, parser.evaluate("1+2*3").compareTo(new BigDecimal("7")));
    }

    @Test
    void shouldSupportParentheses() {
        assertEquals(0, parser.evaluate("(1+2)*3").compareTo(new BigDecimal("9")));
    }

    @Test
    void shouldSupportUnaryMinus() {
        assertEquals(0, parser.evaluate("3*-2").compareTo(new BigDecimal("-6")));
    }

    @Test
    void shouldSupportDecimals() {
        assertEquals(0, parser.evaluate("0.1+0.2").compareTo(new BigDecimal("0.3")));
    }

    @Test
    void shouldRejectDivisionByZero() {
        assertThrows(ExpressionException.class, () -> parser.evaluate("10/0"));
    }

    @Test
    void shouldRejectInvalidExpression() {
        assertThrows(ExpressionException.class, () -> parser.evaluate("1++*2"));
    }
}
