CREATE DATABASE rotom;

CREATE TABLE rotom.accounts (
  user_id BINARY(16) DEFAULT (UUID_TO_BIN(UUID())) NOT NULL PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  auth_string VARCHAR(255) NOT NULL,
  is_active BOOLEAN DEFAULT 1 NOT NULL
);

CREATE TABLE rotom.items (
  item_id BINARY(16) DEFAULT (UUID_TO_BIN(UUID())) NOT NULL PRIMARY KEY,
  owner_id BINARY(16) NOT NULL,
  item_name VARCHAR(255) NOT NULL,
  item_description TEXT,
  INDEX owner_index (owner_id),
  FOREIGN KEY (owner_id) REFERENCES rotom.accounts(user_id) ON DELETE CASCADE
);

CREATE TABLE rotom.loans (
  loan_id BINARY(16) DEFAULT (UUID_TO_BIN(UUID())) NOT NULL PRIMARY KEY,
  item_id BINARY(16) NOT NULL,
  borrower_id BINARY(16) NOT NULL,
  loan_start DATETIME NOT NULL,
  loan_end DATETIME NOT NULL,
  loan_returned DATETIME NULL,
  INDEX item_index (item_id),
  INDEX borrower_index (borrower_id),
  FOREIGN KEY (item_id) REFERENCES rotom.items(item_id) ON DELETE RESTRICT,
  FOREIGN KEY (borrower_id) REFERENCES rotom.accounts(user_id) ON DELETE RESTRICT
);

CREATE user '${ROTOM_ACCOUNTS_USERNAME}'@'localhost' identified BY '${ROTOM_ACCOUNTS_PASSWORD}';
GRANT INSERT, SELECT, UPDATE, DELETE ON rotom.accounts TO '${ROTOM_ACCOUNTS_USERNAME}'@'localhost';

