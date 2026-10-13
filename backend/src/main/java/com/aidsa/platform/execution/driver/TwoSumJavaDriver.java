package com.aidsa.platform.execution.driver;

public class TwoSumJavaDriver implements JavaProblemDriver {

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

                    private static void printArray(int[] values) {

                        if (values == null) {
                            System.out.println("null");
                            return;
                        }

                        for (int i = 0; i < values.length; i++) {

                            if (i > 0) {
                                System.out.print(" ");
                            }

                            System.out.print(values[i]);
                        }

                        System.out.println();
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

                        int[] result =
                                solution.twoSum(nums, target);

                        printArray(result);
                    }
                }
                """.formatted(normalizedCode);
    }
}
