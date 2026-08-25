-- Drop tables if they exist to allow clean recreations
DROP TABLE IF EXISTS audition_applications CASCADE;
DROP TABLE IF EXISTS auditions CASCADE;
DROP TABLE IF EXISTS artist_skills CASCADE;
DROP TABLE IF EXISTS artist_experience CASCADE;
DROP TABLE IF EXISTS artist_profiles CASCADE;
DROP TABLE IF EXISTS organizer_profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table (authentication and login credentials)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('ARTIST', 'ORGANIZER', 'ADMIN')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Artist profile table
CREATE TABLE artist_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    bio TEXT,
    location VARCHAR(100),
    contact_number VARCHAR(20),
    profile_picture_url VARCHAR(255),
    resume_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Artist skills table
CREATE TABLE artist_skills (
    id BIGSERIAL PRIMARY KEY,
    artist_profile_id BIGINT NOT NULL REFERENCES artist_profiles(id) ON DELETE CASCADE,
    skill_name VARCHAR(50) NOT NULL,
    proficiency_level VARCHAR(30) NOT NULL CHECK (proficiency_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'))
);

-- Artist experience table
CREATE TABLE artist_experience (
    id BIGSERIAL PRIMARY KEY,
    artist_profile_id BIGINT NOT NULL REFERENCES artist_profiles(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    organization VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE
);

-- Organizer profile table
CREATE TABLE organizer_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    organization_name VARCHAR(100) NOT NULL,
    description TEXT,
    website_url VARCHAR(255),
    contact_number VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Auditions table
CREATE TABLE auditions (
    id BIGSERIAL PRIMARY KEY,
    organizer_profile_id BIGINT NOT NULL REFERENCES organizer_profiles(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT,
    location VARCHAR(100) NOT NULL,
    compensation VARCHAR(100),
    application_deadline DATE NOT NULL,
    status VARCHAR(30) NOT NULL CHECK (status IN ('DRAFT', 'ACTIVE', 'CLOSED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Audition applications table
CREATE TABLE audition_applications (
    id BIGSERIAL PRIMARY KEY,
    audition_id BIGINT NOT NULL REFERENCES auditions(id) ON DELETE CASCADE,
    artist_profile_id BIGINT NOT NULL REFERENCES artist_profiles(id) ON DELETE CASCADE,
    resume_url VARCHAR(255),
    cover_letter TEXT,
    status VARCHAR(30) NOT NULL CHECK (status IN ('APPLIED', 'SHORTLISTED', 'SELECTED', 'REJECTED')),
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE (audition_id, artist_profile_id)
);
