begin;

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS citext;


CREATE TABLE users(
    id            UUID        DEFAULT gen_random_uuid() NOT NULL,
    external_id   text        unique,
    created_at    TIMESTAMP DEFAULT now() NOT NULL,
    updated_at    TIMESTAMP DEFAULT now() NOT NULL,
    deleted_at    TIMESTAMP,
    activated_at  BIGINT,
    image_url     text,
    first_name    text,
    last_name     text,
    handle        citext UNIQUE,
    agreement_text text,
    bio           text,
    title         text,
    email         citext UNIQUE,
    PRIMARY KEY (id)
);


-- user indices
create unique index idx_user_emails on users (email);
create unique index idx_user_handles on users (handle);
create unique index idx_user_external_ids on users (external_id);

CREATE TABLE endorsements(
    id            UUID        DEFAULT gen_random_uuid() NOT NULL,
    created_at    TIMESTAMP DEFAULT now() NOT NULL,
    updated_at    TIMESTAMP DEFAULT now() NOT NULL,
    deleted_at    TIMESTAMP,
    message text NOT NULL,
    first_name    citext,
    last_name     citext,
    relationship  citext,
    skills        text,
    social_links  jsonb,
    endorser_id   UUID        NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (endorser_id) REFERENCES users (id)
);

-- endorsement indices
create unique index idx_endorsements_by_name on endorsements (first_name, last_name, endorser_id);

CREATE TABLE endorser_access(
    id            UUID        DEFAULT gen_random_uuid() NOT NULL,
    created_at    TIMESTAMP DEFAULT now() NOT NULL,
    updated_at    TIMESTAMP DEFAULT now() NOT NULL,
    approved_at   TIMESTAMP,
    deleted_at    TIMESTAMP,
    message text NOT NULL,
    requester_email citext NOT NULL,
    requester_id UUID,
    endorser_id       UUID        NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (endorser_id) REFERENCES users (id),
    FOREIGN KEY (requester_id) REFERENCES users (id)
);

-- endorser access indices
create unique index idx_endorsement_access_requester_id_endorser_id on endorser_access (requester_id, endorser_id);
create unique index idx_endorsement_access_requester_email_endorser_id on endorser_access(requester_email, endorser_id);

end;
