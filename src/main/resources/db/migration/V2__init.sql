-- Create "submission_results" table
CREATE TABLE `submission_results` (
  `id` CHAR(36) NOT NULL,
  `submission_id` CHAR(36) NOT NULL,
  `runtime` DECIMAL(10,2) NOT NULL,
  `memory` DECIMAL(10,2) NOT NULL,
  `description` TEXT,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`submission_id`) REFERENCES `submissions`(`id`) ON DELETE CASCADE
);
