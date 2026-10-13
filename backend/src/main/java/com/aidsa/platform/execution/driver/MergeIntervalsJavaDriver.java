package com.aidsa.platform.execution.driver;

public class MergeIntervalsJavaDriver implements JavaProblemDriver {

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

                    private static int[][] parseIntervals(
                            Scanner scanner,
                            int n
                    ) {

                        int[][] intervals = new int[n][2];

                        for (int i = 0; i < n; i++) {
                            intervals[i][0] = scanner.nextInt();
                            intervals[i][1] = scanner.nextInt();
                        }

                        return intervals;
                    }

                    private static void printIntervals(
                            int[][] intervals
                    ) {

                        if (intervals == null) {
                            System.out.println();
                            return;
                        }

                        for (int i = 0; i < intervals.length; i++) {

                            if (i > 0) {
                                System.out.print(" ");
                            }

                            System.out.print(
                                    intervals[i][0]
                            );

                            System.out.print(" ");

                            System.out.print(
                                    intervals[i][1]
                            );
                        }

                        System.out.println();
                    }

                    public static void main(String[] args) {

                        Scanner scanner =
                                new Scanner(System.in);

                        int n = scanner.nextInt();

                        int[][] intervals =
                                parseIntervals(scanner, n);

                        Solution solution =
                                new Solution();

                        int[][] result =
                                solution.merge(intervals);

                        printIntervals(result);
                    }
                }
                """.formatted(normalizedCode);
    }
}