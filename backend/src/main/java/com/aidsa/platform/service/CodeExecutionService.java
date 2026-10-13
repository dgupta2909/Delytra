package com.aidsa.platform.service;

import com.aidsa.platform.dto.CodeRunRequest;
import com.aidsa.platform.dto.CodeRunResponse;
import com.aidsa.platform.model.TestCase;
import com.aidsa.platform.repository.TestCaseRepository;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@Service
public class CodeExecutionService {

    private static final long COMPILATION_TIMEOUT_SECONDS = 30;
    private static final long EXECUTION_TIMEOUT_SECONDS = 5;

    private final JavaDriverService javaDriverService;
    private final TestCaseRepository testCaseRepository;

    public CodeExecutionService(
            JavaDriverService javaDriverService,
            TestCaseRepository testCaseRepository
    ) {
        this.javaDriverService = javaDriverService;
        this.testCaseRepository = testCaseRepository;
    }

    /*
     * RUN CODE
     *
     * Executes the public test cases of the selected problem.
     */
    public CodeRunResponse runCode(CodeRunRequest request) {

        if (request == null || request.getProblemId() == null) {

            return new CodeRunResponse(
                    false,
                    "",
                    "Problem ID is required."
            );
        }

        List<TestCase> publicTestCases =
                testCaseRepository.findByProblemIdAndHidden(
                        request.getProblemId(),
                        false
                );

        if (publicTestCases.isEmpty()) {

            return new CodeRunResponse(
                    false,
                    "",
                    "No public test cases are available for this problem."
            );
        }

        StringBuilder combinedOutput =
                new StringBuilder();

        for (TestCase testCase : publicTestCases) {

            CodeExecutionResult result =
                    execute(
                            request,
                            testCase.getInputData()
                    );

            if (!result.isSuccess()) {

                return new CodeRunResponse(
                        false,
                        result.getOutput(),
                        result.getError()
                );
            }

            combinedOutput
                    .append(result.getOutput())
                    .append(System.lineSeparator());
        }

        return new CodeRunResponse(
                true,
                combinedOutput.toString(),
                ""
        );
    }

    /*
     * DIRECT EXECUTION WITH CUSTOM INPUT
     *
     * Kept for internal use and existing functionality.
     */
    public CodeRunResponse runCode(
            CodeRunRequest request,
            String input
    ) {

        CodeExecutionResult result =
                execute(request, input);

        return new CodeRunResponse(
                result.isSuccess(),
                result.getOutput(),
                result.getError()
        );
    }

    /*
     * SUBMISSION EXECUTION
     *
     * SubmissionService uses this method with each test case.
     * Hidden test cases remain protected.
     */
    public CodeExecutionResult runCodeForSubmission(
            CodeRunRequest request,
            String input
    ) {

        return execute(request, input);
    }

