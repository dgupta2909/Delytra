package com.aidsa.platform.controller;

import com.aidsa.platform.ingestion.ProblemIngestionService;
import com.aidsa.platform.ingestion.dto.ExternalProblemRecord;
import com.aidsa.platform.ingestion.dto.ProblemIngestionResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingestion/problems")
public class ProblemIngestionController {

    private final ProblemIngestionService problemIngestionService;

    public ProblemIngestionController(
            ProblemIngestionService problemIngestionService) {

        this.problemIngestionService = problemIngestionService;
    }

    @PostMapping
    public ResponseEntity<ProblemIngestionResult> ingestProblem(
            @RequestBody ExternalProblemRecord record) {

        ProblemIngestionResult result =
                problemIngestionService.ingest(record);

        if (result.duplicate()) {
            return ResponseEntity.ok(result);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }
}