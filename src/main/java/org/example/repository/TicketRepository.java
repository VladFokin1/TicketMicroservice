package org.example.repository;
import org.example.dto.TicketFilterDto;
import org.example.model.Ticket;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public TicketRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Ticket> findAvailableTickets(TicketFilterDto filter, int page, int size) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, r.departure_point, r.destination_point, c.name AS carrier_name " +
                        "FROM tickets t " +
                        "JOIN routes r ON t.route_id = r.id " +
                        "JOIN carriers c ON r.carrier_id = c.id " +
                        "WHERE t.user_id IS NULL"
        );

        // Добавление фильтров
        if (filter.getDateTime() != null) {
            sql.append(" AND t.date_time >= :dateTime");
            params.addValue("dateTime", filter.getDateTime());
        }
        if (filter.getDeparturePoint() != null) {
            sql.append(" AND LOWER(r.departure_point) LIKE LOWER(:departurePoint)");
            params.addValue("departurePoint", "%" + filter.getDeparturePoint() + "%");
        }
        if (filter.getDestinationPoint() != null) {
            sql.append(" AND LOWER(r.destination_point) LIKE LOWER(:destinationPoint)");
            params.addValue("destinationPoint", "%" + filter.getDestinationPoint() + "%");
        }
        if (filter.getCarrierName() != null) {
            sql.append(" AND LOWER(c.name) LIKE LOWER(:carrierName)");
            params.addValue("carrierName", "%" + filter.getCarrierName() + "%");
        }

        // Пагинация
        sql.append(" ORDER BY t.date_time ASC LIMIT :limit OFFSET :offset");
        params.addValue("limit", size);
        params.addValue("offset",  page * size);

        return namedParameterJdbcTemplate.query(sql.toString(), params, new TicketRowMapper());
    }

    public Optional<Ticket> findById(Long id) {
        try {
            String sql = "SELECT * FROM tickets WHERE id = ?";
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new TicketRowMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateUserId(Long ticketId, Long userId) {
        String sql = "UPDATE tickets SET user_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, userId, ticketId);
    }

    public List<Ticket> findByUserId(Long userId) {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";
        return jdbcTemplate.query(sql, new TicketRowMapper(), userId);
    }

    private static class TicketRowMapper implements RowMapper<Ticket> {
        @Override
        public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
            Ticket ticket = new Ticket();
            ticket.setId(rs.getInt("id"));
            ticket.setRouteId(rs.getInt("route_id"));
            ticket.setDateTime(rs.getObject("date_time", LocalDateTime.class));
            ticket.setSeatNumber(rs.getString("seat_number"));
            ticket.setPrice(rs.getBigDecimal("price"));
            ticket.setUserId(rs.getObject("user_id", Integer.class)); // может быть null
            return ticket;
        }
    }
}
