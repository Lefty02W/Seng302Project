# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

-- init script create procs
-- Inital script to create stored procedures etc for mysql platform
DROP PROCEDURE IF EXISTS usp_ebean_drop_foreign_keys;

delimiter $$
--
-- PROCEDURE: usp_ebean_drop_foreign_keys TABLE, COLUMN
-- deletes all constraints and foreign keys referring to TABLE.COLUMN
--
CREATE PROCEDURE usp_ebean_drop_foreign_keys(IN p_table_name VARCHAR(255), IN p_column_name VARCHAR(255))
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE c_fk_name CHAR(255);
  DECLARE curs CURSOR FOR SELECT CONSTRAINT_NAME from information_schema.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE() and TABLE_NAME = p_table_name and COLUMN_NAME = p_column_name
      AND REFERENCED_TABLE_NAME IS NOT NULL;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN curs;

  read_loop: LOOP
    FETCH curs INTO c_fk_name;
    IF done THEN
      LEAVE read_loop;
    END IF;
    SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' DROP FOREIGN KEY ', c_fk_name);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
  END LOOP;

  CLOSE curs;
END
$$

DROP PROCEDURE IF EXISTS usp_ebean_drop_column;

delimiter $$
--
-- PROCEDURE: usp_ebean_drop_column TABLE, COLUMN
-- deletes the column and ensures that all indices and constraints are dropped first
--
CREATE PROCEDURE usp_ebean_drop_column(IN p_table_name VARCHAR(255), IN p_column_name VARCHAR(255))
BEGIN
  CALL usp_ebean_drop_foreign_keys(p_table_name, p_column_name);
  SET @sql = CONCAT('ALTER TABLE ', p_table_name, ' DROP COLUMN ', p_column_name);
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
END
$$
create table destination (
  destination_id                integer auto_increment not null,
  user_id                       integer not null,
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
  birth_date                    datetime(6),
  gender                        varchar(255),
  passports                     varchar(255),
  nationalities                 varchar(255),
  traveller_types               varchar(255),
  time_created                  datetime(6),
  constraint pk_profile primary key (email)
);

create table trip (
  trip_id                       integer auto_increment not null,
  name                          varchar(255),
  user_id                       integer not null,
  constraint pk_trip primary key (trip_id)
);

create table trip_destination (
  trip_destination_id           integer auto_increment not null,
  destination                   varchar(255),
  arrival                       datetime(6),
  departure                     datetime(6),
  destination_id                integer not null,
  trip_id                       integer not null,
  constraint pk_trip_destination primary key (trip_destination_id)
);


# --- !Downs

drop table if exists destination;

drop table if exists profile;

drop table if exists trip;

drop table if exists trip_destination;

