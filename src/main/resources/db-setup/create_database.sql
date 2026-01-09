-- =====================================================
-- Database Creation Script
-- Description: Create the ninjamapdb database for the application
-- Author: System Generated
-- Date: 2025-01-09
-- =====================================================

-- Connect to PostgreSQL as superuser and run this script

-- Create the database
CREATE DATABASE ninjamapdb
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Grant privileges to the application user
GRANT ALL PRIVILEGES ON DATABASE ninjamapdb TO postgres;

-- Connect to the new database
\c ninjamapdb;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- Verify database creation
SELECT 'Database ninjamapdb created successfully' as status;