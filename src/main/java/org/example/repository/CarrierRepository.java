package org.example.repository;


import org.example.model.Carrier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CarrierRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Carrier> carrierRowMapper = new CarrierRowMapper();

    public CarrierRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Carrier carrier) {
        if (carrier.getId() == null) {
            String sql = "INSERT INTO carriers (name, phone) VALUES (?, ?)";
            jdbcTemplate.update(sql, carrier.getName(), carrier.getPhone());
        } else {
            String sql = "UPDATE carriers SET name = ?, phone = ? WHERE id = ?";
            jdbcTemplate.update(sql, carrier.getName(), carrier.getPhone(), carrier.getId());
        }
    }

    public Optional<Carrier> findById(Long id) {
        String sql = "SELECT * FROM carriers WHERE id = ?";
        try {
            Carrier carrier = jdbcTemplate.queryForObject(sql, carrierRowMapper, id);
            return Optional.ofNullable(carrier);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Carrier> findAll() {
        String sql = "SELECT * FROM carriers";
        return jdbcTemplate.query(sql, carrierRowMapper);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM carriers WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class CarrierRowMapper implements RowMapper<Carrier> {
        @Override
        public Carrier mapRow(ResultSet rs, int rowNum) throws SQLException {
            Carrier carrier = new Carrier();
            carrier.setId(rs.getInt("id"));
            carrier.setName(rs.getString("name"));
            carrier.setPhone(rs.getString("phone"));
            return carrier;
        }
    }
}
