package com.example.calculator.repository;

import com.example.calculator.model.CalculationHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CalculationHistoryRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CalculationHistory> rowMapper = (resultSet, rowNum) ->
            new CalculationHistory(
                    resultSet.getLong("id"),
                    resultSet.getString("expression"),
                    resultSet.getString("result"),
                    resultSet.getString("created_at")
            );

    public CalculationHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(String expression, String result, String createdAt) {
        String sql = "INSERT INTO calculation_history(expression, result, created_at) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, expression);
            statement.setString(2, result);
            statement.setString(3, createdAt);
            return statement;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("保存历史记录失败：未获得主键");
        }
        return key.longValue();
    }

    public List<CalculationHistory> findAll() {
        return jdbcTemplate.query(
                "SELECT id, expression, result, created_at FROM calculation_history ORDER BY id DESC",
                rowMapper
        );
    }

    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM calculation_history WHERE id = ?", id);
    }

    public int deleteAll() {
        return jdbcTemplate.update("DELETE FROM calculation_history");
    }
}
