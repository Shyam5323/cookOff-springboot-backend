-- Add columns to "submission_results"
ALTER TABLE `submission_results`
  ADD COLUMN `testcase_id` CHAR(36),
  ADD COLUMN `status` TEXT NOT NULL;

-- Alter "users" table
ALTER TABLE `users`
  MODIFY COLUMN `score` DECIMAL(10,2) NOT NULL,
  ADD COLUMN `is_banned` BOOLEAN NOT NULL DEFAULT FALSE;
