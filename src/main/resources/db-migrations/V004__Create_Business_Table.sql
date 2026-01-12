-- =====================================================
-- Migration: V004__Create_Business_Table.sql
-- Description: Create business table with proper constraints and indexes
-- Author: System Generated
-- Date: 2025-01-12
-- =====================================================

-- Create business table
CREATE TABLE IF NOT EXISTS business (
    id VARCHAR(36) NOT NULL,
    business_name VARCHAR(255) NOT NULL,
    sub_category_id VARCHAR(36) NOT NULL,
    address VARCHAR(500) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    website VARCHAR(255),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT pk_business PRIMARY KEY (id),
    CONSTRAINT fk_business_sub_category FOREIGN KEY (sub_category_id) REFERENCES sub_category(id) ON DELETE RESTRICT,
    CONSTRAINT uk_business_phone_number UNIQUE (phone_number, is_deleted)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_business_sub_category_id ON business(sub_category_id);
CREATE INDEX IF NOT EXISTS idx_business_phone_number ON business(phone_number);
CREATE INDEX IF NOT EXISTS idx_business_active ON business(is_active);
CREATE INDEX IF NOT EXISTS idx_business_deleted ON business(is_deleted);
CREATE INDEX IF NOT EXISTS idx_business_created_date ON business(created_date);
CREATE INDEX IF NOT EXISTS idx_business_latitude_longitude ON business(latitude, longitude);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_business_sub_category_active ON business(sub_category_id, is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_business_active_deleted ON business(is_active, is_deleted);

-- Add comments for documentation
COMMENT ON TABLE business IS 'Businesses table for storing business information';
COMMENT ON COLUMN business.id IS 'Primary key - UUID';
COMMENT ON COLUMN business.business_name IS 'Name of the business';
COMMENT ON COLUMN business.sub_category_id IS 'Foreign key reference to subcategory';
COMMENT ON COLUMN business.address IS 'Physical address of the business';
COMMENT ON COLUMN business.latitude IS 'Latitude coordinate of business location';
COMMENT ON COLUMN business.longitude IS 'Longitude coordinate of business location';
COMMENT ON COLUMN business.phone_number IS 'Contact phone number (Nigeria format)';
COMMENT ON COLUMN business.website IS 'Business website URL';
COMMENT ON COLUMN business.created_date IS 'Timestamp when record was created';
COMMENT ON COLUMN business.updated_date IS 'Timestamp when record was last updated';
COMMENT ON COLUMN business.is_deleted IS 'Soft delete flag';
COMMENT ON COLUMN business.is_active IS 'Active status flag';
