package com.aidsa.platform.service;

import com.aidsa.platform.execution.driver.BinarySearchJavaDriver;
import com.aidsa.platform.execution.driver.MergeIntervalsJavaDriver;
import com.aidsa.platform.execution.driver.ReverseLinkedListJavaDriver;
import com.aidsa.platform.execution.driver.TwoSumJavaDriver;
import com.aidsa.platform.execution.driver.ValidParenthesesJavaDriver;
import org.springframework.stereotype.Service;
import com.aidsa.platform.execution.driver.JavaProblemDriver;
import com.aidsa.platform.execution.driver.MaximumSubarrayJavaDriver;
import com.aidsa.platform.execution.driver.ValidAnagramJavaDriver;
import com.aidsa.platform.execution.driver.BestTimeToBuyAndSellStockJavaDriver;

@Service
public class JavaDriverService {

    private final JavaProblemDriver twoSumDriver;
    private final JavaProblemDriver reverseLinkedListDriver;
    private final JavaProblemDriver binarySearchDriver;
    private final JavaProblemDriver validParenthesesDriver;
    private final JavaProblemDriver mergeIntervalsDriver;
    private final JavaProblemDriver maximumSubarrayDriver;
    private final JavaProblemDriver validAnagramDriver;
    private final JavaProblemDriver bestTimeToBuyAndSellStockDriver;

    public JavaDriverService() {

        this.twoSumDriver =
                new TwoSumJavaDriver();

        this.reverseLinkedListDriver =
                new ReverseLinkedListJavaDriver();

        this.binarySearchDriver =
                new BinarySearchJavaDriver();

        this.validParenthesesDriver =
                new ValidParenthesesJavaDriver();

        this.mergeIntervalsDriver =
                new MergeIntervalsJavaDriver();

	this.maximumSubarrayDriver =
        new MaximumSubarrayJavaDriver();

        this.validAnagramDriver =
        new ValidAnagramJavaDriver();

        this.bestTimeToBuyAndSellStockDriver =
        new BestTimeToBuyAndSellStockJavaDriver();
    }

    public String buildSource(
            Long problemId,
            String userCode
    ) {

        if (problemId == null) {
            throw new IllegalArgumentException(
                    "Problem ID is required for Java solution execution."
            );
        }

        if (userCode == null || userCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Solution code cannot be empty."
            );
        }

        return switch (problemId.intValue()) {

            case 1 ->
                    twoSumDriver.buildSource(userCode);

            case 2 ->
                    reverseLinkedListDriver.buildSource(userCode);

            case 3 ->
                    binarySearchDriver.buildSource(userCode);

            case 4 ->
                    validParenthesesDriver.buildSource(userCode);

            case 5 ->
                    mergeIntervalsDriver.buildSource(userCode);

            case 6 ->
        maximumSubarrayDriver.buildSource(userCode);
    
           case 7 ->
        validAnagramDriver.buildSource(userCode);

           case 8 ->
        bestTimeToBuyAndSellStockDriver.buildSource(userCode);

            default ->
                    throw new IllegalArgumentException(
                            "No Java driver configured for problem ID: "
                                    + problemId
                    );
        };
    }
}