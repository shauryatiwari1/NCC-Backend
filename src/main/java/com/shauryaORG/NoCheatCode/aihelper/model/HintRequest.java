package com.shauryaORG.NoCheatCode.aihelper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HintRequest {
    private String code;
    private String problemTitle;
    private String problemDescription;
    private Integer hintLevel; // 1, 2, 3, or null for analyze
    private String userMessage; // For chat functionality
    private Boolean isAnalyzeRequest; // To differentiate between hint and analyze requests
    private Long problemId;
}
