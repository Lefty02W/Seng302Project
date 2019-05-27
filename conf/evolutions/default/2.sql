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

INSERT INTO photo (image, visible, content_type, name, crop_x, crop_y, crop_width, crop_height, path) values
  (null, 0, 'image/jpg', 'testPic.jpg', 0, 0, 0, 0, 'photos/personalPhotos/testPic.jpg');

INSERT INTO personal_photo (profile_id, photo_id, is_profile_photo) values
  (1, 1, 0);

INSERT INTO photo (image, visible, content_type, name, crop_x, crop_y, crop_width, crop_height, path) values
  (null, 0, 'image/jpg', 'testPic.jpg', 0, 0, 0, 0, 'photos/personalPhotos/testPic.jpg');

INSERT INTO personal_photo (profile_id, photo_id, is_profile_photo) values
  (2, 2, 0);
