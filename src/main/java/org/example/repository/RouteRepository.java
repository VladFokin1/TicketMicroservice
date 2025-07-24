package org.example.repository;


import org.example.model.Route;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class RouteRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Route> routeRowMapper = new RouteRowMapper();

    public RouteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Route route) {
        if (route.getId() == null) {
            String sql = "INSERT INTO routes (departure_point, destination_point, carrier_id, duration_minutes) " +
                    "VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    route.getDeparturePoint(),
                    route.getDestinationPoint(),
                    route.getCarrierId(),
                    route.getDurationMinutes());
        } else {
            String sql = "UPDATE routes SET departure_point = ?, destination_point = ?, " +
                    "carrier_id = ?, duration_minutes = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    route.getDeparturePoint(),
                    route.getDestinationPoint(),
                    route.getCarrierId(),
                    route.getDurationMinutes(),
                    route.getId());
        }
    }

    public Optional<Route> findById(Long id) {
        String sql = "SELECT * FROM routes WHERE id = ?";
        try {
            Route route = jdbcTemplate.queryForObject(sql, routeRowMapper, id);
            return Optional.ofNullable(route);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Route> findAll() {
        String sql = "SELECT * FROM routes";
        return jdbcTemplate.query(sql, routeRowMapper);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM routes WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class RouteRowMapper implements RowMapper<Route> {
        @Override
        public Route mapRow(ResultSet rs, int rowNum) throws SQLException {
            Route route = new Route();
            route.setId(rs.getInt("id"));
            route.setDeparturePoint(rs.getString("departure_point"));
            route.setDestinationPoint(rs.getString("destination_point"));
            route.setCarrierId(rs.getInt("carrier_id"));
            route.setDurationMinutes(rs.getInt("duration_minutes"));
            return route;
        }
    }
}
