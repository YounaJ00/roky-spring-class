ALTER TABLE users
    ADD COLUMN user_uuid BINARY(16) NULL AFTER user_id;

UPDATE users
SET user_uuid = UNHEX(CONCAT(
        LPAD(HEX(CAST(FLOOR(UNIX_TIMESTAMP(created_at) * 1000) AS UNSIGNED)), 12, '0'),
        '7',
        LEFT(LPAD(HEX(user_id), 18, '0'), 3),
        '8',
        RIGHT(LPAD(HEX(user_id), 18, '0'), 15)
    ))
WHERE user_uuid IS NULL;

ALTER TABLE posts
    ADD COLUMN author_uuid BINARY(16) NULL AFTER author_id;

UPDATE posts AS post
    INNER JOIN users AS user ON post.author_id = user.user_id
SET post.author_uuid = user.user_uuid
WHERE post.author_uuid IS NULL;

ALTER TABLE posts
    DROP FOREIGN KEY fk_posts_author,
    DROP INDEX idx_posts_author_id,
    DROP COLUMN author_id,
    CHANGE COLUMN author_uuid author_id BINARY(16) NOT NULL;

ALTER TABLE users
    DROP PRIMARY KEY,
    DROP COLUMN user_id,
    CHANGE COLUMN user_uuid user_id BINARY(16) NOT NULL,
    ADD CONSTRAINT pk_users PRIMARY KEY (user_id);

CREATE INDEX idx_posts_author_id
    ON posts (author_id);

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_author
        FOREIGN KEY (author_id) REFERENCES users (user_id);
