-- Create contests table
CREATE TABLE contests (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    round_number INT NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    max_participants INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index on round_number for better query performance
CREATE INDEX idx_contests_round_number ON contests(round_number);

-- Create index on is_active for filtering active contests
CREATE INDEX idx_contests_is_active ON contests(is_active);

-- Create index on start_time and end_time for time-based queries
CREATE INDEX idx_contests_time_range ON contests(start_time, end_time);
