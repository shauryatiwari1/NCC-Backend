package com.shauryaORG.NoCheatCode.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Username can only contain letters, numbers and underscores")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    private String password;

    public void sanitize() {
        if (username != null) username = sanitizeInput(username);
        if (password != null) password = sanitizeInput(password);
    }

    /**
     * Sanitizes input by trimming, removing dangerous characters, and blocking common injection patterns.
     */
    private String sanitizeInput(String input) {
        String sanitized = input.trim();
        // Remove script tags and HTML tags
        sanitized = sanitized.replaceAll("(?i)<script.*?>.*?</script>", "");
        sanitized = sanitized.replaceAll("<.*?>", "");
        // Remove common SQL injection and command injection metacharacters
        sanitized = sanitized.replaceAll("['\";`|&$\\\\]", "");
        // Remove double dashes and comments
        sanitized = sanitized.replaceAll("--", "");
        sanitized = sanitized.replaceAll("/\\*.*?\\*/", "");
        // Remove common SQL keywords
        sanitized = sanitized.replaceAll("(?i)or\\s+1=1", "");
        sanitized = sanitized.replaceAll("(?i)drop\\s+table", "");
        sanitized = sanitized.replaceAll("(?i)union\\s+select", "");
        sanitized = sanitized.replaceAll("(?i)insert\\s+into", "");
        sanitized = sanitized.replaceAll("(?i)update\\s+set", "");
        sanitized = sanitized.replaceAll("(?i)delete\\s+from", "");
        // Remove command injection patterns and OS commands
        sanitized = sanitized.replaceAll("(?i)(;|&&|\\|\\||\\$\\(|`|\\n|\\r|cat |ls |rm |del |shutdown |reboot |poweroff |mkfs |dd |:\\(\\)\\{:\\|:&\\};:|wget |curl |scp |nc |ncat |python |perl |bash |sh |cmd |powershell |exec |Runtime\\.getRuntime)\\b", "");
        // Remove dangerous Unicode control characters
        sanitized = sanitized.replaceAll("[\\p{Cntrl}]", "");
        return sanitized;
    }
}

