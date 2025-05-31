package com.shauryaORG.NoCheatCode;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class TestEnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        setSystemProperty(dotenv, "JDBC_DATABASE_URL");
        setSystemProperty(dotenv, "JDBC_DATABASE_USERNAME");
        setSystemProperty(dotenv, "JDBC_DATABASE_PASSWORD");
        setSystemProperty(dotenv, "JWT_SECRET");
        setSystemProperty(dotenv, "AI_GEMINI_API_KEY");
        setSystemProperty(dotenv, "JUDGE0_API_KEY");
    }

    private void setSystemProperty(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value != null) {
            System.setProperty(key, value);
        }
    }
}
