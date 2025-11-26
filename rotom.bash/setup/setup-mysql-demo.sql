-- Insert demo accounts
INSERT INTO rotom.accounts (username, auth_string) VALUES ("admin", "admin");
INSERT INTO rotom.accounts (username, auth_string) VALUES ("jan", "password");
INSERT INTO rotom.accounts (username, auth_string) VALUES ("piet", "password");

-- Insert demo items
INSERT INTO rotom.items (owner_id, item_name)
  SELECT user_id, "Drill"
  FROM rotom.accounts
  WHERE username = "jan";
INSERT INTO rotom.items (owner_id, item_name, item_description)
  SELECT user_id, "Car", "It's a car."
  FROM rotom.accounts
  WHERE username = "jan";
INSERT INTO rotom.items (owner_id, item_name)
  SELECT user_id, "Screwdriver"
  FROM rotom.accounts
  WHERE username = "piet";


INSERT INTO rotom.loans (item_id, borrower_id, loan_start, loan_end)
VALUES (
  (SELECT item_id FROM rotom.items WHERE item_name = "Drill"),
  (SELECT user_id from rotom.accounts WHERE username = "piet"),
  NOW(),
  DATE_ADD(NOW(), INTERVAL 14 DAY)
);

INSERT INTO rotom.loans (item_id, borrower_id, loan_start, loan_end)
VALUES (
  (SELECT item_id FROM rotom.items WHERE item_name = "Screwdriver"),
  (SELECT user_id from rotom.accounts WHERE username = "jan"),
  DATE_SUB(NOW(), INTERVAL 14 DAY),
  DATE_SUB(NOW(), INTERVAL 11 DAY)
);

UPDATE rotom.loans SET loan_returned = NOW() WHERE borrower_id = (
  SELECT user_id FROM rotom.accounts WHERE username = "jan"
);