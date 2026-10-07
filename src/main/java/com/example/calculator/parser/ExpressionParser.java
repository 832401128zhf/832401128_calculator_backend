package com.example.calculator.parser;

import com.example.calculator.exception.ExpressionException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class ExpressionParser {
    private static final MathContext MATH_CONTEXT = new MathContext(16, RoundingMode.HALF_UP);

    public BigDecimal evaluate(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new ExpressionException("表达式不能为空");
        }
        if (expression.length() > 200) {
            throw new ExpressionException("表达式过长，最多允许 200 个字符");
        }

        String normalized = expression.replace("×", "*").replace("÷", "/");
        Parser parser = new Parser(normalized);
        BigDecimal result = parser.parseExpression();
        parser.ensureFinished();
        return result.stripTrailingZeros();
    }

    private static class Parser {
        private final String input;
        private int position;

        private Parser(String input) {
            this.input = input;
        }

        private BigDecimal parseExpression() {
            BigDecimal value = parseTerm();
            while (true) {
                skipWhitespace();
                if (match('+')) {
                    value = value.add(parseTerm(), MATH_CONTEXT);
                } else if (match('-')) {
                    value = value.subtract(parseTerm(), MATH_CONTEXT);
                } else {
                    return value;
                }
            }
        }

        private BigDecimal parseTerm() {
            BigDecimal value = parseUnary();
            while (true) {
                skipWhitespace();
                if (match('*')) {
                    value = value.multiply(parseUnary(), MATH_CONTEXT);
                } else if (match('/')) {
                    BigDecimal divisor = parseUnary();
                    if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                        throw new ExpressionException("除数不能为 0");
                    }
                    value = value.divide(divisor, MATH_CONTEXT);
                } else {
                    return value;
                }
            }
        }

        private BigDecimal parseUnary() {
            skipWhitespace();
            if (match('+')) {
                return parseUnary();
            }
            if (match('-')) {
                return parseUnary().negate(MATH_CONTEXT);
            }
            return parsePrimary();
        }

        private BigDecimal parsePrimary() {
            skipWhitespace();
            if (match('(')) {
                BigDecimal value = parseExpression();
                skipWhitespace();
                if (!match(')')) {
                    throw new ExpressionException("括号不匹配：缺少右括号 )");
                }
                return value;
            }
            return parseNumber();
        }

        private BigDecimal parseNumber() {
            skipWhitespace();
            int start = position;
            boolean hasDigit = false;
            boolean hasDot = false;

            while (position < input.length()) {
                char current = input.charAt(position);
                if (Character.isDigit(current)) {
                    hasDigit = true;
                    position++;
                } else if (current == '.' && !hasDot) {
                    hasDot = true;
                    position++;
                } else {
                    break;
                }
            }

            if (!hasDigit) {
                if (position >= input.length()) {
                    throw new ExpressionException("表达式不完整");
                }
                throw new ExpressionException("数字格式错误，位置：" + (position + 1));
            }

            String numberText = input.substring(start, position);
            try {
                return new BigDecimal(numberText, MATH_CONTEXT);
            } catch (NumberFormatException exception) {
                throw new ExpressionException("非法数字：" + numberText);
            }
        }

        private void ensureFinished() {
            skipWhitespace();
            if (position != input.length()) {
                throw new ExpressionException("表达式中存在无法识别的字符：" + input.charAt(position));
            }
        }

        private void skipWhitespace() {
            while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
                position++;
            }
        }

        private boolean match(char expected) {
            if (position < input.length() && input.charAt(position) == expected) {
                position++;
                return true;
            }
            return false;
        }
    }
}
