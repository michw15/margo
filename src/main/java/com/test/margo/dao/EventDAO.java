package com.test.margo.dao;

import com.test.margo.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
@Slf4j
public class EventDAO implements AutoCloseable {

    private final Connection connection;
    private final static String sql = "INSERT INTO EVENT (ID, DURATION, TYPE, HOST, ALERT)  VALUES (?, ?, ?, ?, ?)";

    public EventDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(Event event) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, event.getId());
            statement.setLong(2, event.getDuration());
            statement.setString(3, event.getType());
            statement.setString(4, event.getHost());
            statement.setBoolean(5, event.isAlert());
            statement.executeUpdate();
        } catch (Exception e) {
            log.error("Failure saving event",  e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Failure closing database connection",  e);
        }
    }
}
