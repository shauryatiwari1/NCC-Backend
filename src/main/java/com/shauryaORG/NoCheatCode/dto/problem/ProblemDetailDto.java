package com.shauryaORG.NoCheatCode.dto.problem;

import com.shauryaORG.NoCheatCode.model.enums.Difficulty;
import com.shauryaORG.NoCheatCode.model.ProblemTestCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDetailDto {
    private Long id;
    private String title;
    private String slug;
    private Difficulty difficulty;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private List<ProblemExampleDto> examples;
    private List<String> tags;
    private List<ProblemTestCase> testCases;
}

