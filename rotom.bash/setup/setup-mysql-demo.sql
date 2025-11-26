-- Insert demo accounts
INSERT INTO rotom.accounts (username, auth_string) VALUES ("admin", "admin");
INSERT INTO rotom.accounts (username, auth_string) VALUES ("jan", "password");
INSERT INTO rotom.accounts (username, auth_string) VALUES ("piet", "password");

-- Insert demo items
INSERT INTO rotom.items (owner_id, item_name)
  SELECT user_id, "Drill"
  FROM rotom.accounts
  WHERE username = "jan";
INSERT INTO rotom.items (owner_id, item_name)
  SELECT user_id, "Car"
  FROM rotom.accounts
  WHERE username = "jan";
INSERT INTO rotom.items (owner_id, item_name)
  SELECT user_id, "Screwdriver"
  FROM rotom.accounts
  WHERE username = "piet";
