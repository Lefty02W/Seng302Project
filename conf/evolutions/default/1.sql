# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table destination (
  destination_id                integer auto_increment not null,
  user_email                    varchar(255),
  name                          varchar(255),
  type                          varchar(255),
  country                       varchar(255),
  district                      varchar(255),
  latitude                      double not null,
  longitude                     double not null,
  visible                       integer not null,
  constraint pk_destination primary key (destination_id)
);

create table image (
  image_id                      integer auto_increment not null,
  email                         varchar(255),
  image                         blob,
  visible                       integer,
  content_type                  varchar(255),
  name                          varchar(255),
  crop_x                        integer not null,
  crop_y                        integer not null,
  crop_width                    integer not null,
  crop_height                   integer not null,
  is_profile_pic                integer,
  constraint pk_image primary key (image_id)
);

create table nationality (
  nationality_id                integer auto_increment not null,
  nationality_name              varchar(255),
  constraint pk_nationality primary key (nationality_id)
);

create table profile (
  email                         varchar(255) not null,
  first_name                    varchar(255),
  middle_name                   varchar(255),
  last_name                     varchar(255),
  password                      varchar(255),
  birth_date                    timestamp,
  gender                        varchar(255),
  passports                     varchar(255),
  nationalities                 varchar(255),
  traveller_types               varchar(255),
  admin                         boolean default false not null,
  time_created                  timestamp,
  constraint pk_profile primary key (email)
);

create table traveller_types (
  traveller_type_id             integer not null,
  traveller_type_name           varchar(255)
);

create table trip (
  trip_id                       integer auto_increment not null,
  name                          varchar(255),
  email                         varchar(255),
  constraint pk_trip primary key (trip_id)
);

create table trip_destination (
  trip_destination_id           integer auto_increment not null,
  arrival                       timestamp,
  departure                     timestamp,
  destination_id                integer not null,
  trip_id                       integer not null,
  dest_order                    integer not null,
  constraint pk_trip_destination primary key (trip_destination_id)
);


# --- !Downs

drop table if exists destination;

drop table if exists image;

drop table if exists nationality;

drop table if exists profile;

drop table if exists traveller_types;

drop table if exists trip;

drop table if exists trip_destination;

