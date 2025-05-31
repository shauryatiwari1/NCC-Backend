package com.shauryaORG.NoCheatCode.dto.submission;

import lombok.Data;

@Data
public class SubmissionRequestDto {
    private Long problemId;
    private String code;
    private String language;
}

