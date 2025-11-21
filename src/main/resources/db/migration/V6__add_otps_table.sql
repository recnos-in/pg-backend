-- Create OTP table for mobile-based authentication
CREATE TABLE otps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mobile VARCHAR(15) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    user_type VARCHAR(10) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    attempts INTEGER DEFAULT 0,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for efficient OTP lookup
CREATE INDEX idx_otps_mobile ON otps(mobile);
CREATE INDEX idx_otps_expires ON otps(expires_at);
CREATE INDEX idx_otps_lookup ON otps(mobile, user_type, is_verified, expires_at);

-- Add comment to table
COMMENT ON TABLE otps IS 'Stores OTP codes for mobile-based authentication for users and owners';
COMMENT ON COLUMN otps.user_type IS 'Type of user: USER or OWNER';
COMMENT ON COLUMN otps.attempts IS 'Number of verification attempts (max 3)';
COMMENT ON COLUMN otps.expires_at IS 'OTP expiration timestamp (5 minutes from creation)';