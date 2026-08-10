ALTER TABLE posts
    ADD COLUMN post_uuid BINARY(16) NULL AFTER post_id;

UPDATE posts
SET post_uuid = UNHEX(CONCAT(
        LPAD(HEX(CAST(FLOOR(UNIX_TIMESTAMP(created_at) * 1000) AS UNSIGNED)), 12, '0'),
        '7',
        LEFT(LPAD(HEX(post_id), 18, '0'), 3),
        '8',
        RIGHT(LPAD(HEX(post_id), 18, '0'), 15)
    ))
WHERE post_uuid IS NULL;

ALTER TABLE posts
    DROP PRIMARY KEY,
    DROP COLUMN post_id,
    CHANGE COLUMN post_uuid post_id BINARY(16) NOT NULL,
    ADD CONSTRAINT pk_posts PRIMARY KEY (post_id);
