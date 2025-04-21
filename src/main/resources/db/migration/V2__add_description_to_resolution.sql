-- V2__add_description_to_resolution.sql
-- Add a non-null description column to resolution table
ALTER TABLE resolution
  ADD COLUMN description VARCHAR(255) NOT NULL DEFAULT '';
