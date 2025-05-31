package com.shauryaORG.NoCheatCode.util;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Standalone test to verify database connectivity
 */
public class DatabaseConnectionTest {

    @Test
    public void testDirectPostgresConnection() {
        // Test different URL formats
        String host = "aws-0-ap-south-1.pooler.supabase.com";
        String db = "postgres";
        String user = "postgres.tdvwzlomalxhbykbrnyq";
        String password = "oksowhyme00";
        int port = 5432;

        // Format 1 - JDBC standard format
        String url1 = String.format("jdbc:postgresql://%s:%d/%s", host, port, db);

        // Format 2 - With credentials in URL
        String url2 = String.format("jdbc:postgresql://%s:%s@%s:%d/%s",
                                    user, password, host, port, db);

        // Format 3 - With credentials as query parameters
        String url3 = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s",
                                    host, port, db, user, password);

        // Format 4 - With SSL disabled
        String url4 = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s&sslmode=disable",
                                    host, port, db, user, password);

        // Try each connection format
        System.out.println("Testing connection with format 1: " + url1);
        testConnection(url1, user, password);

        System.out.println("\nTesting connection with format 2: " + url2);
        testConnection(url2, null, null);

        System.out.println("\nTesting connection with format 3: " + url3);
        testConnection(url3, null, null);

        System.out.println("\nTesting connection with format 4: " + url4);
        testConnection(url4, null, null);
    }

    private void testConnection(String url, String user, String password) {
        long startTime = System.currentTimeMillis();
        try {
            Connection conn;
            if (user != null && password != null) {
                conn = DriverManager.getConnection(url, user, password);
            } else {
                conn = DriverManager.getConnection(url);
            }

            System.out.println("✅ Connection successful!");
            System.out.println("Database product name: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Database product version: " + conn.getMetaData().getDatabaseProductVersion());
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null) {
                System.out.println("Root cause: " + cause.getMessage());
            }
            e.printStackTrace();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Connection attempt took " + duration + "ms");
        }
    }
}
