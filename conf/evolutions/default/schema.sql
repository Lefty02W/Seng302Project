create table if not exists artist
(
	artist_id int auto_increment,
	artist_name varchar(500) not null,
	biography varchar(500) not null,
	facebook_link varchar(500) null,
	instagram_link varchar(500) null,
	spotify_link varchar(500) null,
	twitter_link varchar(500) null,
	website_link varchar(50) null,
	soft_delete tinyint default '0' null,
	verified int default '0' null,
	members varchar(500) not null,
	constraint artist_profile_artist_id_uindex
		unique (artist_id)
)
;

alter table artist
	add primary key (artist_id)
;

create table if not exists music_genre
(
	genre_Id int auto_increment,
	genre varchar(20) not null,
	constraint music_genre_genreId_uindex
		unique (genre_Id)
)
;

alter table music_genre
	add primary key (genre_Id)
;

create table if not exists artist_genre
(
	artist_id int not null,
	genre_id int not null,
	constraint artist_genre_artist_fk
		foreign key (artist_id) references artist (artist_id)
			on update cascade on delete cascade,
	constraint artist_genre_genre_fk
		foreign key (genre_id) references music_genre (genre_Id)
			on update cascade on delete cascade
)
;

create table if not exists nationality
(
	nationality_id int auto_increment
		primary key,
	nationality_name varchar(50) not null,
	constraint nationality_nationality_name_uindex
		unique (nationality_name)
)
;

create table if not exists passport_country
(
	passport_country_id int auto_increment
		primary key,
	passport_name varchar(50) not null,
	constraint passport_country_passport_name_uindex
		unique (passport_name)
)
;

create table if not exists artist_country
(
	artist_id int not null,
	country_id int not null,
	constraint artist_country__artist_fk
		foreign key (artist_id) references artist (artist_id)
			on update cascade on delete cascade,
	constraint artist_country_country__fk
		foreign key (country_id) references passport_country (passport_country_id)
			on update cascade on delete cascade
)
;

create table if not exists photo
(
	photo_id int auto_increment
		primary key,
	path varchar(255) null,
	visible tinyint(1) not null,
	content_type varchar(50) not null,
	name varchar(255) not null
)
;

create table if not exists artist_profile_photo
(
	artist_id int not null,
	photo_id int not null,
	constraint artist_profile_photo_artist_id_uindex
		unique (artist_id),
	constraint artist_profile_photo_artist_fk
		foreign key (artist_id) references artist (artist_id)
			on update cascade on delete cascade,
	constraint artist_profile_photo_photo_fk
		foreign key (photo_id) references photo (photo_id)
			on update cascade on delete cascade
)
;

create table if not exists profile
(
	profile_id int auto_increment
		primary key,
	first_name varchar(50) not null,
	middle_name varchar(50) null,
	last_name varchar(50) not null,
	email varchar(50) not null,
	password varchar(255) not null,
	birth_date date not null,
	gender varchar(20) not null,
	time_created timestamp default CURRENT_TIMESTAMP not null,
	soft_delete tinyint(1) default '0' not null,
	constraint profile_email_uindex
		unique (email)
)
;

create table if not exists artist_profile
(
	artist_id int not null,
	profile_id int null,
	constraint artist_profile__artist_fk
		foreign key (artist_id) references artist (artist_id)
			on update cascade on delete cascade,
	constraint artist_profile_profile__fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade
)
;

create table if not exists destination
(
	destination_id int auto_increment
		primary key,
	profile_id int not null,
	name varchar(255) not null,
	type varchar(255) not null,
	country varchar(255) not null,
	district varchar(255) null,
	latitude double null,
	longitude double null,
	visible tinyint(1) default '0' not null,
	soft_delete tinyint(1) default '0' not null,
	constraint destination_profile__fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade
)
;

create table if not exists destination_photo
(
	destination_photo_id int auto_increment
		primary key,
	profile_id int not null,
	photo_id int not null,
	destination_id int not null,
	constraint destination_photo_destination_destination_id_fk
		foreign key (destination_id) references destination (destination_id)
			on update cascade on delete cascade,
	constraint destination_photo_photo_photo_id_fk
		foreign key (photo_id) references photo (photo_id)
			on update cascade on delete cascade,
	constraint destination_photo_profile_profile_id_fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade
)
;

create table if not exists destination_request
(
	id int auto_increment,
	destination_Id int null,
	profile_Id int null,
	constraint destination_request_id_uindex
		unique (id),
	constraint destination_request_destination_destination_id_fk
		foreign key (destination_Id) references destination (destination_id)
			on update cascade on delete cascade,
	constraint destination_request_profile_profile_id_fk
		foreign key (profile_Id) references profile (profile_id)
			on update cascade on delete cascade
)
;

