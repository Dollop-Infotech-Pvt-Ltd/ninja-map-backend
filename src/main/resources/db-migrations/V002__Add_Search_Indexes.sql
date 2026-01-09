-- =====================================================
-- Migration: V002__Add_Search_Indexes.sql
-- Description: Add additional indexes for enhanced search functionality
-- Author: System Generated
-- Date: 2025-01-09
-- =====================================================

-- Add search indexes for category table
CREATE INDEX IF NOT EXISTS idx_category_name_search ON category(LOWER(category_name));
CREATE INDEX IF NOT EXISTS idx_category_active_deleted ON category(is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_category_created_date ON category(created_date);

-- Add composite indexes for hierarchical search queries
CREATE INDEX IF NOT EXISTS idx_category_name_active ON category(category_name, is_active, is_deleted);

-- Add partial indexes for better performance on filtered queries
CREATE INDEX IF NOT EXISTS idx_sub_category_active_only ON sub_category(category_id, sub_category_name) 
WHERE is_active = true AND is_deleted = false;

CREATE INDEX IF NOT EXISTS idx_category_active_only ON category(category_name) 
WHERE is_active = true AND is_deleted = false;

-- Add GIN index for full-text search (if needed in future)
-- Note: This is commented out as it requires additional setup for full-text search
-- CREATE INDEX IF NOT EXISTS idx_sub_category_fulltext ON sub_category USING gin(to_tsvector('english', sub_category_name));
-- CREATE INDEX IF NOT EXISTS idx_category_fulltext ON category USING gin(to_tsvector('english', category_name));

-- Add comments for documentation
COMMENT ON INDEX idx_category_name_search IS 'Index for case-insensitive category name search';
COMMENT ON INDEX idx_sub_category_name_search IS 'Index for case-insensitive subcategory name search';
COMMENT ON INDEX idx_sub_category_active_only IS 'Partial index for active subcategories only';
COMMENT ON INDEX idx_category_active_only IS 'Partial index for active categories only';