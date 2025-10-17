-- Performance optimization indexes for MedHead Hospital Allocation
-- These indexes will significantly improve query performance

-- Index on available beds for filtering hospitals with available capacity
CREATE INDEX IF NOT EXISTS idx_hospital_beds ON hospitals(available_beds);

-- Index on location coordinates for distance calculations
CREATE INDEX IF NOT EXISTS idx_hospital_location ON hospitals(latitude, longitude);

-- Index on hospital name for faster lookups
CREATE INDEX IF NOT EXISTS idx_hospital_name ON hospitals(name);

-- Index on created_at for audit queries
CREATE INDEX IF NOT EXISTS idx_hospital_created_at ON hospitals(created_at);

-- Index on updated_at for change tracking
CREATE INDEX IF NOT EXISTS idx_hospital_updated_at ON hospitals(updated_at);

-- Analyze tables to update statistics for query planner
ANALYZE hospitals;
