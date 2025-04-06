
CREATE TABLE IF NOT EXISTS t_tasks (
                                       tasks_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       title VARCHAR(100) NOT NULL,
                                       description VARCHAR(500),
                                       user_id UUID NOT NULL,
                                       status VARCHAR(30),
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                       is_active BOOLEAN DEFAULT TRUE
);
