# --- !Ups

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (1, 'john', 'James', 'john@gmail.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (2, 'Bob', 'James', 'bob@gmail.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO profile(profile_id, first_name, last_name, email, password, birth_date, gender) values
  (3, 'John', 'Doe', 'john.gherkin.doe@travelea.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO profile(profile_id, first_name, last_name, email, password, birth_date, gender) values
  (4, 'Admin', 'Doe', 'admin.jane.doe@travelea.com', 'yolo', CURRENT_DATE, 'Female');

INSERT into roles (role_id, role_name) values
  (1, 'admin');

INSERT INTO profile_roles (profile_role_id, profile_id, role_id) values
  (1, 2, 1);

INSERT INTO profile_roles (profile_id, role_id) values (4, 1);

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude) values
  (1, 'Tokyo', 'City', 'Japan', 'Japan', 17.68, 67.98);

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude) values
  (1, 'Matakana', 'Town', 'New Zealand', 'Rodney', 67.45, -67.98);

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude, visible) values
  (3, 'Ruhr', 'Area', 'Germany', 'North Rhine-Westphalia', 51.5, 7.5, 0);

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude, visible) values
  (3, 'Ardennes', 'Forest', 'Belgium', 'Wallonia', 50.25, 5.67, 0);

INSERT INTO trip(name, profile_id) VALUES ('Johnny Trip', 3);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (1, 3, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (1, 4, CURRENT_DATE, CURRENT_DATE, 1);
