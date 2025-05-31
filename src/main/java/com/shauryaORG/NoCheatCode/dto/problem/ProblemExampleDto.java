package com.shauryaORG.NoCheatCode.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemExampleDto {
    private String input;
    private String output;
    private String explanation;
}