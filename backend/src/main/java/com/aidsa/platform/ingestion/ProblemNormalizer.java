package com.aidsa.platform.ingestion;

import com.aidsa.platform.ingestion.dto.ExternalProblemRecord;
import com.aidsa.platform.model.Difficulty;
import com.aidsa.platform.model.Problem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ProblemNormalizer {

    public Problem normalize(ExternalProblemRecord record) {

        if (record == null) {
            throw new IllegalArgumentException(
                    "Problem record cannot be null."
            );
        }

        String title = require(
                record.title(),
                "Problem title is required."
        );

        String slug = slugify(title);

        Difficulty difficulty =
                normalizeDifficulty(record.difficulty());

        String category = normalizeCategory(
                record.category()
        );

        String tags = normalizeTags(
                record.tags()
        );

        String description = require(
                record.description(),
                "Problem description is required."
        );

        String constraints = record.constraints() == null
                ? ""
                : record.constraints().trim();

        Problem problem = new Problem(
                null,
                title,
                slug,
                description,
                constraints,
                difficulty,
                category,
                tags
        );

        problem.setSource(
                require(
                        record.source(),
                        "Problem source is required."
                )
        );

        problem.setSourceProblemId(
                clean(record.sourceProblemId())
        );

        problem.setSourceUrl(
                clean(record.sourceUrl())
        );

        return problem;
    }

    private Difficulty normalizeDifficulty(String value) {

        String normalized = require(
                value,
                "Problem difficulty is required."
        )
        .toUpperCase(Locale.ROOT)
        .trim();

        return switch (normalized) {

            case "EASY", "BEGINNER", "BASIC" ->
                    Difficulty.EASY;

            case "MEDIUM", "INTERMEDIATE" ->
                    Difficulty.MEDIUM;

            case "HARD", "ADVANCED" ->
                    Difficulty.HARD;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported difficulty: " + value
                    );
        };
    }

    private String normalizeCategory(String value) {

        String category = clean(value);

        if (category.isEmpty()) {
            return "General";
        }

        return category;
    }

    private String normalizeTags(List<String> tags) {

        if (tags == null || tags.isEmpty()) {
            return "";
        }

        return tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private String slugify(String value) {

        return value
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");
    }

    private String require(
            String value,
            String message
    ) {

        String cleaned = clean(value);

        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException(message);
        }

        return cleaned;
    }

    private String clean(String value) {

        return value == null
                ? ""
                : value.trim();
    }
}
