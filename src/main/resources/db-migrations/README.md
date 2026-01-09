# Database Migration Scripts for Hierarchical Categories

This directory contains database migration scripts for implementing the hierarchical categories feature in the Ninja Map application.

## Overview

The hierarchical categories system allows for a parent-child relationship between categories and subcategories, enabling better organization and management of location-based data.

## Migration Files

### Core Migration Scripts

1. **V001__Create_SubCategory_Table.sql**
   - Creates the `sub_category` table with proper structure
   - Adds foreign key constraints to `category` table
   - Creates basic indexes for performance
   - Includes unique constraint for subcategory names within categories

2. **V002__Add_Search_Indexes.sql**
   - Adds additional indexes for enhanced search functionality
   - Creates case-insensitive search indexes
   - Adds partial indexes for better performance on filtered queries

3. **V003__Data_Validation_And_Constraints.sql**
   - Adds data validation constraints
   - Creates triggers for automatic timestamp updates
   - Ensures data integrity with check constraints

### Utility Scripts

4. **setup_hierarchical_categories.sql**
   - Complete setup script that combines all migrations
   - Can be run as a single script for fresh installations
   - Includes verification queries and helpful views

5. **V001__Create_SubCategory_Table_Rollback.sql**
   - Rollback script for the main table creation
   - Use this to undo the subcategory table creation if needed

## Database Schema

### sub_category Table Structure

```sql
CREATE TABLE sub_category (
    id VARCHAR(36) NOT NULL,                    -- UUID primary key
    sub_category_name VARCHAR(100) NOT NULL,    -- Subcategory name
    category_id VARCHAR(36) NOT NULL,           -- Foreign key to category
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- Soft delete flag
    is_active BOOLEAN NOT NULL DEFAULT TRUE,    -- Active status flag
    
    CONSTRAINT pk_sub_category PRIMARY KEY (id),
    CONSTRAINT fk_sub_category_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    CONSTRAINT uk_sub_category_name_category UNIQUE (sub_category_name, category_id, is_deleted)
);
```

### Key Features

- **UUID Primary Keys**: Uses VARCHAR(36) for UUID storage
- **Foreign Key Constraints**: Ensures referential integrity with cascade delete
- **Unique Constraints**: Prevents duplicate subcategory names within the same category
- **Soft Delete**: Uses `is_deleted` flag instead of hard deletes
- **Audit Fields**: Automatic timestamp tracking for created and updated dates
- **Performance Indexes**: Optimized for common query patterns

## Indexes Created

### Basic Indexes
- `idx_sub_category_category_id` - For category-based queries
- `idx_sub_category_name` - For name-based searches
- `idx_sub_category_active` - For active status filtering
- `idx_sub_category_deleted` - For soft delete filtering

### Search Indexes
- `idx_sub_category_name_search` - Case-insensitive name search
- `idx_category_name_search` - Case-insensitive category name search

### Composite Indexes
- `idx_sub_category_category_active` - For category + status queries
- `idx_sub_category_active_deleted` - For status-based filtering

### Partial Indexes
- `idx_sub_category_active_only` - Only indexes active, non-deleted records
- `idx_category_active_only` - Only indexes active categories

## Usage Instructions

### For Development Environment

Since the application uses Hibernate's `ddl-auto=update`, the tables will be created automatically. However, you can run these scripts manually for:

1. **Production deployments**
2. **Database version control**
3. **Performance optimization**
4. **Data integrity enforcement**

### Running the Scripts

#### Option 1: Complete Setup (Recommended for fresh installations)
```bash
psql -U your_username -d ninjamapdb -f setup_hierarchical_categories.sql
```

#### Option 2: Individual Migrations (Recommended for existing systems)
```bash
psql -U your_username -d ninjamapdb -f V001__Create_SubCategory_Table.sql
psql -U your_username -d ninjamapdb -f V002__Add_Search_Indexes.sql
psql -U your_username -d ninjamapdb -f V003__Data_Validation_And_Constraints.sql
```

#### Option 3: Rollback (if needed)
```bash
psql -U your_username -d ninjamapdb -f V001__Create_SubCategory_Table_Rollback.sql
```

### Verification

After running the scripts, you can verify the setup with these queries:

```sql
-- Check table structure
\d sub_category

-- Check indexes
\di sub_category*

-- Check constraints
SELECT constraint_name, constraint_type 
FROM information_schema.table_constraints 
WHERE table_name = 'sub_category';

-- Test the relationship
SELECT c.category_name, sc.sub_category_name 
FROM category c 
LEFT JOIN sub_category sc ON c.id = sc.category_id 
WHERE c.is_active = true AND c.is_deleted = false;
```

## Integration with Application

The migration scripts are designed to work with the Java application's JPA entities:

- **Category.java** - Maps to `category` table
- **SubCategory.java** - Maps to `sub_category` table

The application uses:
- **Hibernate** for ORM mapping
- **PostgreSQL** as the database
- **UUID** for primary keys
- **Soft deletes** for data preservation

## Performance Considerations

1. **Indexes**: All common query patterns are indexed
2. **Partial Indexes**: Only active records are indexed for better performance
3. **Foreign Key Constraints**: Ensure data integrity with minimal overhead
4. **Triggers**: Automatic timestamp updates without application logic

## Maintenance

### Regular Maintenance Tasks

1. **Index Maintenance**: PostgreSQL handles this automatically
2. **Statistics Updates**: Run `ANALYZE` periodically
3. **Constraint Validation**: Constraints are enforced automatically

### Monitoring Queries

```sql
-- Check subcategory distribution
SELECT c.category_name, COUNT(sc.id) as subcategory_count
FROM category c
LEFT JOIN sub_category sc ON c.id = sc.category_id
WHERE c.is_active = true AND c.is_deleted = false
GROUP BY c.id, c.category_name
ORDER BY subcategory_count DESC;

-- Check for orphaned subcategories (should be none due to FK constraint)
SELECT COUNT(*) as orphaned_count
FROM sub_category sc
LEFT JOIN category c ON sc.category_id = c.id
WHERE c.id IS NULL;
```

## Troubleshooting

### Common Issues

1. **Foreign Key Violations**: Ensure category exists before creating subcategories
2. **Unique Constraint Violations**: Check for duplicate subcategory names within the same category
3. **Permission Issues**: Ensure database user has proper permissions

### Error Messages

- `duplicate key value violates unique constraint`: Subcategory name already exists in that category
- `violates foreign key constraint`: Referenced category doesn't exist
- `check constraint violation`: Data validation failed (empty names, invalid dates, etc.)

## Future Enhancements

The migration scripts are designed to be extensible for future features:

1. **Full-text Search**: GIN indexes are prepared (commented out)
2. **Hierarchical Queries**: Views are created for common patterns
3. **Additional Constraints**: Easy to add more validation rules
4. **Performance Tuning**: Indexes can be adjusted based on usage patterns

## Support

For issues or questions regarding these migration scripts, please refer to:

1. Application documentation
2. Database administrator
3. Development team lead

---

**Note**: Always backup your database before running migration scripts in production environments.