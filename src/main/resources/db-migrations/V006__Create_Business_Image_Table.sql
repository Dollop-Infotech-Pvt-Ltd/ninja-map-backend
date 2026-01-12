-- =====================================================
-- Migration: V006__Create_Business_Image_Table.sql
-- Description: Create business_image table for storing business images
-- Author: System Generated
-- Date: 2025-01-12
-- =====================================================

-- Create business_image table
CREATE TABLE IF NOT EXISTS business_image (
    id VARCHAR(36) NOT NULL,
    business_id VARCHAR(36) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT pk_business_image PRIMARY KEY (id),
    CONSTRAINT fk_business_image_business FOREIGN KEY (business_id) REFERENCES business(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_business_image_business_id ON business_image(business_id);
CREATE INDEX IF NOT EXISTS idx_business_image_display_order ON business_image(business_id, display_order);
CREATE INDEX IF NOT EXISTS idx_business_image_active ON business_image(is_active);
CREATE INDEX IF NOT EXISTS idx_business_image_deleted ON business_image(is_deleted);
CREATE INDEX IF NOT EXISTS idx_business_image_created_date ON business_image(created_date);

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_business_image_business_active ON business_image(business_id, is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_business_image_active_deleted ON business_image(is_active, is_deleted);

-- Add comments for documentation
COMMENT ON TABLE business_image IS 'Business images table for storing business photos';
COMMENT ON COLUMN business_image.id IS 'Primary key - UUID';
COMMENT ON COLUMN business_image.business_id IS 'Foreign key reference to business';
COMMENT ON COLUMN business_image.image_url IS 'URL of the uploaded image';
COMMENT ON COLUMN business_image.display_order IS 'Order of display for images';
COMMENT ON COLUMN business_image.created_date IS 'Timestamp when record was created';
COMMENT ON COLUMN business_image.updated_date IS 'Timestamp when record was last updated';
COMMENT ON COLUMN business_image.is_deleted IS 'Soft delete flag';
COMMENT ON COLUMN business_image.is_active IS 'Active status flag';
