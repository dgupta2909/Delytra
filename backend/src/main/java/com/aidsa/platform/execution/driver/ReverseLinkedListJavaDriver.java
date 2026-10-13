package com.aidsa.platform.execution.driver;

public class ReverseLinkedListJavaDriver implements JavaProblemDriver {

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

                class ListNode {
                    int val;
                    ListNode next;

                    ListNode() {}

                    ListNode(int val) {
                        this.val = val;
                    }

                    ListNode(int val, ListNode next) {
                        this.val = val;
                        this.next = next;
                    }
                }

                %s

                public class Main {

                    private static ListNode buildList(
                            Scanner scanner,
                            int n
                    ) {

                        if (n == 0) {
                            return null;
                        }

                        ListNode head = null;
                        ListNode tail = null;

                        for (int i = 0; i < n; i++) {

                            int value = scanner.nextInt();

                            ListNode node =
                                    new ListNode(value);

                            if (head == null) {
                                head = node;
                                tail = node;
                            } else {
                                tail.next = node;
                                tail = node;
                            }
                        }

                        return head;
                    }

                    private static void printList(
                            ListNode head
                    ) {

                        if (head == null) {
                            System.out.println();
                            return;
                        }

                        ListNode current = head;

                        while (current != null) {

                            if (current != head) {
                                System.out.print(" ");
                            }

                            System.out.print(current.val);

                            current = current.next;
                        }

                        System.out.println();
                    }

                    public static void main(String[] args) {

                        Scanner scanner =
                                new Scanner(System.in);

                        int n = scanner.nextInt();

                        ListNode head =
                                buildList(scanner, n);

                        Solution solution =
                                new Solution();

                        ListNode result =
                                solution.reverseList(head);

                        printList(result);
                    }
                }
                """.formatted(normalizedCode);
    }
}