package com.shauryaORG.NoCheatCode.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DbConnectionTester {

    private static final Logger logger = LoggerFactory.getLogger(DbConnectionTester.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    @Profile("!test")
    public CommandLineRunner testDbConnection() {
        return args -> {
            logger.info("Testing database connection with URL: {}", dbUrl);
            logger.info("Username: {}", dbUsername);
            logger.info("Password length: {}", dbPassword == null ? "null" : dbPassword.length());


            String actualUrl = dbUrl;
            String actualUsername = dbUsername;
            String actualPassword = dbPassword;

            if (dbUrl.contains("${")) {
                String envVar = dbUrl.substring(dbUrl.indexOf("${") + 2, dbUrl.indexOf("}"));
                String defaultVal = null;
                if (envVar.contains(":")) {
                    String[] parts = envVar.split(":");
                    envVar = parts[0];
                    defaultVal = parts[1];
                }
                String envValue = System.getenv(envVar);
                if (envValue != null) {
                    actualUrl = envValue;
                } else if (defaultVal != null) {
                    actualUrl = defaultVal;
                }
                logger.info("Resolved URL: {}", actualUrl);
            }

            try {
                logger.info("Attempting to connect to database...");

                Connection conn = DriverManager.getConnection(actualUrl, actualUsername, actualPassword);
                logger.info("Database connection successful!");
                conn.close();
            } catch (SQLException e) {
                logger.error("Database connection failed!", e);

                if (!actualUrl.contains("user=")) {
                    String urlWithCreds = actualUrl.contains("?") ?
                        actualUrl + "&user=" + actualUsername + "&password=" + actualPassword :
                        actualUrl + "?user=" + actualUsername + "&password=" + actualPassword;
                    logger.info("Trying alternative URL format: {}", urlWithCreds);
                    try {
                        Connection conn = DriverManager.getConnection(urlWithCreds);
                        logger.info("Connection successful with credentials in URL!");
                        conn.close();
                    } catch (SQLException e2) {
                        logger.error("Alternative connection attempt failed", e2);

                        String urlWithoutSsl = urlWithCreds + "&sslmode=disable";
                        logger.info("Trying without SSL: {}", urlWithoutSsl);
                        try {
                            Connection conn = DriverManager.getConnection(urlWithoutSsl);
                            logger.info("Connection successful without SSL!");
                            conn.close();
                        } catch (SQLException e3) {
                            logger.error("Connection without SSL failed", e3);
                        }
                    }
                }
            }
        };
    }
}