    private CodeExecutionResult execute(
            CodeRunRequest request,
            String input
    ) {

        if (request == null ||
                request.getCode() == null ||
                request.getCode().trim().isEmpty()) {

            return new CodeExecutionResult(
                    false,
                    "",
                    "Code cannot be empty.",
                    false,
                    false
            );
        }

        String language = request.getLanguage();

        if (language == null ||
                language.trim().isEmpty()) {

            return new CodeExecutionResult(
                    false,
                    "",
                    "Programming language is required.",
                    false,
                    false
            );
        }

        Path tempDirectory = null;

        try {

            tempDirectory =
                    Files.createTempDirectory(
                            "delytra-code-"
                    );

            String normalizedLanguage =
                    language.trim().toLowerCase();

            List<String> compileCommand = null;
            List<String> runCommand;

            switch (normalizedLanguage) {

                case "java":

                    if (request.getProblemId() == null) {

                        return new CodeExecutionResult(
                                false,
                                "",
                                "Problem ID is required for Java DSA execution.",
                                false,
                                false
                        );
                    }

                    String generatedSource =
                            javaDriverService.buildSource(
                                    request.getProblemId(),
                                    request.getCode()
                            );

                    Path javaFile =
                            tempDirectory.resolve("Main.java");

                    Files.writeString(
                            javaFile,
                            generatedSource,
                            StandardCharsets.UTF_8
                    );

                    compileCommand = List.of(
                            "javac",
                            "Main.java"
                    );

                    runCommand = List.of(
                            "java",
                            "Main"
                    );

                    break;

                case "python":

                    Path pythonFile =
                            tempDirectory.resolve("solution.py");

                    Files.writeString(
                            pythonFile,
                            request.getCode(),
                            StandardCharsets.UTF_8
                    );

                    runCommand = List.of(
                            "python",
                            "solution.py"
                    );

                    break;

                case "javascript":

                    Path javascriptFile =
                            tempDirectory.resolve("solution.js");

                    Files.writeString(
                            javascriptFile,
                            request.getCode(),
                            StandardCharsets.UTF_8
                    );

                    runCommand = List.of(
                            "node",
                            "solution.js"
                    );

                    break;

                case "cpp":

                    Path cppFile =
                            tempDirectory.resolve("solution.cpp");

                    Files.writeString(
                            cppFile,
                            request.getCode(),
                            StandardCharsets.UTF_8
                    );

                    compileCommand = List.of(
                            "g++",
                            "solution.cpp",
                            "-o",
                            "solution.exe"
                    );

                    runCommand = List.of(
                            tempDirectory
                                    .resolve("solution.exe")
                                    .toString()
                    );

                    break;

                default:

                    return new CodeExecutionResult(
                            false,
                            "",
                            "Unsupported language: " + language,
                            false,
                            false
                    );
            }

            /*
             * COMPILE
             */

            if (compileCommand != null) {

                ProcessResult compileResult =
                        executeProcess(
                                compileCommand,
                                tempDirectory,
                                "",
                                COMPILATION_TIMEOUT_SECONDS
                        );

                if (compileResult.timedOut) {

                    return new CodeExecutionResult(
                            false,
                            compileResult.output,
                            "Compilation timed out. Maximum allowed time is "
                                    + COMPILATION_TIMEOUT_SECONDS
                                    + " seconds.",
                            true,
                            true
                    );
                }

                if (compileResult.exitCode != 0) {

                    return new CodeExecutionResult(
                            false,
                            compileResult.output,
                            compileResult.error,
                            true,
                            false
                    );
                }
            }

            /*
             * RUN
             */

            ProcessResult runResult =
                    executeProcess(
                            runCommand,
                            tempDirectory,
                            input,
                            EXECUTION_TIMEOUT_SECONDS
                    );

            if (runResult.timedOut) {

                return new CodeExecutionResult(
                        false,
                        runResult.output,
                        "Execution timed out. Maximum allowed time is "
                                + EXECUTION_TIMEOUT_SECONDS
                                + " seconds.",
                        false,
                        true
                );
            }

            if (runResult.exitCode != 0) {

                return new CodeExecutionResult(
                        false,
                        runResult.output,
                        runResult.error,
                        false,
                        false
                );
            }

            return new CodeExecutionResult(
                    true,
                    runResult.output,
                    "",
                    false,
                    false
            );

        } catch (IllegalArgumentException e) {

            return new CodeExecutionResult(
                    false,
                    "",
                    e.getMessage(),
                    false,
                    false
            );

        } catch (Exception e) {

            return new CodeExecutionResult(
                    false,
                    "",
                    "Execution error: " + e.getMessage(),
                    false,
                    false
            );

        } finally {

            if (tempDirectory != null) {
                deleteDirectory(tempDirectory);
            }
        }
    }

    private ProcessResult executeProcess(
            List<String> command,
            Path workingDirectory,
            String input,
            long timeoutSeconds
    ) throws IOException, InterruptedException {

        ProcessBuilder processBuilder =
                new ProcessBuilder(command);

        processBuilder.directory(
                workingDirectory.toFile()
        );

        processBuilder.redirectErrorStream(false);

        Process process =
                processBuilder.start();

        if (input != null && !input.isEmpty()) {

            try (OutputStream outputStream =
                         process.getOutputStream()) {

                outputStream.write(
                        input.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

                outputStream.flush();
            }

        } else {

            process.getOutputStream().close();
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        Future<String> outputFuture =
                executor.submit(() ->
                        readStream(
                                process.getInputStream()
                        )
                );

        Future<String> errorFuture =
                executor.submit(() ->
                        readStream(
                                process.getErrorStream()
                        )
                );

        boolean finished =
                process.waitFor(
                        timeoutSeconds,
                        TimeUnit.SECONDS
                );

        if (!finished) {

            process.destroyForcibly();

            executor.shutdownNow();

            return new ProcessResult(
                    -1,
                    "",
                    "Process exceeded execution time limit.",
                    true
            );
        }

        String output;
        String error;

        try {

            output =
                    outputFuture.get(
                            1,
                            TimeUnit.SECONDS
                    );

        } catch (Exception e) {

            output = "";
        }

        try {

            error =
                    errorFuture.get(
                            1,
                            TimeUnit.SECONDS
                    );

        } catch (Exception e) {

            error = "";
        }

        executor.shutdown();

        return new ProcessResult(
                process.exitValue(),
                output,
                error,
                false
        );
    }

    private String readStream(
            InputStream inputStream
    ) throws IOException {

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     inputStream,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            StringBuilder result =
                    new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {

                result.append(line)
                        .append(System.lineSeparator());
            }

            return result.toString();
        }
    }

    private void deleteDirectory(
            Path directory
    ) {

        try {

            if (!Files.exists(directory)) {
                return;
            }

            Files.walk(directory)
                    .sorted(
                            Comparator.reverseOrder()
                    )
                    .forEach(path -> {

                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }

                    });

        } catch (IOException ignored) {
        }
    }

    private static class ProcessResult {

        private final int exitCode;
        private final String output;
        private final String error;
        private final boolean timedOut;

        ProcessResult(
                int exitCode,
                String output,
                String error,
                boolean timedOut
        ) {
            this.exitCode = exitCode;
            this.output = output;
            this.error = error;
            this.timedOut = timedOut;
        }
    }
}