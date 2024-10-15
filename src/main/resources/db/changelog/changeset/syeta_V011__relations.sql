CREATE TABLE user_files (
                            user_id bigint,
                            file_id bigint,
                            created_at timestamptz DEFAULT current_timestamp,

                            PRIMARY KEY (user_id, file_id),
                            CONSTRAINT fk_user_key FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                            CONSTRAINT fk_file_key FOREIGN KEY (file_id) REFERENCES files (id)
);

-- TODO rename
CREATE TABLE type_tags (
                           type_id smallint,
                           tag_id int,

                           PRIMARY KEY (type_id, tag_id),
                           CONSTRAINT fk_type_key FOREIGN KEY (type_id) REFERENCES types (id) ON DELETE CASCADE,
                           CONSTRAINT fk_tag_key FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

CREATE TABLE event_files (
                             event_id bigint,
                             file_id bigint,
                             created_at timestamptz DEFAULT current_timestamp,

                             PRIMARY KEY (event_id, file_id),
                             CONSTRAINT fk_event_key FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
                             CONSTRAINT fk_file_id FOREIGN KEY (file_id) REFERENCES files (id)
);

CREATE TABLE event_tags (
                            event_id bigint,
                            tag_id int,
                            created_at timestamptz DEFAULT current_timestamp,

                            PRIMARY KEY (event_id, tag_id),
                            CONSTRAINT fk_event_key FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
                            CONSTRAINT fk_tag_key FOREIGN KEY (tag_id) REFERENCES tags (id)
);

CREATE TABLE event_participants (
                                    event_id bigint,
                                    user_id bigint,
                                    created_at timestamptz DEFAULT current_timestamp,

                                    PRIMARY KEY (event_id, user_id),
                                    CONSTRAINT fk_event_key FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
                                    CONSTRAINT fk_user_key FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE event_languages (
                                 event_id bigint,
                                 lang_id smallint,
                                 created_at timestamptz DEFAULT current_timestamp,

                                 PRIMARY KEY (event_id, lang_id),
                                 CONSTRAINT fk_event_key FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
                                 CONSTRAINT fk_lang_key FOREIGN KEY (lang_id) REFERENCES languages (id)
);

CREATE TABLE user_favourite_events (
                                       event_id bigint,
                                       user_id bigint,
                                       created_at timestamptz DEFAULT current_timestamp,

                                       PRIMARY KEY (event_id, user_id),
                                       CONSTRAINT fk_event_key FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
                                       CONSTRAINT fk_user_key FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE participate_confirmation (
                                          event_id bigint,
                                          participant_id bigint,
                                          created_at timestamptz DEFAULT current_timestamp,

                                          PRIMARY KEY (event_id, participant_id),
                                          CONSTRAINT fk_event_key FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
                                          CONSTRAINT fk_participant_key FOREIGN KEY (participant_id) REFERENCES users (id)
);