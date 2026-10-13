package com.aidsa.platform.execution.driver;

public class BinarySearchJavaDriver implements JavaProblemDriver {

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

                    private static int[] parseArray(
                            Scanner scanner,
                            int n
                    ) {

                        int[] nums = new int[n];

                        for (int i = 0; i < n; i++) {
                            nums[i] = scanner.nextInt();
                        }

                        return nums;
                    }

                    public static void main(String[] args) {

                        Scanner scanner =
                                new Scanner(System.in);

                        int n = scanner.nextInt();

                        int[] nums =
                                parseArray(scanner, n);

                        int target =
                                scanner.nextInt();

                        Solution solution =
                                new Solution();

                        int result =
                                solution.search(nums, target);

                        System.out.println(result);
                    }
                }
                """.formatted(normalizedCode);
    }
}