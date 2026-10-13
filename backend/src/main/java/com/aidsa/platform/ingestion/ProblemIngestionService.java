package com.aidsa.platform.ingestion;

import com.aidsa.platform.ingestion.dto.ExternalProblemRecord;
import com.aidsa.platform.ingestion.dto.ProblemIngestionResult;
import com.aidsa.platform.model.Problem;
import com.aidsa.platform.model.TestCase;
import com.aidsa.platform.repository.ProblemRepository;
import com.aidsa.platform.repository.TestCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProblemIngestionService {

    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProblemNormalizer problemNormalizer;

    public ProblemIngestionService(
            ProblemRepository problemRepository,
            TestCaseRepository testCaseRepository,
            ProblemNormalizer problemNormalizer
    ) {
        this.problemRepository = problemRepository;
        this.testCaseRepository = testCaseRepository;
        this.problemNormalizer = problemNormalizer;
    }

    @Transactional
    public ProblemIngestionResult ingest(
            ExternalProblemRecord record
    ) {

        if (record == null) {
            throw new IllegalArgumentException(
                    "Problem record cannot be null."
            );
        }

        String source = clean(record.source());

        String sourceProblemId =
                clean(record.sourceProblemId());

        if (source.isEmpty()) {
            throw new IllegalArgumentException(
                    "Problem source is required."
            );
        }

        if (sourceProblemId.isEmpty()) {
            throw new IllegalArgumentException(
                    "Source problem ID is required."
            );
        }

        /*
         * ---------------------------------------------------------
         * DUPLICATE CHECK
         * ---------------------------------------------------------
         */

        var existing =
                problemRepository
                        .findBySourceAndSourceProblemId(
                                source,
                                sourceProblemId
                        );

        if (existing.isPresent()) {

            Problem problem = existing.get();

            return ProblemIngestionResult.duplicate(
                    problem.getId(),
                    problem.getProblemNumber()
            );
        }

        /*
         * ---------------------------------------------------------
         * NORMALIZE
         * ---------------------------------------------------------
         */

        Problem problem =
                problemNormalizer.normalize(record);

        /*
         * ---------------------------------------------------------
         * INTERNAL PROBLEM NUMBER
         * ---------------------------------------------------------
         */

        int nextProblemNumber =
                problemRepository
                        .findTopByOrderByProblemNumberDesc()
                        .map(p -> p.getProblemNumber() + 1)
                        .orElse(1);

        problem.setProblemNumber(nextProblemNumber);

        /*
         * ---------------------------------------------------------
         * SAVE PROBLEM
         * ---------------------------------------------------------
         */

        Problem savedProblem =
                problemRepository.save(problem);

        /*
         * ---------------------------------------------------------
         * SAVE TEST CASES
         * ---------------------------------------------------------
         */

        int testCasesInserted = 0;

        if (record.testCases() != null) {

            for (
                    ExternalProblemRecord.ExternalTestCase externalTestCase
                    : record.testCases()
            ) {

                if (externalTestCase == null) {
                    continue;
                }

                String input =
                        clean(externalTestCase.input());

                String expectedOutput =
                        clean(externalTestCase.expectedOutput());

                if (input.isEmpty()
                        || expectedOutput.isEmpty()) {
                    continue;
                }

                TestCase testCase =
                        new TestCase(
                                savedProblem,
                                input,
                                expectedOutput,
                                externalTestCase.hidden()
                        );

                testCaseRepository.save(testCase);

                testCasesInserted++;
            }
        }

        return ProblemIngestionResult.inserted(
                savedProblem.getId(),
                savedProblem.getProblemNumber(),
                testCasesInserted
        );
    }

    private String clean(String value) {

        return value == null
                ? ""
                : value.trim();
    }
}
