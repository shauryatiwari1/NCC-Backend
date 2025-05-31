package com.shauryaORG.NoCheatCode.util;


public class TestPostgresConnection {

    public static void main(String[] args) {
        System.out.println("Testing Supabase PostgreSQL connection...");

        String host = "aws-0-ap-south-1.pooler.supabase.com";
        String dbName = "postgres";
        String user = "postgres.tdvwzlomalxhbykbrnyq";
        String password = "oksowhyme00";
        int port = 5432;


        String[] urls = {
            // Format 1: Basic URL with separate credentials
            String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName),

            // Format 2: Credentials in URL
            String.format("jdbc:postgresql://%s:%s@%s:%d/%s",
                         user, password, host, port, dbName),

            // Format 3: Credentials as query parameters
            String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s",
                         host, port, dbName, user, password),

            // Format 4: Without SSL
            String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s&sslmode=disable",
                         host, port, dbName, user, password)
        };

        for (int i = 0; i < urls.length; i++) {
            System.out.printf("\n\nTesting connection format %d: %s\n", i+1, urls[i]);

            try {
                // Load PostgreSQL driver
                Class.forName("org.postgresql.Driver");
                System.out.println("PostgreSQL JDBC driver loaded successfully");

                // Set connection timeout to 10 seconds
                java.util.Properties props = new java.util.Properties();
                if (i == 0) {
                    // For format 1, add credentials separately
                    props.setProperty("user", user);
                    props.setProperty("password", password);
                }

                // Set timeouts
                props.setProperty("connectTimeout", "10");  // 10 seconds
                props.setProperty("socketTimeout", "10");   // 10 seconds

                System.out.println("Attempting connection...");
                long startTime = System.currentTimeMillis();

                java.sql.Connection conn = java.sql.DriverManager.getConnection(urls[i], props);
                long endTime = System.currentTimeMillis();

                System.out.println("✅ Connection successful! Time taken: " + (endTime - startTime) + "ms");
                System.out.println("Database product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database version: " + conn.getMetaData().getDatabaseProductVersion());

                conn.close();
                System.out.println("Connection closed properly");
            } catch (Exception e) {
                System.out.println("❌ Connection failed: " + e.getMessage());
                if (e.getCause() != null) {
                    System.out.println("Root cause: " + e.getCause().getMessage());
                }
                e.printStackTrace();
            }
        }
    }
}
