package com.tecnocampus.examsimulation.api.application;

import com.tecnocampus.examsimulation.api.domain.Kingdom;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class KingdomRepository {
    private final JdbcTemplate jdbcTemplate;

    public KingdomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Kingdom save(Kingdom kingdom) {
        if (kingdom.getId() == null) {
            String sql = "INSERT INTO kingdoms (id, gold, citizens, food, date_of_creation) VALUES (?, ?, ?, ?, ?)";
            String id = java.util.UUID.randomUUID().toString();
            jdbcTemplate.update(sql, id, kingdom.getGold(), kingdom.getCitizens(), kingdom.getFood(), kingdom.getDateOfCreation());
            kingdom.setId(id);
        } else {
            String sql = "UPDATE kingdoms SET gold = ?, citizens = ?, food = ? WHERE id = ?";
            jdbcTemplate.update(sql, kingdom.getGold(), kingdom.getCitizens(), kingdom.getFood(), kingdom.getId());
        }
        return kingdom;
    }

    public Optional<Kingdom> findById(String id) {
        String sql = "SELECT * FROM kingdoms WHERE id = ?";
        return jdbcTemplate.query(sql, new KingdomRowMapper(), id).stream().findFirst();
    }

    public List<Kingdom> findAll() {
        String sql = "SELECT * FROM kingdoms";
        return jdbcTemplate.query(sql, new KingdomRowMapper());
    }

    public void delete(String id) {
        String sql = "DELETE FROM kingdoms WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Kingdom> findRichest() {
        String sql = "SELECT * FROM kingdoms ORDER BY gold DESC LIMIT 1";
        return jdbcTemplate.query(sql, new KingdomRowMapper()).stream().findFirst();
    }

    private static class KingdomRowMapper implements RowMapper<Kingdom> {
        @Override
        public Kingdom mapRow(ResultSet rs, int rowNum) throws SQLException {
            Kingdom kingdom = new Kingdom();
            kingdom.setId(rs.getString("id"));
            kingdom.setGold(rs.getInt("gold"));
            kingdom.setCitizens(rs.getInt("citizens"));
            kingdom.setFood(rs.getInt("food"));
            kingdom.setDateOfCreation(rs.getDate("date_of_creation").toLocalDate());
            return kingdom;
        }
    }
}