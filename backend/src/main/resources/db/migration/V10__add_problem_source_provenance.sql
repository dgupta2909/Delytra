ALTER TABLE problems
ADD COLUMN source VARCHAR(50) NOT NULL DEFAULT 'DELYTRA',
ADD COLUMN source_problem_id VARCHAR(255),
ADD COLUMN source_url TEXT;

CREATE INDEX idx_problems_source
ON problems(source);

CREATE INDEX idx_problems_source_problem_id
ON problems(source, source_problem_id);

CREATE UNIQUE INDEX uq_problems_source_identity
ON problems(source, source_problem_id)
WHERE source_problem_id IS NOT NULL;
