CREATE TABLE event_notification (
                        id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,

                        event_id bigint,
                        target_id bigint,

                        active boolean DEFAULT true NOT NULL,

                        created_at timestamptz DEFAULT current_timestamp,
                        updated_at timestamptz DEFAULT current_timestamp,

                        CONSTRAINT fk_target_id FOREIGN KEY (target_id) REFERENCES users (id),
                        CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE event_participate_request_notification (
                        id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,

                        event_id bigint,
                        participant_id bigint,
                        notification_id bigint,

                        active boolean DEFAULT true NOT NULL,

                        created_at timestamptz DEFAULT current_timestamp,
                        updated_at timestamptz DEFAULT current_timestamp,

                        CONSTRAINT fk_participant_id FOREIGN KEY (participant_id) REFERENCES users (id),
                        CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id),
                        CONSTRAINT fk_notification_id FOREIGN KEY (notification_id) REFERENCES event_notification (id)
);
