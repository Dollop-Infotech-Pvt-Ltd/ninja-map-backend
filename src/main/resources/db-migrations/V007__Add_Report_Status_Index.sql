-- =====================================================
-- Migration: V007__Add_Report_Status_Index.sql
-- Description: Add index on reports.status column for efficient status-based queries
-- Author: System Generated
-- Date: 2025-01-30
-- =====================================================

-- Add index on status column for efficient filtering
CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status);

-- Add composite index for status and created_date for common query patterns
CREATE INDEX IF NOT EXISTS idx_reports_status_created_date ON reports(status, created_date DESC);

-- Add comments for documentation
COMMENT ON INDEX idx_reports_status IS 'Index for efficient status-based report filtering';
COMMENT ON INDEX idx_reports_status_created_date IS 'Composite index for status filtering with date sorting';
