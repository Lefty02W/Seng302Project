# --- !Ups

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (1, 'john', 'James', 'john@gmail.com', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (2, 'Bob', 'James', 'bob@gmail.com', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Male');

INSERT INTO profile(profile_id, first_name, last_name, email, password, birth_date, gender) values
  (3, 'John', 'Doe', 'john.gherkin.doe@travelea.com', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Male');

INSERT INTO profile(profile_id, first_name, last_name, email, password, birth_date, gender) values
  (4, 'Admin', 'Doe', 'admin.jane.doe@travelea.com', 'yolo', CURRENT_DATE, 'Female');

INSERT INTO profile(profile_id, first_name, last_name, email, password, birth_date, gender) values
  (5, 'To', 'Be', 'DELETED@travelea.com', 'swag', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (6, 'Dave', 'Brown', 'dave@email', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender) values
  (7, 'steve', 'miller', 'steve@email', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Male');

INSERT INTO profile (profile_id, first_name, last_name, email, password, birth_date, gender, soft_delete) values
  (8, 'Johnny', 'Sins', 'sins@gmail.com', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Male', 1);

INSERT INTO profile(profile_id,first_name,last_name,email,password,birth_date,gender,soft_delete) values
  (9, 'Lisa', 'Curt', 'lisa@gmail.com', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Female', 1);

INSERT INTO profile(profile_id,first_name,last_name,email,password,birth_date,gender) values
  (10, 'Burt', 'Curt', 'BurtCurt@gmail.com', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Female');

INSERT INTO profile(profile_id,first_name,last_name,email,password,birth_date,gender) values
  (11, 'Bort', 'Cort', 'BortCort@gmail.com', '$2a$12$nODuNzk9U7Hrq6DgspSp4.uMJbF9bZ/qCRJEx0jE8u8q5QiMZgUrm', CURRENT_DATE, 'Male');

INSERT into roles (role_id, role_name) values
  (1, 'admin');

INSERT INTO profile_roles (profile_role_id, profile_id, role_id) values
  (1, 2, 1);

INSERT INTO profile_roles (profile_role_id, profile_id, role_id) values
  (3, 11, 1);

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

  INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible, soft_delete) values
  (7, 3, 'Crystal', 'City', 'Australia', 'Australia', 0.0, 0.0, 0, 1);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible) values
  (8, 1, 'New York', 'City', 'America', 'state', 0.0, 0.0, 1);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible) values
  (9, 2, 'Kentucky', 'City', 'America', 'state', 0.0, 0.0, 1);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible) values
  (10, 2, 'Texas', 'City', 'America', 'state', 0.0, 0.0, 1);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible) values
  (11, 2, 'New Orleans', 'City', 'America', 'state', 0.0, 0.0, 1);

INSERT INTO destination (destination_id, profile_id, name, type, country, district, latitude, longitude, visible) values
  (12, 2, 'New Jersey', 'City', 'America', 'state', 0.0, 0.0, 1);

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

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (2, 10, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO trip(trip_id, name, profile_id) VALUES (3, 'Johnny Trip', 3);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (3, 3, CURRENT_DATE, CURRENT_DATE, 1);

INSERT INTO trip_destination(trip_id, destination_id, arrival, departure, dest_order) values
  (3, 4, CURRENT_DATE, CURRENT_DATE, 1);

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

INSERT INTO treasure_hunt (treasure_hunt_id, profile_id, destination_id, riddle, start_date, end_date) VALUES
  (6, 2, 11, 'A fourth riddle', CURRENT_DATE, CURRENT_DATE);

INSERT INTO undo_stack (entry_id, item_type, object_id, profile_id, time_created) VALUES
  (1, 'destination', 5, 2, CURRENT_DATE - 5);

INSERT INTO undo_stack (entry_id, item_type, object_id, profile_id, time_created) VALUES
  (2, 'destination', 2, 3, CURRENT_DATE);


INSERT INTO traveller_type(traveller_type_id, traveller_type_name) values (1, 'Backpacker');

INSERT INTO traveller_type(traveller_type_id, traveller_type_name) values (2, 'Groupie');

INSERT INTO traveller_type(traveller_type_id, traveller_type_name) values (3, 'Gap year');

INSERT INTO traveller_type(traveller_type_id, traveller_type_name) values (4, 'ThrillSeeker');

INSERT INTO destination_traveller_type(id, destination_id, traveller_type_id) values (1,1,1);

INSERT INTO destination_traveller_type(id, destination_id, traveller_type_id) values (2,5,2);

INSERT INTO destination_traveller_type(id, destination_id, traveller_type_id) values (3,7,2);

INSERT INTO destination_request (id, destination_id, profile_id) values (1,1,1);

INSERT INTO destination_request (id, destination_id, profile_id) values (2,1,2);

INSERT INTO destination_change (id, traveller_type_id, action, request_id) values (1,1,1,1);

INSERT INTO destination_change (id, traveller_type_id, action, request_id) values (2,1,1,2);

INSERT INTO undo_stack (entry_id, item_type, object_id, profile_id, time_created) VALUES
  (3, 'destination', 3, 5, CURRENT_DATE);

  INSERT INTO undo_stack (entry_id, item_type, object_id, profile_id, time_created) VALUES
  (4, 'destination', 7, 11, CURRENT_DATE - 5);

INSERT INTO music_genre (genre_Id, genre) VALUES
  (1, 'Rock');

INSERT INTO music_genre (genre_Id, genre) VALUES
  (2, 'Alternative');

INSERT INTO music_genre (genre_Id, genre) VALUES
  (3, 'Reggae');

INSERT INTO music_genre (genre_Id, genre) VALUES
  (4, 'Indie');

INSERT INTO artist (artist_id, artist_name, biography, members) VALUES
  (1, 'Mr Walsh', 'Mr Walsh', 'walsh');

INSERT INTO artist (artist_id, artist_name, biography, members, verified) VALUES
  (2, 'James', 'James', 'james', 1);

INSERT INTO artist (artist_id, artist_name, biography, members) VALUES
  (3, 'Jerry', 'Jerry', 'jerry');


INSERT INTO artist (artist_id, artist_name, biography, members) VALUES
  (4, 'Yes', 'Jerry', 'jerry');

INSERT INTO artist (artist_id, artist_name, biography, members) VALUES
  (5, 'Steve', 'Big boi steve', 'jerry');

INSERT INTO artist_genre (artist_id, genre_id) VALUES
  (1, 4);

INSERT INTO artist_genre (artist_id, genre_id) VALUES
  (4, 4);

INSERT INTO artist_genre (artist_id, genre_id) VALUES
  (3, 4);

INSERT INTO artist_genre (artist_id, genre_id) VALUES
  (2, 4);

INSERT INTO artist_genre (artist_id, genre_id) VALUES
  (1, 3);

INSERT INTO artist_genre (artist_id, genre_id) VALUES
  (3, 1);

INSERT INTO artist_profile (artist_id, profile_id) VALUES
  (2, 2);

INSERT INTO follow_artist (artist_follow_id, profile_id, artist_id) VALUES
  (1, 1, 2);


INSERT INTO follow_artist (artist_follow_id, profile_id, artist_id) VALUES
  (2, 1, 3);

INSERT INTO passport_country (passport_country_id, passport_name) VALUES
  (1, 'New Zealand');

INSERT INTO passport_country (passport_country_id, passport_name) VALUES
  (2, 'Fiji');

INSERT INTO artist_country (artist_id, country_id) VALUES
  (3,1);

INSERT INTO events (event_id, event_name, description, destination_id, start_date, end_date, age_restriction) VALUES
  (1, 'Event', 'description', 9, CURRENT_DATE, CURRENT_DATE, 0);

INSERT INTO events (event_id, event_name, description, destination_id, start_date, end_date, age_restriction) VALUES
  (2, 'Woodstock', 'description', 9, CURRENT_DATE, CURRENT_DATE, 0);

INSERT INTO events (event_id, event_name, description, destination_id, start_date, end_date, age_restriction) VALUES
  (3, 'Burning Man', 'description', 9, CURRENT_DATE, CURRENT_DATE, 0);

INSERT INTO event_artists (artist_id, event_id) VALUES
  (1, 2);

INSERT INTO event_artists (artist_id, event_id) VALUES
  (1, 3);