alter table destination_request
	add primary key (id)
;

create table if not exists events
(
	event_id int auto_increment,
	event_name varchar(255) not null,
	description varchar(255) null,
	destination_id int not null,
	start_date datetime null,
	end_date datetime null,
	age_restriction int null,
	soft_delete int default '0' null,
	ticket_price double default '-1' null,
	ticket_link varchar(500) null,
	constraint events_event_id_uindex
		unique (event_id),
	constraint destination_id_____destination_fk
		foreign key (destination_id) references destination (destination_id)
			on update cascade on delete cascade
)
;

alter table events
	add primary key (event_id)
;

create table if not exists attend_event
(
	attend_event_id int auto_increment
		primary key,
	event_id int not null,
	profile_id int not null,
	constraint attend_event_event_id___fk
		foreign key (event_id) references events (event_id)
			on update cascade on delete cascade,
	constraint attend_event_profile_id___fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade
)
;

create table if not exists event_artists
(
	artist_id int not null,
	event_id int not null,
	constraint artist_id_events_____artist_fk
		foreign key (artist_id) references artist (artist_id)
			on update cascade on delete cascade,
	constraint event_id_events_____events_fk
		foreign key (event_id) references events (event_id)
			on update cascade on delete cascade
)
;

create table if not exists event_genres
(
	event_id int not null,
	genre_id int not null,
	constraint event_id_genres_____events_fk
		foreign key (event_id) references events (event_id)
			on update cascade on delete cascade,
	constraint genre_id_____music_genre_fk
		foreign key (genre_id) references music_genre (genre_Id)
			on update cascade on delete cascade
)
;

create table if not exists event_photo
(
	event_id int not null,
	photo_id int not null,
	constraint event_photo_event_id_uindex
		unique (event_id),
	constraint event_photo_event_fk
		foreign key (event_id) references events (event_id)
			on update cascade on delete cascade,
	constraint event_photo_photo_fk
		foreign key (photo_id) references photo (photo_id)
			on update cascade on delete cascade
)
;

create table if not exists follow_artist
(
	artist_follow_id int auto_increment
		primary key,
	profile_id int not null,
	artist_id int not null,
	constraint follow_artist_ibfk_1
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade,
	constraint follow_artist_ibfk_2
		foreign key (artist_id) references artist (artist_id)
			on update cascade on delete cascade
)
;

create table if not exists follow_destination
(
	destination_follow_id int auto_increment
		primary key,
	profile_id int not null,
	destination_id int not null,
	constraint follow_destination_destination__fk
		foreign key (destination_id) references destination (destination_id)
			on update cascade on delete cascade,
	constraint follow_destination_profile__fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade
)
;

create table if not exists personal_photo
(
	personal_photo_id int auto_increment
		primary key,
	profile_id int not null,
	photo_id int not null,
	is_profile_photo tinyint(1) not null,
	constraint personal_photo__profile_id_fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade,
	constraint personal_photo_photo_photo_id_fk
		foreign key (photo_id) references photo (photo_id)
			on update cascade on delete cascade
)
;

create table if not exists profile_nationality
(
	profile_nationality_id int auto_increment
		primary key,
	profile int not null,
	nationality int not null,
	constraint profile_nationality_nationality_nationality_id_fk
		foreign key (nationality) references nationality (nationality_id)
			on update cascade on delete cascade,
	constraint profile_nationality_profile_profile_id_fk
		foreign key (profile) references profile (profile_id)
			on update cascade on delete cascade
)
;

create table if not exists profile_passport_country
(
	profile_passport_country_id int auto_increment
		primary key,
	profile int not null,
	passport_country int not null,
	constraint profile_passport_country_passport_country_passport_country_id_fk
		foreign key (passport_country) references passport_country (passport_country_id)
			on update cascade on delete cascade,
	constraint profile_passport_country_profile_id_fk
		foreign key (profile) references profile (profile_id)
			on update cascade on delete cascade
)
;

create table if not exists roles
(
	role_id int auto_increment,
	role_name varchar(128) null,
	constraint roles_role_id_uindex
		unique (role_id),
	constraint roles_role_name_uindex
		unique (role_name)
)
;

alter table roles
	add primary key (role_id)
;

