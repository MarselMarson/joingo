ALTER TABLE tetatet_chats ADD COLUMN
    last_message_id bigint REFERENCES tetatet_chat_messages(id);

ALTER TABLE tetatet_chat_messages ALTER COLUMN recipient_id DROP NOT NULL;