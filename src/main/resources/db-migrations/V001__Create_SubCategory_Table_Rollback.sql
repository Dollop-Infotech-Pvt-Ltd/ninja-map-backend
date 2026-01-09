-- =====================================================
-- Rollback: V001__Create_SubCategory_Table_Rollback.sql
-- Description: Rollback script for sub_category table creation
-- Author: System Generated
-- Date: 2025-01-09
-- =====================================================

-- Drop indexes first
DROP INDEX IF EXISTS idx_sub_category_active_deleted;
DROP INDEX IF EXISTS idx_sub_category_category_active;
DROP INDEX IF EXISTS idx_sub_category_name_search;
DROP INDEX IF EXISTS idx_sub_category_created_date;
DROP INDEX IF EXISTS idx_sub_category_deleted;
DROP INDEX IF EXISTS idx_sub_category_active;
DROP INDEX IF EXISTS idx_sub_category_name;
DROP INDEX IF EXISTS idx_sub_category_category_id;

-- Drop the table (this will also drop the foreign key constraint)
DROP TABLE IF EXISTS sub_category;