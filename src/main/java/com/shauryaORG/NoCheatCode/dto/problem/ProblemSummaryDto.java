package com.shauryaORG.NoCheatCode.dto.problem;

import com.shauryaORG.NoCheatCode.model.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSummaryDto {
    private Long id;
    private String title;
    private String slug;
    private Difficulty difficulty;
    private List<String> tags;
}