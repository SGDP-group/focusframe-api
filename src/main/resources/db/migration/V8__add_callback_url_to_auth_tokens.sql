ALTER TABLE auth_tokens
ADD COLUMN IF NOT EXISTS callback_url VARCHAR(512);

UPDATE auth_tokens
SET callback_url = ip_address
WHERE callback_url IS NULL;

ALTER TABLE auth_tokens
ALTER COLUMN callback_url SET NOT NULL;
