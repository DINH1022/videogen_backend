CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    fullname VARCHAR(100),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(20)
);


CREATE TABLE USER_SOCIAL_TOKEN(
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    youtube_token varchar(255),
    tiktok_token varchar(255)
);

CREATE TABLE YOUTUBE_UPLOADS(
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    video_id varchar(255)
)

CREATE TABLE USER_VIDEO(
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    video_url varchar(255),
    title VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE USER_VIDEO
ADD CONSTRAINT fk_user_id
FOREIGN KEY (user_id)
REFERENCES users(id);

ALTER TABLE USER_SOCIAL_TOKEN
ADD CONSTRAINT fk_user_tokens
FOREIGN KEY (user_id)
REFERENCES users(id);

ALTER TABLE YOUTUBE_UPLOADS
ADD CONSTRAINT fk_youtube_uploads_user
FOREIGN KEY (user_id)
REFERENCES users(id);

