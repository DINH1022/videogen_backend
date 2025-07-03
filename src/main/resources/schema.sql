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


CREATE TABLE TIKTOK_UPLOADS(
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    video_id varchar(255),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE audios (
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    language VARCHAR(50),
    voice VARCHAR(100),
    gender VARCHAR(20),
    age VARCHAR(20)
);

CREATE TABLE workspace (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    user_id SERIAL NOT NULL,
    script VARCHAR(255),
    images_set TEXT[],
    audio_id INTEGER,
    video_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE workspace
ADD COLUMN language VARCHAR(50),
ADD COLUMN short_script VARCHAR(1000),
ADD COLUMN writing_style VARCHAR(100);

ALTER TABLE workspace
ALTER COLUMN short_script TYPE TEXT[] USING array[short_script],
ALTER COLUMN script TYPE VARCHAR(2000);

ALTER TABLE workspace
ADD CONSTRAINT fk_workspace_user
FOREIGN KEY (user_id)
REFERENCES users(id);

ALTER TABLE workspace
ADD CONSTRAINT fk_workspace_audio
FOREIGN KEY (audio_id)
REFERENCES audios(id);


ALTER TABLE TIKTOK_UPLOADS
ADD CONSTRAINT fk_tiktok_uploads_user
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

