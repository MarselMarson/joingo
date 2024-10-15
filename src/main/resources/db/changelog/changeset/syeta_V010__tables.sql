CREATE TABLE country (
                         id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                         name varchar(64) UNIQUE NOT NULL,
                         russian_name varchar(64),
                         flag varchar(256)
);

CREATE TABLE files (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       name varchar(64),
                       url varchar(256),

                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE languages (
                           id smallint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                           name varchar(64)
);

CREATE TABLE users (
                       id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       firstName varchar(64),
                       lastName varchar(64),
                       password varchar(128),
                       birth_date timestamptz,
                       email varchar(64),
                       role varchar(16) NOT NULL,
                       description varchar(4096),
                       active boolean DEFAULT true NOT NULL,
                       country_id bigint,
                       is_email_verified boolean,

                       created_at timestamptz DEFAULT current_timestamp,
                       updated_at timestamptz DEFAULT current_timestamp,

                       CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES country (id)
);

CREATE TABLE types (
                       id smallint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                       name varchar(64)
);

CREATE TABLE tags (
                      id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                      name varchar(64)
);

CREATE TABLE events (
                        id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                        name varchar(128),
                        description varchar(4096),
                        info varchar(4096),
                        schedule jsonb,
                        location_LatLng varchar(64),
                        city varchar(64),
                        state varchar(64),
                        country varchar(64),
                        full_address varchar(256),
                        start_date timestamptz,
                        finish_date timestamptz,
                        is_public boolean,
                        owner_id bigint,
                        type_id smallint,
                        limit_participants int,
                        confirmation boolean,
                        active boolean default true,
                        created_at timestamptz DEFAULT current_timestamp,
                        updated_at timestamptz DEFAULT current_timestamp,

                        CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES users (id),
                        CONSTRAINT fk_type_id FOREIGN KEY (type_id) REFERENCES types (id)
);

CREATE TABLE verification_tokens (
                                     id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                                     token varchar(64) NOT NULL,
                                     user_id bigint NOT NULL,
                                     created_at timestamptz DEFAULT current_timestamp,

                                     CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_token
    ON verification_tokens(token);