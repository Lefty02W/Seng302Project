# --- !Ups

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (1, 'john', 'James', 'john@gmail.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude) values
  (1, 'Tokyo', 'City', 'Japan', 'Japan', 17.68, 67.98);