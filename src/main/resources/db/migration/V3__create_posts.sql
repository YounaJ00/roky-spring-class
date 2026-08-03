CREATE TABLE posts
(
    post_id    BIGINT       NOT NULL AUTO_INCREMENT,
    author_id  BIGINT       NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    view_count BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_posts PRIMARY KEY (post_id),
    CONSTRAINT fk_posts_author
        FOREIGN KEY (author_id) REFERENCES users (user_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_posts_author_id
    ON posts (author_id);