-- SQL Script to create the ExamResults table
-- This script matches the project requirements perfectly.

CREATE TABLE `ExamResults` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Student ID` text NOT NULL,
  `Course Name` text NOT NULL,
  `Year` text NOT NULL,
  `Semester` text NOT NULL,
  `Score` int(11) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Note: The column names 'Student ID' and 'Course Name' contain spaces,
-- so they must always be enclosed in backticks (`) in your SQL queries.
