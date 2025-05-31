package com.shauryaORG.NoCheatCode.controller;

import com.shauryaORG.NoCheatCode.dto.problem.ProblemDetailDto;
import com.shauryaORG.NoCheatCode.dto.problem.ProblemSummaryDto;
import com.shauryaORG.NoCheatCode.model.enums.Difficulty;
import com.shauryaORG.NoCheatCode.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseEntity<Page<ProblemSummaryDto>> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(problemService.getAllProblems(pageable));
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<Page<ProblemSummaryDto>> getProblemsByDifficulty(
            @PathVariable Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(problemService.getProblemsByDifficulty(difficulty, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDetailDto> getProblemById(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getProblemById(id));
    }
}