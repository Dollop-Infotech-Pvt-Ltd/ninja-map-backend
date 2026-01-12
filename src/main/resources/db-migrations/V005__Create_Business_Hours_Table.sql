-- =====================================================
-- Migration: V005__Create_Business_Hours_Table.sql
-- Description: Create business_hours table for managing business operating hours
-- Author: System Generated
-- Date: 2025-01-12
-- =====================================================

-- Create business_hours table
CREATE TABLE IF NOT EXISTS business_hours (
    id VARCHAR(36) NOT NULL,
    business_id VARCHAR(36) NOT NULL,
    weekday VARCHAR(20) NOT NULL,
    is_open_24_hours BOOLEAN NOT NULL DEFAULT FALSE,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    opening_time TIME,
    closing_time TIME,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT pk_business_hours PRIMARY KEY (id),
    CONSTRAINT fk_business_hours_business FOREIGN KEY (business_id) REFERENCES business(id) ON DELETE CASCADE,
    CONSTRAINT uk_business_hours_weekday UNIQUE (business_id, weekday, is_deleted)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_business_hours_business_id ON business_hours(business_id);
CREATE INDEX IF NOT EXISTS idx_business_hours_weekday ON business_hours(weekday);
CREATE INDEX IF NOT EXISTS idx_business_hours_active ON business_hours(is_active);
CREATE INDEX IF NOT EXISTS idx_business_hours_deleted ON business_hours(is_deleted);
CREATE INDEX IF NOT EXISTS idx_business_hours_created_date ON business_hours(created_date);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_business_hours_business_active ON business_hours(business_id, is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_business_hours_active_deleted ON business_hours(is_active, is_deleted);

-- Add comments for documentation
COMMENT ON TABLE business_hours IS 'Business operating hours table for each day of the week';
COMMENT ON COLUMN business_hours.id IS 'Primary key - UUID';
COMMENT ON COLUMN business_hours.business_id IS 'Foreign key reference to business';
COMMENT ON COLUMN business_hours.weekday IS 'Day of the week (SUNDAY through SATURDAY)';
COMMENT ON COLUMN business_hours.is_open_24_hours IS 'Flag indicating 24-hour operation';
COMMENT ON COLUMN business_hours.is_closed IS 'Flag indicating business is closed on this day';
COMMENT ON COLUMN business_hours.opening_time IS 'Opening time in HH:mm format';
COMMENT ON COLUMN business_hours.closing_time IS 'Closing time in HH:mm format';
COMMENT ON COLUMN business_hours.created_date IS 'Timestamp when record was created';
COMMENT ON COLUMN business_hours.updated_date IS 'Timestamp when record was last updated';
COMMENT ON COLUMN business_hours.is_deleted IS 'Soft delete flag';
COMMENT ON COLUMN business_hours.is_active IS 'Active status flag';
