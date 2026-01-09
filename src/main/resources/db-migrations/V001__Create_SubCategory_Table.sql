-- =====================================================
-- Migration: V001__Create_SubCategory_Table.sql
-- Description: Create sub_category table with proper constraints and indexes
-- Author: System Generated
-- Date: 2025-01-09
-- =====================================================

-- Create sub_category table
CREATE TABLE IF NOT EXISTS sub_category (
    id VARCHAR(36) NOT NULL,
    sub_category_name VARCHAR(100) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT pk_sub_category PRIMARY KEY (id),
    CONSTRAINT fk_sub_category_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    CONSTRAINT uk_sub_category_name_category UNIQUE (sub_category_name, category_id, is_deleted)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_sub_category_category_id ON sub_category(category_id);
CREATE INDEX IF NOT EXISTS idx_sub_category_name ON sub_category(sub_category_name);
CREATE INDEX IF NOT EXISTS idx_sub_category_active ON sub_category(is_active);
CREATE INDEX IF NOT EXISTS idx_sub_category_deleted ON sub_category(is_deleted);
CREATE INDEX IF NOT EXISTS idx_sub_category_created_date ON sub_category(created_date);
CREATE INDEX IF NOT EXISTS idx_sub_category_name_search ON sub_category(LOWER(sub_category_name));

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_sub_category_category_active ON sub_category(category_id, is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_sub_category_active_deleted ON sub_category(is_active, is_deleted);

-- Add comments for documentation
COMMENT ON TABLE sub_category IS 'Subcategories table for hierarchical category management';
COMMENT ON COLUMN sub_category.id IS 'Primary key - UUID';
COMMENT ON COLUMN sub_category.sub_category_name IS 'Name of the subcategory';
COMMENT ON COLUMN sub_category.category_id IS 'Foreign key reference to parent category';
COMMENT ON COLUMN sub_category.created_date IS 'Timestamp when record was created';
COMMENT ON COLUMN sub_category.updated_date IS 'Timestamp when record was last updated';
COMMENT ON COLUMN sub_category.is_deleted IS 'Soft delete flag';
COMMENT ON COLUMN sub_category.is_active IS 'Active status flag';