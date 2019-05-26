# --- !Ups

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (1, 'john', 'James', 'john@gmail.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude) values
  (1, 'Tokyo', 'City', 'Japan', 'Japan', 17.68, 67.98);

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude, visible) values
  (1, 'Matakana', 'Town', 'New Zealand', 'Rodney', 67.45, -67.98, 1);

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (2, 'steve', 'miller', 'steve@email', 'password', CURRENT_DATE, 'Male')

