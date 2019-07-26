# --- !Ups
create table if not exists admin
(
	admin_id int auto_increment
		primary key,
	profile_id int not null,
	is_master tinyint(1) default '0' not null
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

create table if not exists traveller_type
(
	traveller_type_id int auto_increment
		primary key,
	traveller_type_name varchar(50) not null,
	constraint traveller_type_traveller_type_name_uindex
		unique (traveller_type_name)
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
		foreign key (profile_id) references profile (profile_id),
	constraint profile_roles_roles_role_id_fk
		foreign key (role_id) references roles (role_id)
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
		foreign key (destination_id) references destination (destination_id),
	constraint destination_photo_photo_photo_id_fk
		foreign key (photo_id) references photo (photo_id),
	constraint destination_photo_profile_profile_id_fk
		foreign key (profile_id) references profile (profile_id)
)
;


create table if not exists destination_request
(
  id             int auto_increment,
  destination_Id int null,
  profile_Id     int null,
  constraint destination_request_id_uindex
  unique (id),
  constraint destination_request_destination_destination_id_fk
  foreign key (destination_Id) references destination (destination_id),
  constraint destination_request_profile_profile_id_fk
  foreign key (profile_Id) references profile (profile_id)
)
;


create table if not exists destination_change
(
  id                int auto_increment,
  traveller_type_id int     not null,
  action            tinyint not null,
  request_id        int     not null,
  constraint destination_changes_id_uindex
  unique (id),
  constraint destination_changes_destination_request_id_fk
  foreign key (request_id) references destination_request (id),
  constraint destination_changes_traveller_type_traveller_type_id_fk
  foreign key (traveller_type_id) references traveller_type (traveller_type_id)
)
;


create table if not exists destination_traveller_type
(
  id                int auto_increment
    primary key,
  destination_id    int not null,
  traveller_type_id int not null,
  constraint destination_traveller_type_destination_destination_id_fk
  foreign key (destination_id) references destination (destination_id),
  constraint destination_traveller_type_traveller_type_traveller_type_id_fk
  foreign key (traveller_type_id) references traveller_type (traveller_type_id)
)
;

create table if not exists undo_stack
(
  entry_id   int auto_increment
    primary key,
  item_type  varchar(30) null,
  object_id  int         null,
  profile_id int         null,
  constraint undo_stack_profile_profile_id_fk
  foreign key (profile_id) references profile (profile_id)
);

create table treasure_hunt
(
	treasure_hunt_id int auto_increment,
	profile_id int null,
	destination_id int not null,
	riddle varchar(256) not null,
	start_date date not null,
	end_date date not null,
	soft_delete tinyint(1) default '0' not null,
	constraint treasure_hunt_treasure_hunt_id_uindex
		unique (treasure_hunt_id)
)
;

alter table treasure_hunt
	add primary key (treasure_hunt_id)
;




# --- !Downs

drop table if exists destination;

drop table if exists photo;

drop table if exists nationality;

drop table if exists passport_country;

drop table if exists profile;

drop table if exists traveller_types;

drop table if exists trip;

drop table if exists trip_destination;

drop table if exists photo;

drop table if exists personal_photo;

drop table if exists admin;

drop table if exists follow_destination;

drop table if exists profile_nationality;

drop table if exists profile_passport_country;

drop table if exists profile_traveller_type;

drop table if exists roles;

drop table if exists profile_roles;

drop table if exists destination_photo;

drop table if exists destination_request;

drop table if exists destination_change;

drop table if exists destination_traveller_type;

drop table if exists undo_stack;

drop table if exists treasure_hunt