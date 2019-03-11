# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table destination (
  destination_id                integer auto_increment not null,
  member_email                  varchar(255),
  name                          varchar(255),
  type                          varchar(255),
  country                       varchar(255),
  district                      varchar(255),
  latitude                      double not null,
  longitude                     double not null,
  constraint pk_destination primary key (destination_id)
);

create table profile (
  email                         varchar(255) not null,
  first_name                    varchar(255),
  middle_name                   varchar(255),
  last_name                     varchar(255),
  password                      varchar(255),
  birth_date                    timestamp,
  passports                     varchar(255),
  gender                        varchar(255),
  nationality                   varchar(255),
  time_created                  timestamp,
  groupie                       boolean default false not null,
  thrillseeker                  boolean default false not null,
  gap_year                      boolean default false not null,
  weekender                     boolean default false not null,
  holidaymaker                  boolean default false not null,
  business                      boolean default false not null,
  backpacker                    boolean default false not null,
  constraint pk_profile primary key (email)
);


# --- !Downs

drop table if exists destination;

drop table if exists profile;

