-- =====================================================
-- Migration: V003__Data_Validation_And_Constraints.sql
-- Description: Add additional constraints and validation rules
-- Author: System Generated
-- Date: 2025-01-09
-- =====================================================

-- Add check constraints for data validation
ALTER TABLE sub_category 
ADD CONSTRAINT chk_sub_category_name_not_empty 
CHECK (LENGTH(TRIM(sub_category_name)) > 0);

ALTER TABLE sub_category 
ADD CONSTRAINT chk_sub_category_name_length 
CHECK (LENGTH(sub_category_name) <= 100);

-- Add check constraint to ensure created_date <= updated_date
ALTER TABLE sub_category 
ADD CONSTRAINT chk_sub_category_dates 
CHECK (created_date <= updated_date);

-- Add similar constraints to category table if they don't exist
ALTER TABLE category 
ADD CONSTRAINT chk_category_name_not_empty 
CHECK (LENGTH(TRIM(category_name)) > 0);

ALTER TABLE category 
ADD CONSTRAINT chk_category_name_length 
CHECK (LENGTH(category_name) <= 100);

ALTER TABLE category 
ADD CONSTRAINT chk_category_dates 
CHECK (created_date <= updated_date);

-- Create a function to automatically update the updated_date timestamp
CREATE OR REPLACE FUNCTION update_updated_date_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers to automatically update updated_date
CREATE TRIGGER trigger_sub_category_updated_date 
    BEFORE UPDATE ON sub_category 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_date_column();

-- Add trigger for category table as well (if it doesn't exist)
CREATE TRIGGER trigger_category_updated_date 
    BEFORE UPDATE ON category 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_date_column();

-- Add comments for documentation
COMMENT ON CONSTRAINT chk_sub_category_name_not_empty ON sub_category IS 'Ensures subcategory name is not empty';
COMMENT ON CONSTRAINT chk_sub_category_name_length ON sub_category IS 'Ensures subcategory name does not exceed 100 characters';
COMMENT ON CONSTRAINT chk_sub_category_dates ON sub_category IS 'Ensures created_date is not after updated_date';