CREATE TABLE contacts (
                           id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                           user_id bigint NOT NULL,
                           code_id bigint NOT NULL,
                           phone_number bigint NOT NULL,
                           has_whatsapp boolean,
                           has_telegram boolean,

                           updated_at timestamptz DEFAULT current_timestamp,
                           created_at timestamptz DEFAULT current_timestamp,

                           CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                           CONSTRAINT fk_code_id FOREIGN KEY (code_id) REFERENCES phone_codes (id) ON DELETE CASCADE
);

CREATE TABLE phone_codes (
                          id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                          code varchar(16) NOT NULL,
                          country_name_rus varchar(128),
                          country_name_eng varchar(128),
                          country_name_esp varchar(128),
                          country_name_por varchar(128),
                          country_name_ind varchar(128),
                            flag varchar(MAX);
);

CREATE INDEX [IF NOT EXISTS] idx_country_name_rus
    ON phone_codes(country_name_rus);

CREATE INDEX [IF NOT EXISTS] idx_country_name_eng
    ON phone_codes(country_name_eng);

CREATE INDEX [IF NOT EXISTS] idx_country_name_esp
    ON phone_codes(country_name_esp);

CREATE INDEX [IF NOT EXISTS] idx_country_name_por
    ON phone_codes(country_name_por);

CREATE INDEX [IF NOT EXISTS] idx_country_name_ind
    ON phone_codes(country_name_ind);

INSERT INTO types (code, country_id)
VALUES
    ('+375', 6),
    ('+1', 3),
    ('+33', 8),
    ('+7', 5),
    ('+7', 1),
    ('+66', 4),
    ('+1', 2)
    ('+84', 7);