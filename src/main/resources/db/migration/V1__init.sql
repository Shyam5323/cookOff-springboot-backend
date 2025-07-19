-- Create "questions" table
CREATE TABLE `questions` (
  `id` CHAR(36) NOT NULL,
  `description` TEXT NOT NULL,
  `title` TEXT NOT NULL,
  `input_format` TEXT,
  `points` INT NOT NULL,
  `round` INT NOT NULL,
  `constraints` TEXT NOT NULL,
  `output_format` TEXT NOT NULL,
  `sample_test_input` TEXT,
  `sample_test_output` TEXT,
  `explanation` TEXT,
  PRIMARY KEY (`id`)
);

-- Create "testcases" table
CREATE TABLE `testcases` (
  `id` CHAR(36) NOT NULL,
  `expected_output` TEXT NOT NULL,
  `memory` DECIMAL(10,2) NOT NULL,
  `input` TEXT NOT NULL,
  `hidden` BOOLEAN NOT NULL,
  `runtime` DECIMAL(10,2) NOT NULL,
  `question_id` CHAR(36) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`question_id`) REFERENCES `questions`(`id`) ON DELETE CASCADE
);

-- Create "users" table
CREATE TABLE `users` (
  `id` CHAR(36) NOT NULL,
  `email` TEXT NOT NULL,
  `reg_no` TEXT NOT NULL,
  `password` TEXT NOT NULL,
  `role` TEXT NOT NULL,
  `round_qualified` INT NOT NULL DEFAULT 0,
  `score` INT DEFAULT 0,
  `name` TEXT NOT NULL,
  `is_banned` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_email` (`email`(255)),
  UNIQUE KEY `unique_reg_no` (`reg_no`(255))
);

-- Create "submissions" table
CREATE TABLE `submissions` (
  `id` CHAR(36) NOT NULL,
  `question_id` CHAR(36) NOT NULL,
  `testcases_passed` INT DEFAULT 0,
  `testcases_failed` INT DEFAULT 0,
  `runtime` DECIMAL(10,2),
  `submission_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `testcase_id` CHAR(36),
  `language_id` INT NOT NULL,
  `description` TEXT,
  `memory` DECIMAL(10,2),
  `user_id` CHAR(36),
  `status` TEXT,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`question_id`) REFERENCES `questions`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`testcase_id`) REFERENCES `testcases`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);
