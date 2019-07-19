# --- !Ups

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (1, 'john', 'James', 'john@gmail.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (2, 'Bob', 'James', 'bob@gmail.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO profile(profile_id, first_name, last_name, email, password, birth_date, gender) values
  (3, 'John', 'Doe', 'john.gherkin.doe@travelea.com', 'password', CURRENT_DATE, 'Male');

INSERT INTO profile(profile_id, first_name, last_name, email, password, birth_date, gender) values
  (4, 'Admin', 'Doe', 'admin.jane.doe@travelea.com', 'yolo', CURRENT_DATE, 'Female');

INSERT INTO profile(profile_id, first_name, last_name, email, password, birth_date, gender) values
(5, 'To', 'Be', 'DELETED@travelea.com', 'swag', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (6, 'Dave', 'Brown', 'dave@email', 'password', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (7, 'steve', 'miller', 'steve@email', 'password', CURRENT_DATE, 'Male');


INSERT into roles (role_id, role_name) values
  (1, 'admin');

INSERT INTO profile_roles (profile_role_id, profile_id, role_id) values
  (1, 2, 1);

INSERT INTO profile_roles (profile_id, role_id) values (4, 1);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude) values
  (1, 1, 'Tokyo', 'City', 'Japan', 'Japan', 17.68, 67.98);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude) values
  (2, 1, 'Matakana', 'Town', 'New Zealand', 'Rodney', 67.45, -67.98);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible) values
  (3, 3, 'Ruhr', 'Area', 'Germany', 'North Rhine-Westphalia', 51.5, 7.5, 0);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible) values
  (4, 3, 'Ardennes', 'Forest', 'Belgium', 'Wallonia', 50.25, 5.67, 0);

INSERT INTO trip(trip_id, name, profile_id) VALUES (1, 'Johnny Trip', 3);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (1, 3, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (1, 4, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO photo (visible, content_type, name,  path) values
  (0, 'image/jpg', 'testPic1.jpg', 'photos/personalPhotos/testPic1.jpg');

INSERT INTO personal_photo (profile_id, photo_id, is_profile_photo) values
  (1, 1, 0);

INSERT INTO photo (visible, content_type, name, path) values
  (0, 'image/jpg', 'testPic2.jpg', 'photos/personalPhotos/testPic2.jpg');

INSERT INTO personal_photo (profile_id, photo_id, is_profile_photo) values
  (2, 2, 0);

INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude, visible) values
(1, 'Matakana', 'Town', 'New Zealand', 'Rodney', 67.45, -67.98, 1);