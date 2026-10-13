package com.aidsa.platform.ingestion.dto;

import java.util.List;
import java.util.Map;

public record ExternalProblemRecord(

        String source,

        String sourceProblemId,

        String sourceUrl,

        String title,

        String difficulty,

        String category,

        List<String> tags,

        String description,

        String constraints,

        List<Map<String, Object>> examples,

        List<ExternalTestCase> testCases

) {

    public record ExternalTestCase(

            String input,

            String expectedOutput,

            boolean hidden

    ) {
    }
}
