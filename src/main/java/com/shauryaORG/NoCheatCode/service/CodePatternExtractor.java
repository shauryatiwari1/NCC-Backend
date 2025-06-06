package com.shauryaORG.NoCheatCode.service;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CodePatternExtractor {

    // Common programming patterns and structures
    private static final Map<String, Pattern> CODE_PATTERNS = Map.of(
            "loops", Pattern.compile("\\b(for|while|do)\\s*\\(", Pattern.CASE_INSENSITIVE),
            "conditionals", Pattern.compile("\\b(if|else|switch|case)\\b", Pattern.CASE_INSENSITIVE),
            "functions", Pattern.compile("\\b(def|function|public|private|static)\\s+\\w+\\s*\\(", Pattern.CASE_INSENSITIVE),
            "data_structures", Pattern.compile("\\b(array|list|map|set|stack|queue|hashmap|arraylist|vector|dictionary)\\b", Pattern.CASE_INSENSITIVE),
            "sorting", Pattern.compile("\\b(sort|sorted|collections\\.sort|arrays\\.sort|merge|quick|bubble|heap)\\b", Pattern.CASE_INSENSITIVE),
            "recursion", Pattern.compile("\\breturn\\s+\\w+\\s*\\([^)]*\\w+[^)]*\\)", Pattern.CASE_INSENSITIVE),
            "string_operations", Pattern.compile("\\b(substring|split|join|replace|trim|length|concat|indexof|charat)\\b", Pattern.CASE_INSENSITIVE),
            "math_operations", Pattern.compile("\\b(math\\.|abs|max|min|pow|sqrt|floor|ceil|random)\\b", Pattern.CASE_INSENSITIVE)
    );

    // Algorithm-specific keywords
    private static final Set<String> ALGORITHM_KEYWORDS = Set.of(
            "binary_search", "linear_search", "dfs", "bfs", "dynamic_programming", "dp",
            "greedy", "backtrack", "divide_conquer", "two_pointers", "sliding_window",
            "graph", "tree", "heap", "priority_queue", "dijkstra", "floyd", "kmp"
    );

    // Data structure usage patterns
    private static final Map<String, Pattern> DS_USAGE_PATTERNS = Map.of(
            "array_access", Pattern.compile("\\w+\\[\\d*\\w*\\]", Pattern.CASE_INSENSITIVE),
            "object_method", Pattern.compile("\\w+\\.\\w+\\(", Pattern.CASE_INSENSITIVE),
            "nested_structure", Pattern.compile("\\[\\s*\\[|\\{\\s*\\{", Pattern.CASE_INSENSITIVE)
    );

    public String extractCodePatterns(String code, String language) {
        if (code == null || code.trim().isEmpty()) {
            return "";
        }

        Map<String, Object> patterns = new HashMap<>();

        // Extract basic code patterns
        Map<String, Integer> codePatternCounts = new HashMap<>();
        for (Map.Entry<String, Pattern> entry : CODE_PATTERNS.entrySet()) {
            Matcher matcher = entry.getValue().matcher(code);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            if (count > 0) {
                codePatternCounts.put(entry.getKey(), count);
            }
        }
        patterns.put("code_patterns", codePatternCounts);

        // Extract data structure usage
        Map<String, Integer> dsUsage = new HashMap<>();
        for (Map.Entry<String, Pattern> entry : DS_USAGE_PATTERNS.entrySet()) {
            Matcher matcher = entry.getValue().matcher(code);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            if (count > 0) {
                dsUsage.put(entry.getKey(), count);
            }
        }
        patterns.put("ds_usage", dsUsage);

        // Extract algorithm keywords
        Set<String> foundAlgorithms = new HashSet<>();
        String codeUpper = code.toUpperCase();
        for (String keyword : ALGORITHM_KEYWORDS) {
            if (codeUpper.contains(keyword.toUpperCase())) {
                foundAlgorithms.add(keyword);
            }
        }
        patterns.put("algorithms", foundAlgorithms);

        // Extract variable naming patterns
        patterns.put("variable_patterns", extractVariablePatterns(code));

        // Extract code complexity indicators
        patterns.put("complexity_indicators", extractComplexityIndicators(code));

        // Language-specific patterns
        patterns.put("language_specific", extractLanguageSpecificPatterns(code, language));

        return convertPatternsToString(patterns);
    }

    private Map<String, Object> extractVariablePatterns(String code) {
        Map<String, Object> varPatterns = new HashMap<>();

        // Common variable naming conventions
        Pattern variablePattern = Pattern.compile("\\b[a-zA-Z_]\\w*\\b");
        Matcher matcher = variablePattern.matcher(code);

        Set<String> commonNames = new HashSet<>();
        while (matcher.find()) {
            String var = matcher.group().toLowerCase();
            if (isCommonVariableName(var)) {
                commonNames.add(var);
            }
        }

        varPatterns.put("common_names", commonNames);
        return varPatterns;
    }

    private boolean isCommonVariableName(String name) {
        Set<String> common = Set.of("i", "j", "k", "n", "m", "len", "size", "count", "sum",
                "max", "min", "left", "right", "start", "end", "index",
                "temp", "result", "answer", "target", "current", "prev", "next");
        return common.contains(name);
    }

    private Map<String, Object> extractComplexityIndicators(String code) {
        Map<String, Object> complexity = new HashMap<>();

        // Count nested structures (rough time complexity indicator)
        int nestedLoops = countNestedPatterns(code, "\\b(for|while)\\s*\\(");
        int nestedConditions = countNestedPatterns(code, "\\bif\\s*\\(");

        complexity.put("nested_loops", nestedLoops);
        complexity.put("nested_conditions", nestedConditions);
        complexity.put("total_lines", code.split("\n").length);

        return complexity;
    }

    private int countNestedPatterns(String code, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        String[] lines = code.split("\n");
        int maxNesting = 0;
        int currentNesting = 0;

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                currentNesting++;
                maxNesting = Math.max(maxNesting, currentNesting);
            }
            if (line.contains("}")) {
                currentNesting = Math.max(0, currentNesting - 1);
            }
        }

        return maxNesting;
    }

    private Map<String, Object> extractLanguageSpecificPatterns(String code, String language) {
        Map<String, Object> langPatterns = new HashMap<>();

        switch (language.toLowerCase()) {
            case "java":
                langPatterns.put("uses_streams", code.contains(".stream()"));
                langPatterns.put("uses_collections", code.contains("Collections."));
                langPatterns.put("uses_arrays_util", code.contains("Arrays."));
                break;
            case "python":
                langPatterns.put("uses_list_comprehension", code.contains("[") && code.contains("for") && code.contains("in"));
                langPatterns.put("uses_lambda", code.contains("lambda"));
                langPatterns.put("uses_builtin_functions",
                        Pattern.compile("\\b(map|filter|reduce|zip|enumerate)\\b").matcher(code).find());
                break;
            case "javascript":
                langPatterns.put("uses_arrow_functions", code.contains("=>"));
                langPatterns.put("uses_array_methods",
                        Pattern.compile("\\.(map|filter|reduce|forEach|find)\\(").matcher(code).find());
                break;
            case "cpp":
                langPatterns.put("uses_stl",
                        Pattern.compile("\\b(vector|map|set|stack|queue|priority_queue)\\b").matcher(code).find());
                langPatterns.put("uses_algorithms", code.contains("#include <algorithm>"));
                break;
        }

        return langPatterns;
    }

    private String convertPatternsToString(Map<String, Object> patterns) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : patterns.entrySet()) {
            // Skip empty maps/sets and nulls
            Object value = entry.getValue();
            boolean isEmpty = false;
            if (value instanceof Map) isEmpty = ((Map<?, ?>) value).isEmpty();
            if (value instanceof Set) isEmpty = ((Set<?>) value).isEmpty();
            if (value == null || isEmpty) continue;

            sb.append(entry.getKey()).append(": ");
            if (value instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) value;
                sb.append(map.entrySet().stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .collect(Collectors.joining(", ")));
            } else if (value instanceof Set) {
                Set<?> set = (Set<?>) value;
                sb.append(set.stream().map(Object::toString).collect(Collectors.joining(", ")));
            } else {
                sb.append(value);
            }
            sb.append("; ");
        }
        // If nothing was appended, return a clear message
        if (sb.length() == 0) return "No code patterns detected.";
        return sb.toString().trim();
    }
}

