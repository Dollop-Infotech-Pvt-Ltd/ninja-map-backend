-- =====================================================
-- Complete Setup Script for Hierarchical Categories
-- Description: Complete database setup for category-subcategory hierarchy
-- Author: System Generated
-- Date: 2025-01-09
-- Usage: Run this script to set up the complete hierarchical categories system
-- =====================================================

-- Start transaction
BEGIN;

-- =====================================================
-- 1. CREATE SUB_CATEGORY TABLE
-- =====================================================

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

-- =====================================================
-- 2. CREATE INDEXES FOR PERFORMANCE
-- =====================================================

-- Basic indexes
CREATE INDEX IF NOT EXISTS idx_sub_category_category_id ON sub_category(category_id);
CREATE INDEX IF NOT EXISTS idx_sub_category_name ON sub_category(sub_category_name);
CREATE INDEX IF NOT EXISTS idx_sub_category_active ON sub_category(is_active);
CREATE INDEX IF NOT EXISTS idx_sub_category_deleted ON sub_category(is_deleted);
CREATE INDEX IF NOT EXISTS idx_sub_category_created_date ON sub_category(created_date);

-- Search indexes
CREATE INDEX IF NOT EXISTS idx_sub_category_name_search ON sub_category(LOWER(sub_category_name));
CREATE INDEX IF NOT EXISTS idx_category_name_search ON category(LOWER(category_name));

-- Composite indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_sub_category_category_active ON sub_category(category_id, is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_sub_category_active_deleted ON sub_category(is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_category_active_deleted ON category(is_active, is_deleted);

-- Partial indexes for better performance
CREATE INDEX IF NOT EXISTS idx_sub_category_active_only ON sub_category(category_id, sub_category_name) 
WHERE is_active = true AND is_deleted = false;

CREATE INDEX IF NOT EXISTS idx_category_active_only ON category(category_name) 
WHERE is_active = true AND is_deleted = false;

-- =====================================================
-- 3. ADD DATA VALIDATION CONSTRAINTS
-- =====================================================

-- Sub-category constraints
ALTER TABLE sub_category 
ADD CONSTRAINT chk_sub_category_name_not_empty 
CHECK (LENGTH(TRIM(sub_category_name)) > 0);

ALTER TABLE sub_category 
ADD CONSTRAINT chk_sub_category_name_length 
CHECK (LENGTH(sub_category_name) <= 100);

ALTER TABLE sub_category 
ADD CONSTRAINT chk_sub_category_dates 
CHECK (created_date <= updated_date);

-- Category constraints (add if they don't exist)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.check_constraints 
                   WHERE constraint_name = 'chk_category_name_not_empty') THEN
        ALTER TABLE category 
        ADD CONSTRAINT chk_category_name_not_empty 
        CHECK (LENGTH(TRIM(category_name)) > 0);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.check_constraints 
                   WHERE constraint_name = 'chk_category_name_length') THEN
        ALTER TABLE category 
        ADD CONSTRAINT chk_category_name_length 
        CHECK (LENGTH(category_name) <= 100);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.check_constraints 
                   WHERE constraint_name = 'chk_category_dates') THEN
        ALTER TABLE category 
        ADD CONSTRAINT chk_category_dates 
        CHECK (created_date <= updated_date);
    END IF;
END $$;

-- =====================================================
-- 4. CREATE TRIGGERS FOR AUTOMATIC TIMESTAMP UPDATES
-- =====================================================

-- Create function for updating timestamps
CREATE OR REPLACE FUNCTION update_updated_date_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers
DROP TRIGGER IF EXISTS trigger_sub_category_updated_date ON sub_category;
CREATE TRIGGER trigger_sub_category_updated_date 
    BEFORE UPDATE ON sub_category 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_date_column();

-- Add trigger for category table if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.triggers 
                   WHERE trigger_name = 'trigger_category_updated_date') THEN
        CREATE TRIGGER trigger_category_updated_date 
            BEFORE UPDATE ON category 
            FOR EACH ROW 
            EXECUTE FUNCTION update_updated_date_column();
    END IF;
END $$;

-- =====================================================
-- 5. ADD TABLE AND COLUMN COMMENTS
-- =====================================================

COMMENT ON TABLE sub_category IS 'Subcategories table for hierarchical category management';
COMMENT ON COLUMN sub_category.id IS 'Primary key - UUID';
COMMENT ON COLUMN sub_category.sub_category_name IS 'Name of the subcategory';
COMMENT ON COLUMN sub_category.category_id IS 'Foreign key reference to parent category';
COMMENT ON COLUMN sub_category.created_date IS 'Timestamp when record was created';
COMMENT ON COLUMN sub_category.updated_date IS 'Timestamp when record was last updated';
COMMENT ON COLUMN sub_category.is_deleted IS 'Soft delete flag';
COMMENT ON COLUMN sub_category.is_active IS 'Active status flag';

-- =====================================================
-- 6. CREATE VIEWS FOR COMMON QUERIES (OPTIONAL)
-- =====================================================

-- View for active categories with subcategory counts
CREATE OR REPLACE VIEW v_categories_with_subcategory_counts AS
SELECT 
    c.id,
    c.category_name,
    c.category_picture,
    c.is_active,
    c.created_date,
    c.updated_date,
    COUNT(sc.id) as subcategory_count,
    CASE WHEN COUNT(sc.id) > 0 THEN true ELSE false END as has_subcategories
FROM category c
LEFT JOIN sub_category sc ON c.id = sc.category_id 
    AND sc.is_active = true 
    AND sc.is_deleted = false
WHERE c.is_active = true 
    AND c.is_deleted = false
GROUP BY c.id, c.category_name, c.category_picture, c.is_active, c.created_date, c.updated_date;

-- View for active subcategories with category information
CREATE OR REPLACE VIEW v_subcategories_with_category_info AS
SELECT 
    sc.id,
    sc.sub_category_name,
    sc.category_id,
    c.category_name,
    sc.is_active,
    sc.created_date,
    sc.updated_date
FROM sub_category sc
INNER JOIN category c ON sc.category_id = c.id
WHERE sc.is_active = true 
    AND sc.is_deleted = false
    AND c.is_active = true 
    AND c.is_deleted = false;

-- =====================================================
-- 7. GRANT PERMISSIONS (ADJUST AS NEEDED)
-- =====================================================

-- Grant permissions to application user (adjust username as needed)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON sub_category TO your_app_user;
-- GRANT SELECT ON v_categories_with_subcategory_counts TO your_app_user;
-- GRANT SELECT ON v_subcategories_with_category_info TO your_app_user;

-- Commit transaction
COMMIT;

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Verify table creation
SELECT 'sub_category table created successfully' as status 
WHERE EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'sub_category');

-- Verify indexes
SELECT 'Indexes created successfully' as status, count(*) as index_count
FROM pg_indexes 
WHERE tablename IN ('category', 'sub_category');

-- Verify constraints
SELECT 'Constraints created successfully' as status, count(*) as constraint_count
FROM information_schema.table_constraints 
WHERE table_name IN ('category', 'sub_category');

-- Display summary
SELECT 
    'Setup completed successfully' as status,
    'Tables: category, sub_category' as tables_created,
    'Views: v_categories_with_subcategory_counts, v_subcategories_with_category_info' as views_created,
    'Ready for hierarchical category management' as note;