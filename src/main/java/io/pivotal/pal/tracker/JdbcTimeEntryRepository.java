package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Objects;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<TimeEntry> timeEntryRowMapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong(1),
            rs.getLong(2),
            rs.getLong(3),
            rs.getDate(4).toLocalDate(),
            Long.valueOf(rs.getLong(5)).intValue());

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntryToCreate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(
                            "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                                    "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setLong(1, timeEntryToCreate.getProjectId());
                    preparedStatement.setLong(2, timeEntryToCreate.getUserId());
                    preparedStatement.setDate(3, Date.valueOf(timeEntryToCreate.getDate()));
                    preparedStatement.setLong(4, timeEntryToCreate.getHours());
                    return preparedStatement;
                }, generatedKeyHolder);
        long id = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
        timeEntryToCreate.setId(id);
        return timeEntryToCreate;
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                    new Object[]{timeEntryId},
                    timeEntryRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT * FROM time_entries", timeEntryRowMapper);
    }

    @Override
    public TimeEntry update(long timeEntryID, TimeEntry timeEntry) {
        int updateCount = jdbcTemplate.update(
                "UPDATE time_entries " +
                        "SET project_id = ?, user_id = ?, date = ?, hours = ? " +
                        "WHERE id = ?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                timeEntry.getDate(),
                timeEntry.getHours(),
                timeEntryID);
        if (updateCount != 1) {
            return null;
        }
        timeEntry.setId(timeEntryID);
        return timeEntry;
    }

    @Override
    public void delete(long timeEntryId) {
        jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", timeEntryId);
    }
}
