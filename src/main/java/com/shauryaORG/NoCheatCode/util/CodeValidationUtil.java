package com.shauryaORG.NoCheatCode.util;

import java.util.regex.Pattern;

public class CodeValidationUtil {
    private static final int MAX_LINES = 100;
    private static final int MAX_CHARS = 5000;

    // Suspicious patterns
    private static final Pattern[] SUSPICIOUS_PATTERNS = new Pattern[] {
        Pattern.compile("while\\s*\\(\\s*true\\s*\\)", Pattern.CASE_INSENSITIVE), // infinite loop
        Pattern.compile("for\\s*\\(.*;\\s*;.*\\)", Pattern.CASE_INSENSITIVE), // for(;;)
        Pattern.compile("fs\\.", Pattern.CASE_INSENSITIVE), // Node.js fs
        Pattern.compile("open\\s*\\(", Pattern.CASE_INSENSITIVE), // open(
        Pattern.compile("fetch\\s*\\(", Pattern.CASE_INSENSITIVE), // fetch(
        Pattern.compile("curl\\s*", Pattern.CASE_INSENSITIVE), // curl
        Pattern.compile("os\\.system", Pattern.CASE_INSENSITIVE), // os.system
        Pattern.compile("exec\\s*\\(", Pattern.CASE_INSENSITIVE), // exec(
        Pattern.compile("child_process", Pattern.CASE_INSENSITIVE), // child_process
        Pattern.compile("require\\s*\\(\\s*['\"]child_process['\"]\\s*\\)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("subprocess", Pattern.CASE_INSENSITIVE), // subprocess
        Pattern.compile("ProcessBuilder", Pattern.CASE_INSENSITIVE), // Java ProcessBuilder
        Pattern.compile("Runtime\\.getRuntime\\(\\)\\.exec", Pattern.CASE_INSENSITIVE) // Java exec
    };

    public static boolean isCodeTooLong(String code) {
        if (code == null) return true;
        int lines = code.split("\\r?\\n").length;
        return lines > MAX_LINES || code.length() > MAX_CHARS;
    }

    public static String findSuspiciousPattern(String code) {
        if (code == null) return null;
        for (Pattern pattern : SUSPICIOUS_PATTERNS) {
            if (pattern.matcher(code).find()) {
                return pattern.pattern();
            }
        }
        return null;
    }
}


