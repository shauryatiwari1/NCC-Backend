package com.shauryaORG.NoCheatCode;
import io.github.cdimascio.dotenv.Dotenv;
import com.shauryaORG.NoCheatCode.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

@SpringBootTest
@ContextConfiguration(initializers = TestEnvInitializer.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
class NoCheatCodeApplicationTests {

	@Test
	void contextLoads() {
	}

}
