-- Users schema

-- !Ups
CREATE TABLE user_data(
  user_id       SERIAL PRIMARY KEY, 
  passwd        VARCHAR(120),
  email         VARCHAR(250),
  firstName     VARCHAR(120),
  lastName      VARCHAR(120),
  confirmed     boolean,
  verified      boolean,
  roles         VARCHAR(20)[]
);

CREATE TABLE user_role (
  user_id       INTEGER REFERENCES user_data,
  user_role     VARCHAR(20)
);

CREATE INDEX ON user_role(user_id);

--INSERT INTO user_data (user_id, first_name, last_name, email, phone, 
--    mobile_phone, enrolled, confirmed, passwd, verified)
--SELECT alumno_id, nombre, apellido, email, telefono, celular, matriculado,
--    confirmado, email, false FROM alumno;

create view login_data as
  select user_data.user_id as id, email, passwd, verified from user_data;

-- !Downs

DROP TABLE user_role;

DROP TABLE user_data;