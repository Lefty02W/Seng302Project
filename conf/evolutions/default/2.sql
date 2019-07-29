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

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender, soft_delete) values
  (8, 'Johnny', 'Sins', 'sins@gmail.com', 'password', CURRENT_DATE, 'Male', 1);

INSERT INTO profile(profile_id,first_name,last_name,email,password,birth_date,gender,soft_delete) values
  (9, 'Lisa', 'Curt', 'lisa@gmail.com', 'password', CURRENT_DATE, 'Female', 1);

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

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible, soft_delete) values
  (5, 3, 'yes', 'Forest', 'Belgium', 'Wallonia', 50.25, 5.67, 0, 1);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible, soft_delete) values
  (6, 3, 'luke', 'City', 'Australia', 'Australia', 0.0, 0.0, 0, 1);


INSERT INTO trip(trip_id, name, profile_id) VALUES (1, 'Johnny Trip', 3);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (1, 3, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (1, 4, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO trip(trip_id, name, profile_id) VALUES (2, 'to delete Trip', 3);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (2, 5, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (2, 4, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO photo (visible, content_type, name,  path) values
  (0, 'image/jpg', 'testPic1.jpg', 'photos/personalPhotos/testPic1.jpg');

INSERT INTO personal_photo (profile_id, photo_id, is_profile_photo) values
  (1, 1, 0);

INSERT INTO photo (visible, content_type, name, path) values
  (0, 'image/jpg', 'testPic2.jpg', 'photos/personalPhotos/testPic2.jpg');

INSERT INTO personal_photo (profile_id, photo_id, is_profile_photo) values
  (2, 2, 0);

INSERT INTO treasure_hunt (treasure_hunt_id, profile_id, destination_id, riddle, start_date, end_date) VALUES
  (1, 1, 1, 'Yes but No', CURRENT_DATE, CURRENT_DATE);

INSERT INTO treasure_hunt (treasure_hunt_id, profile_id, destination_id, riddle, start_date, end_date) VALUES
  (2, 1, 2, 'What has a head, a tail, is brown, and has no legs?', CURRENT_DATE, CURRENT_DATE);

INSERT INTO treasure_hunt (treasure_hunt_id, profile_id, destination_id, riddle, start_date, end_date) VALUES
  (3, 2, 1, 'A riddle', CURRENT_DATE, CURRENT_DATE);

INSERT INTO treasure_hunt (treasure_hunt_id, profile_id, destination_id, riddle, start_date, end_date) VALUES
  (4, 2, 1, 'A second riddle', CURRENT_DATE, CURRENT_DATE);

INSERT INTO treasure_hunt (treasure_hunt_id, profile_id, destination_id, riddle, start_date, end_date) VALUES
  (5, 2, 1, 'A third riddle', CURRENT_DATE, CURRENT_DATE);

INSERT INTO undo_stack (entry_id, item_type, object_id, profile_id, time_created) VALUES
  (1, 'destination', 5, 2, CURRENT_DATE - 5);

INSERT INTO undo_stack (entry_id, item_type, object_id, profile_id, time_created) VALUES
  (2, 'destination', 2, 3, CURRENT_DATE);

INSERT INTO undo_stack (entry_id, item_type, object_id, profile_id, time_created) VALUES
  (3, 'destination', 3, 5, CURRENT_DATE);