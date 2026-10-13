package com.aidsa.platform.repository;

import com.aidsa.platform.model.Difficulty;
import com.aidsa.platform.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository
        extends JpaRepository<Problem, Long>,
                JpaSpecificationExecutor<Problem> {

    Optional<Problem> findBySlug(String slug);

    Optional<Problem> findBySourceAndSourceProblemId(
            String source,
            String sourceProblemId
    );

    boolean existsBySourceAndSourceProblemId(
            String source,
            String sourceProblemId
    );

    Optional<Problem> findTopByOrderByProblemNumberDesc();

    List<Problem> findByDifficulty(Difficulty difficulty);

    List<Problem> findByCategoryIgnoreCase(String category);
}
