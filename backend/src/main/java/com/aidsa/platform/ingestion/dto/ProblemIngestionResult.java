package com.aidsa.platform.ingestion.dto;

public record ProblemIngestionResult(

        boolean inserted,

        boolean duplicate,

        Long problemId,

        Integer problemNumber,

        int testCasesInserted,

        String message

) {

    public static ProblemIngestionResult inserted(
            Long problemId,
            Integer problemNumber,
            int testCasesInserted
    ) {

        return new ProblemIngestionResult(
                true,
                false,
                problemId,
                problemNumber,
                testCasesInserted,
                "Problem ingested successfully."
        );
    }

    public static ProblemIngestionResult duplicate(
            Long problemId,
            Integer problemNumber
    ) {

        return new ProblemIngestionResult(
                false,
                true,
                problemId,
                problemNumber,
                0,
                "Problem already exists for this source."
        );
    }
}