create table if not exists profile_roles
(
	profile_role_id int auto_increment
		primary key,
	profile_id int not null,
	role_id int not null,
	constraint profile_roles_unique_pair
		unique (profile_id, role_id),
	constraint profile_roles_profile_profile_id_fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade,
	constraint profile_roles_roles_role_id_fk
		foreign key (role_id) references roles (role_id)
			on update cascade on delete cascade
)
;

create table if not exists thumbnail_link
(
	photo_id int default '0' not null,
	thumbnail_id int default '0' not null,
	primary key (photo_id, thumbnail_id),
	constraint photo_id_fk
		foreign key (photo_id) references photo (photo_id)
			on update cascade on delete cascade,
	constraint thumbnail_id
		foreign key (thumbnail_id) references photo (photo_id)
			on update cascade on delete cascade
)
;

create table if not exists traveller_type
(
	traveller_type_id int auto_increment
		primary key,
	traveller_type_name varchar(50) not null,
	constraint traveller_type_traveller_type_name_uindex
		unique (traveller_type_name)
)
;

create table if not exists destination_change
(
	id int auto_increment,
	traveller_type_id int not null,
	action tinyint not null,
	request_id int not null,
	constraint destination_changes_id_uindex
		unique (id),
	constraint destination_changes_destination_request_id_fk
		foreign key (request_id) references destination_request (id)
			on update cascade on delete cascade,
	constraint destination_changes_traveller_type_traveller_type_id_fk
		foreign key (traveller_type_id) references traveller_type (traveller_type_id)
			on update cascade on delete cascade
)
;

alter table destination_change
	add primary key (id)
;

create trigger RemoveRequestOnDeleteOfChange
after DELETE on destination_change
for each row
DELETE FROM destination_request
  WHERE NOT Exists (
              SELECT *
              FROM destination_change
              WHERE destination_change.request_id = destination_request.id
      )
;

create table if not exists destination_traveller_type
(
	id int auto_increment
		primary key,
	destination_id int not null,
	traveller_type_id int not null,
	constraint destination_traveller_type_destination_destination_id_fk
		foreign key (destination_id) references destination (destination_id)
			on update cascade on delete cascade,
	constraint destination_traveller_type_traveller_type_traveller_type_id_fk
		foreign key (traveller_type_id) references traveller_type (traveller_type_id)
			on update cascade on delete cascade
)
;

create table if not exists profile_traveller_type
(
	profile_traveller_type_id int auto_increment
		primary key,
	profile int not null,
	traveller_type int not null,
	constraint profile_traveller_type_profile_profile_id_fk
		foreign key (profile) references profile (profile_id)
			on update cascade on delete cascade,
	constraint profile_traveller_type_traveller_type_traveller_type_id_fk
		foreign key (traveller_type) references traveller_type (traveller_type_id)
			on update cascade on delete cascade
)
;

create table if not exists treasure_hunt
(
	treasure_hunt_id int auto_increment,
	profile_id int not null,
	destination_id int not null,
	riddle varchar(256) not null,
	start_date date not null,
	end_date date not null,
	soft_delete tinyint(1) default '0' not null,
	constraint treasure_hunt_treasure_hunt_id_uindex
		unique (treasure_hunt_id),
	constraint treasure_hunt_destination_fk
		foreign key (destination_id) references destination (destination_id)
			on update cascade on delete cascade,
	constraint treasure_hunt_profile_fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade
)
;

alter table treasure_hunt
	add primary key (treasure_hunt_id)
;

create table if not exists trip
(
	trip_id int auto_increment
		primary key,
	name varchar(255) not null,
	profile_id int not null,
	soft_delete tinyint(1) default '0' not null,
	constraint trip_profile_profile_id_fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade
)
;

create table if not exists trip_destination
(
	trip_destination_id int auto_increment
		primary key,
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
)
;

create table if not exists type_of_events
(
	type_id int auto_increment,
	type_name varchar(50) not null,
	constraint type_of_events_type_id_uindex
		unique (type_id)
)
;

alter table type_of_events
	add primary key (type_id)
;

create table if not exists event_type
(
	event_id int not null,
	type_id int not null,
	constraint event_id_____events_fk
		foreign key (event_id) references events (event_id)
			on update cascade on delete cascade,
	constraint type_id_____type_of_events_fk
		foreign key (type_id) references type_of_events (type_id)
			on update cascade on delete cascade
)
;

create table if not exists undo_stack
(
	entry_id int auto_increment
		primary key,
	item_type varchar(30) null,
	object_id int null,
	profile_id int null,
	time_created timestamp default CURRENT_TIMESTAMP not null,
	constraint undo_stack_profile_profile_id_fk
		foreign key (profile_id) references profile (profile_id)
			on update cascade on delete cascade
)
;