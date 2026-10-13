package com.aidsa.platform.execution.driver;

public class ValidAnagramJavaDriver implements JavaProblemDriver {

    @Override
    public String buildSource(String userCode) {

        if (userCode == null || userCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Solution code cannot be empty."
            );
        }

        String normalizedCode = userCode.trim();

        if (!normalizedCode.contains("class Solution")) {
            throw new IllegalArgumentException(
                    "Java solution must contain class Solution."
            );
        }

        return """
                import java.util.*;

                %s

                public class Main {

                    public static void main(String[] args) {

                        Scanner scanner =
                                new Scanner(System.in);

                        String s = scanner.nextLine();
                        String t = scanner.nextLine();

                        Solution solution =
                                new Solution();

                        boolean result =
                                solution.isAnagram(s, t);

                        System.out.println(result);
                    }
                }
                """.formatted(normalizedCode);
    }
}