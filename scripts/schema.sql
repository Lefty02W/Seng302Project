CREATE TABLE photo
(
	photo_id int auto_increment
		primary key,
	image longblob null,
	visible tinyint(1) not null,
	content_type varchar(50) not null,
	name varchar(255) not null,
	crop_x int default '0' not null,
	crop_y int default '0' not null,
	crop_width int default '100' not null,
	crop_height int default '100' not null,
	path varchar(255) null
);


CREATE TABLE profile
(
	profile_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	first_name varchar(50) not null,
	middle_name varchar(50) null,
	last_name varchar(50) not null,
	email varchar(50) not null,
	password varchar(255) not null,
	birth_date date not null,
	gender varchar(20) not null,
	time_created timestamp default CURRENT_TIMESTAMP not null
);


CREATE TABLE personal_photo
(
	personal_photo_id INT PRIMARY KEY AUTO_INCREMENT,
	profile_id INT NOT NULL REFERENCES profile ON UPDATE CASCADE ON DELETE CASCADE,
	photo_id INT NOT NULL REFERENCES photo ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE admin
(
	admin_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	profile_id INT NOT NULL REFERENCES profile ON UPDATE CASCADE ON DELETE CASCADE,
	is_master TINYINT(1) NOT NULL DEFAULT '0'
);


create table destination
(
	destination_id int auto_increment
		primary key,
	profile_id INT NOT NULL REFERENCES profile ON UPDATE CASCADE ON DELETE CASCADE,
	name varchar(255) not null,
	type varchar(255) not null,
	country varchar(255) not null,
	district varchar(255) null,
	latitude double null,
	longitude double null,
	visible tinyint(1) default '0' not null
);


create table follow_destination
(
	destination_follow_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	profile_id INT NOT NULL REFERENCES profile ON UPDATE CASCADE ON DELETE CASCADE,
	destination_id INT NOT NULL REFERENCES destination ON UPDATE CASCADE ON DELETE CASCADE
);


create table trip
(
	trip_id int auto_increment primary key,
	name varchar(255) not null,
	profile_id INT NOT NULL REFERENCES profile ON UPDATE CASCADE ON DELETE CASCADE
);


create table trip_destination
(
	trip_destination_id int auto_increment primary key,
	trip_id int not null,
	destination_id int not null,
	arrival date null,
	departure date null,
	dest_order int null,
	constraint destination_fk
	foreign key (destination_id) references destination (destination_id)
		on update cascade on delete cascade,
	constraint trip_fk
	foreign key (trip_id) references trip (trip_id)
		on update cascade on delete cascade
);


CREATE TABLE nationality
(
	nationality_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	nationality_name VARCHAR(50) NOT NULL
);


CREATE TABLE passport_country
(
	passport_country_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	passport_name VARCHAR(50) NOT NULL
);


CREATE TABLE traveller_type
(
	traveller_type_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	traveller_type_name VARCHAR(50) NOT NULL
);


CREATE TABLE profile_nationality
(
	profile_nationality_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	profile INT NOT NULL REFERENCES profile ON UPDATE CASCADE ON DELETE CASCADE,
	natioanlity INT NOT NULL REFERENCES nationality ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE profile_passport_country
(
	profile_passport_country_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	profile INT NOT NULL REFERENCES profile ON UPDATE CASCADE ON DELETE CASCADE,
	passport_country INT NOT NULL REFERENCES passport_country ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE profile_traveller_type
(
	profile_traveller_type_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	profile INT NOT NULL REFERENCES profile ON UPDATE CASCADE ON DELETE CASCADE,
	traveller_type INT NOT NULL REFERENCES traveller_type ON UPDATE CASCADE ON DELETE CASCADE
);