package com.test.margo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
@Slf4j
public class MargoConfiguration {

    @Value("${hs.datasource.driver-class-name}")
    private String className;

    @Value("${hs.datasource.url}")
    private String url;

    @Value("${hs.datasource.username}")
    private String username;

    @Value("${hs.datasource.password}")
    private String password;


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Connection connection() {
        try {
            Class.forName(className);
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS EVENT (ID VARCHAR(50), DURATION INTEGER, TYPE VARCHAR(50), HOST VARCHAR(50), ALERT BOOLEAN)");
            return connection;
        } catch (Exception e) {
            log.error("Failure during on JDBCDriver initialization");
            throw new BeanCreationException("Connection", "Failure during creating connection", e);
        }
    }
}
