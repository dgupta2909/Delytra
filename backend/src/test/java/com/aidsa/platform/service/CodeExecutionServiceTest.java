package com.aidsa.platform.service;

import com.aidsa.platform.dto.CodeRunRequest;
import com.aidsa.platform.dto.CodeRunResponse;
import com.aidsa.platform.model.TestCase;
import com.aidsa.platform.repository.TestCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeExecutionServiceTest {

    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private TestCase testCase;

    private CodeExecutionService service;

    @BeforeEach
    void setUp() {

        service =
                new CodeExecutionService(
                        new JavaDriverService(),
                        testCaseRepository
                );
    }

    /*
     * RUN CODE USING PUBLIC TEST CASE
     */

    @Test
    void javaCodeExecutesUsingPublicTestCase() {

        String code = """
                class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        return new int[]{0, 1};
                    }
                }
                """;

        when(testCaseRepository.findByProblemIdAndHidden(
                1L,
                false
        )).thenReturn(List.of(testCase));

        when(testCase.getInputData())
                .thenReturn(
                        "4\n2 7 11 15\n9\n"
                );

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                1L,
                                "java",
                                code
                        )
                );

        assertTrue(
                response.isSuccess(),
                "Java public test execution failed: "
                        + response.getError()
        );

        assertEquals(
                "0 1"
                        + System.lineSeparator()
                        + System.lineSeparator(),
                response.getOutput()
        );

        assertTrue(
                response.getError().isEmpty()
        );
    }

    /*
     * JAVA
     */

    @Test
    void javaCodeExecutesSuccessfully() {

        String code = """
                class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        return new int[]{0, 1};
                    }
                }
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                1L,
                                "java",
                                code
                        ),
                        "4\n2 7 11 15\n9\n"
                );

        assertTrue(
                response.isSuccess(),
                "Java execution failed: "
                        + response.getError()
        );

        assertEquals(
                "0 1"
                        + System.lineSeparator(),
                response.getOutput()
        );

        assertTrue(
                response.getError().isEmpty()
        );
    }

    /*
     * PYTHON
     */

    @Test
    void pythonCodeExecutesSuccessfully() {

        String code = """
                print("Hello Python")
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                "python",
                                code
                        ),
                        ""
                );

        assertTrue(
                response.isSuccess(),
                "Python execution failed: "
                        + response.getError()
        );

        assertEquals(
                "Hello Python"
                        + System.lineSeparator(),
                response.getOutput()
        );

        assertTrue(
                response.getError().isEmpty()
        );
    }

    /*
     * JAVASCRIPT
     */

    @Test
    void javascriptCodeExecutesSuccessfully() {

        String code = """
                console.log("Hello JavaScript");
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                "javascript",
                                code
                        ),
                        ""
                );

        assertTrue(
                response.isSuccess(),
                "JavaScript execution failed: "
                        + response.getError()
        );

        assertEquals(
                "Hello JavaScript"
                        + System.lineSeparator(),
                response.getOutput()
        );

        assertTrue(
                response.getError().isEmpty()
        );
    }

    /*
     * C++
     */

    @Test
    void cppCodeExecutesSuccessfully() {

        String code = """
                #include <iostream>

                int main() {
                    std::cout << "Hello C++" << std::endl;
                    return 0;
                }
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                "cpp",
                                code
                        ),
                        ""
                );

        assertTrue(
                response.isSuccess(),
                "C++ execution failed: "
                        + response.getError()
        );

        assertEquals(
                "Hello C++"
                        + System.lineSeparator(),
                response.getOutput()
        );

        assertTrue(
                response.getError().isEmpty()
        );
    }

    /*
     * VALIDATION
     */

    @Test
    void emptyCodeIsRejected() {

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                "java",
                                ""
                        ),
                        ""
                );

        assertFalse(response.isSuccess());

        assertEquals(
                "Code cannot be empty.",
                response.getError()
        );
    }

    @Test
    void missingLanguageIsRejected() {

        String code = """
                public class Solution {
                    public static void main(String[] args) {
                        System.out.println("Hello");
                    }
                }
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                null,
                                code
                        ),
                        ""
                );

        assertFalse(response.isSuccess());

        assertEquals(
                "Programming language is required.",
                response.getError()
        );
    }

    @Test
    void unsupportedLanguageIsRejected() {

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                "ruby",
                                "puts 'Hello'"
                        ),
                        ""
                );

        assertFalse(response.isSuccess());

        assertEquals(
                "Unsupported language: ruby",
                response.getError()
        );
    }

    /*
     * JAVA COMPILATION ERROR
     */

    @Test
    void javaCompilationErrorIsHandled() {

        String code = """
                class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        this is invalid Java;
                        return new int[]{0, 1};
                    }
                }
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                1L,
                                "java",
                                code
                        ),
                        "4\n2 7 11 15\n9\n"
                );

        assertFalse(response.isSuccess());

        assertFalse(
                response.getError().isEmpty()
        );
    }

    /*
     * JAVA RUNTIME ERROR
     */

    @Test
    void javaRuntimeErrorIsHandled() {

        String code = """
                class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        int x = 10 / 0;
                        return new int[]{0, 1};
                    }
                }
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                1L,
                                "java",
                                code
                        ),
                        "4\n2 7 11 15\n9\n"
                );

        assertFalse(response.isSuccess());

        assertFalse(
                response.getError().isEmpty()
        );
    }

    /*
     * JAVA TIMEOUT
     */

    @Test
    void javaInfiniteLoopTimesOut() {

        String code = """
                class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        while (true) {
                        }
                    }
                }
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                1L,
                                "java",
                                code
                        ),
                        "4\n2 7 11 15\n9\n"
                );

        assertFalse(response.isSuccess());

        assertTrue(
                response.getError().contains("timed out")
                        || response.getError().contains("time limit")
        );
    }

    /*
     * JAVA CUSTOM INPUT
     */

    @Test
    void javaCodeAcceptsInput() {

        String code = """
                class Solution {
                    public int[] twoSum(int[] nums, int target) {

                        for (int i = 0; i < nums.length; i++) {

                            for (int j = i + 1; j < nums.length; j++) {

                                if (nums[i] + nums[j] == target) {

                                    return new int[]{i, j};
                                }
                            }
                        }

                        return new int[]{};
                    }
                }
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                1L,
                                "java",
                                code
                        ),
                        "4\n2 7 11 15\n9\n"
                );

        assertTrue(
                response.isSuccess(),
                "Java stdin execution failed: "
                        + response.getError()
        );

        assertEquals(
                "0 1"
                        + System.lineSeparator(),
                response.getOutput()
        );
    }

    /*
     * PYTHON INPUT
     */

    @Test
    void pythonCodeAcceptsInput() {

        String code = """
                n = int(input())
                print(n * 2)
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                "python",
                                code
                        ),
                        "5\n"
                );

        assertTrue(
                response.isSuccess(),
                "Python stdin execution failed: "
                        + response.getError()
        );

        assertEquals(
                "10"
                        + System.lineSeparator(),
                response.getOutput()
        );
    }

    /*
     * JAVASCRIPT INPUT
     */

    @Test
    void javascriptCodeAcceptsInput() {

        String code = """
                let input = "";

                process.stdin.on("data", data => input += data);

                process.stdin.on("end", () => {

                    const n = parseInt(input.trim());

                    console.log(n * 2);
                });
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                "javascript",
                                code
                        ),
                        "5\n"
                );

        assertTrue(
                response.isSuccess(),
                "JavaScript stdin execution failed: "
                        + response.getError()
        );

        assertEquals(
                "10"
                        + System.lineSeparator(),
                response.getOutput()
        );
    }

    /*
     * C++ INPUT
     */

    @Test
    void cppCodeAcceptsInput() {

        String code = """
                #include <iostream>

                int main() {

                    int n;

                    std::cin >> n;

                    std::cout << n * 2 << std::endl;

                    return 0;
                }
                """;

        CodeRunResponse response =
                service.runCode(
                        new CodeRunRequest(
                                null,
                                "cpp",
                                code
                        ),
                        "5\n"
                );

        assertTrue(
                response.isSuccess(),
                "C++ stdin execution failed: "
                        + response.getError()
        );

        assertEquals(
                "10"
                        + System.lineSeparator(),
                response.getOutput()
        );
    }
}