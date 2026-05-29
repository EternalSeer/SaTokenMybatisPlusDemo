INSERT INTO sys_user (username, password, nickname, email, created_at, updated_at, version, deleted)
VALUES ('admin', 'admin123', 'Administrator', 'admin@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0)
ON CONFLICT (username) DO NOTHING;

INSERT INTO sys_login_log (user_id, username, ip_address, user_agent, success, message, created_at, deleted)
SELECT id, username, '127.0.0.1', 'bootstrap', true, 'seed login success', CURRENT_TIMESTAMP, 0
FROM sys_user
WHERE username = 'admin'
  AND NOT EXISTS (
      SELECT 1 FROM sys_login_log WHERE username = 'admin' AND message = 'seed login success'
  );

INSERT INTO sys_login_log (user_id, username, ip_address, user_agent, success, message, created_at, deleted)
SELECT null, 'admin', '127.0.0.1', 'bootstrap', false, 'invalid username or password', CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (
    SELECT 1 FROM sys_login_log WHERE username = 'admin' AND message = 'invalid username or password'
);
