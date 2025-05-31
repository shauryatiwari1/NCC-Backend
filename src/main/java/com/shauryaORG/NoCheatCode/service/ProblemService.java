package com.shauryaORG.NoCheatCode.service;

import com.shauryaORG.NoCheatCode.dto.problem.ProblemDetailDto;
import com.shauryaORG.NoCheatCode.dto.problem.ProblemSummaryDto;
import com.shauryaORG.NoCheatCode.model.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemService {
    Page<ProblemSummaryDto> getAllProblems(Pageable pageable);
    Page<ProblemSummaryDto> getProblemsByDifficulty(Difficulty difficulty, Pageable pageable);
    ProblemDetailDto getProblemById(Long id);
}