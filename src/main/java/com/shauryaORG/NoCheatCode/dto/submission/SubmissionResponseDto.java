package com.shauryaORG.NoCheatCode.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResponseDto {
    private String status;
    private String message;
    private List<Map<String, Object>> testResults;
}

