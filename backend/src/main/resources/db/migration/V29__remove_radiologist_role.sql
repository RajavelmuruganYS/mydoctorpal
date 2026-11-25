-- Remove the deprecated RADIOLOGIST role from the live DB state.
-- Historical Flyway migrations are intentionally left untouched.

DELETE FROM user_roles
WHERE role_id IN (
    SELECT id FROM roles WHERE name = 'RADIOLOGIST'
);

DELETE FROM role_permissions
WHERE role_id IN (
    SELECT id FROM roles WHERE name = 'RADIOLOGIST'
);

DELETE FROM roles
WHERE name = 'RADIOLOGIST';