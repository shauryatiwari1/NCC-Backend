package com.shauryaORG.NoCheatCode;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoCheatCodeApplication {

	public static void main(String[] args) {
		// Loading env here so that it can be used in the application
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		for (String key : new String[]{"JDBC_DATABASE_URL", "JDBC_DATABASE_USERNAME", "JDBC_DATABASE_PASSWORD", "JWT_SECRET", "AI_GEMINI_API_KEY", "JUDGE0_API_KEY"}) {
			String value = dotenv.get(key);
			if (value != null) System.setProperty(key, value);
		}
		SpringApplication.run(NoCheatCodeApplication.class, args);
	}

}


