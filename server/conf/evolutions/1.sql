-- Users schema

-- !Ups
CREATE TABLE user_data(
  user_id       SERIAL PRIMARY KEY, 
  passwd        VARCHAR(120),
  email         VARCHAR(250),
  firstName     VARCHAR(120),
  lastName      VARCHAR(120),
  confirmed     boolean,
  verified      boolean
);

CREATE TABLE user_role (
  user_id       INTEGER REFERENCES user_data,
  user_role     VARCHAR(20)
);

-- !Downs

DROP TABLE user_role;

DROP TABLE user_data;