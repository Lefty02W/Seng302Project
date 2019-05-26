# --- !Ups

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (1, 'john', 'James', 'john@gmail.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (2, 'Bob', 'James', 'bob@gmail.com', 'password', CURRENT_DATE, 'Male');

INSERT into roles (role_id, role_name) values
  (1, 'admin');

INSERT INTO profile_roles (profile_role_id, profile_id, role_id) values
  (1, 2, 1);

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude) values
  (1, 'Tokyo', 'City', 'Japan', 'Japan', 17.68, 67.98);

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude) values
  (1, 'Matakana', 'Town', 'New Zealand', 'Rodney', 67.45, -67.98);