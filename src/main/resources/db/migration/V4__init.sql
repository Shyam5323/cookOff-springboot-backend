-- Drop column from "submissions"
ALTER TABLE `submissions`
  DROP COLUMN `testcase_id`,
  MODIFY COLUMN `memory` DECIMAL(10,2);
