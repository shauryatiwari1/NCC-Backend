package com.shauryaORG.NoCheatCode.service;

import com.shauryaORG.NoCheatCode.dto.problem.ProblemDetailDto;
import com.shauryaORG.NoCheatCode.dto.problem.ProblemExampleDto;
import com.shauryaORG.NoCheatCode.dto.problem.ProblemSummaryDto;
import com.shauryaORG.NoCheatCode.model.Problem;
import com.shauryaORG.NoCheatCode.model.enums.Difficulty;
import com.shauryaORG.NoCheatCode.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;

    @Override
    public Page<ProblemSummaryDto> getAllProblems(Pageable pageable) {
        return problemRepository.findAll(pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public Page<ProblemSummaryDto> getProblemsByDifficulty(Difficulty difficulty, Pageable pageable) {
        return problemRepository.findByDifficulty(difficulty, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    public ProblemDetailDto getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        return mapToDetailDto(problem);
    }

    private ProblemSummaryDto mapToSummaryDto(Problem problem) {
        return ProblemSummaryDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .slug(problem.getSlug())
                .difficulty(problem.getDifficulty())
                .tags(problem.getTags())
                .build();
    }

    private ProblemDetailDto mapToDetailDto(Problem problem) {
        List<ProblemExampleDto> exampleDtos = new ArrayList<>();
        if (problem.getExamples() != null) {
            for (String exampleStr : problem.getExamples()) {
                // Expecting format: input|output|explanation
                String[] parts = exampleStr.split("\\|", 3);
                String input = parts.length > 0 ? parts[0] : "";
                String output = parts.length > 1 ? parts[1] : "";
                String explanation = parts.length > 2 ? parts[2] : "";
                ProblemExampleDto exampleDto = new ProblemExampleDto(input, output, explanation);
                exampleDtos.add(exampleDto);
            }
        }

        return ProblemDetailDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .slug(problem.getSlug())
                .description(problem.getDescription())
                .difficulty(problem.getDifficulty())
                .inputDescription(problem.getInputDescription())
                .outputDescription(problem.getOutputDescription())
                .examples(exampleDtos)
                .tags(problem.getTags())
                .testCases(problem.getTestCases())
                .build();
    }
}
