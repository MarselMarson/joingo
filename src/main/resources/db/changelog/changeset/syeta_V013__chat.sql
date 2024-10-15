CREATE TABLE tetatet_chats (
                    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                    user1_id bigint NOT NULL,
                    user2_id bigint NOT NULL,
                    active boolean DEFAULT true NOT NULL,

                    created_at timestamptz DEFAULT current_timestamp,
                    updated_at timestamptz DEFAULT current_timestamp,

                    CHECK (user1_id <> user2_id),
                    CONSTRAINT fk_user1_id FOREIGN KEY (user1_id) REFERENCES users (id) ON DELETE CASCADE,
                    CONSTRAINT fk_user2_id FOREIGN KEY (user2_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX ON tetatet_chats (
    greatest(user1_id, user2_id),
    least(user1_id, user2_id)
);

CREATE TABLE tetatet_chat_messages (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       chat_id bigint NOT NULL,
                       sender_id bigint NOT NULL,
                       recipient_id bigint NOT NULL,
                       content varchar(4096) NOT NULL,
                       status varchar(32) NOT NULL,

                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp,

                       CONSTRAINT fk_chat_id FOREIGN KEY (chat_id) REFERENCES tetatet_chats (id) ON DELETE CASCADE,
                       CONSTRAINT fk_sender_id FOREIGN KEY (sender_id) REFERENCES users (id),
                       CONSTRAINT fk_recipient_id FOREIGN KEY (recipient_id) REFERENCES users (id)
);