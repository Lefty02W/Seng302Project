# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  first_name                    varchar(255),
  middle_name                   varchar(255),
  last_name                     varchar(255),
  email                         varchar(255),
  password                      varchar(255),
  birth_date                    timestamp,
  gender                        varchar(255),
  date_of_birth                 varchar(255),
  nationality                   varchar(255),
  passport_country              varchar(255),
  traveller_type                varchar(255)
);


# --- !Downs

drop table if exists user;

