package com.shauryaORG.NoCheatCode.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionListResponseDto {
    private List<SubmissionDto> submissions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmissionDto {
        private Long id;
        private Long problemId;
        private String problemTitle;
        private String code;
        private boolean solved;
        private LocalDateTime submittedAt;
        private String codePatterns;
    }
}

