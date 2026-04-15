-- V4: Enforce unique usernames for authentication
ALTER TABLE users ADD CONSTRAINT uq_users_username UNIQUE (username);
