""" A script for exploring inserting data to a MySql database
    Requires the pymysql package, this can be installed by:
    pip install pymysql
"""

import pymysql
import getpass
import bcrypt
from datetime import date
import datetime
from readJSON import *
from profiles import *
from destinations import *
from artists import *
from trips import *
from treasureHunts import *

global db


def add_artists(cursor):
    """Add artists to database"""
    artists = read_artists()
    query = create_artist_queries(artists)
    execute_query(cursor, query)


def main():
    global db
    """Set up DB connection, cursor and queries"""

    password = getpass.getpass()  # Get password without echoing (showing) it on terminal

    # Open database connection
    db = pymysql.connect("mysql2.csse.canterbury.ac.nz", "seng302-team700", password, "seng302-2019-team700-test")
    # prepare a cursor object using cursor() method
    cursor = db.cursor()

    # -------------queries-------------
    # Set the number of each data to be inserted, max is length of json around 2000 each roughly - if you exceed this
    # number all data will be inserted. Set to true if you want all data to be inserted, set to 0 if you don't want any
    # profiles = 2000
    # destinations = 47104
    # artists = 600
    # trips = 500
    # treasure hunts = 500
    number_profiles = 1000
    number_destinations = 4000
    number_artists = True
    number_trips = True
    number_treasure_hunts = 0

    db_profiles = pymysql.connect("mysql2.csse.canterbury.ac.nz", "seng302-team700", password, "seng302-2019-team700-test")
    cursor_profiles = db.cursor()
    execute_profile_queries(cursor_profiles, db_profiles, number_profiles)

    db_destinations = pymysql.connect("mysql2.csse.canterbury.ac.nz", "seng302-team700", password, "seng302-2019-team700-test")
    cursor_destinations = db.cursor()
    execute_destination_queries(cursor_destinations, db_destinations, number_destinations, number_profiles)

    db_artists = pymysql.connect("mysql2.csse.canterbury.ac.nz", "seng302-team700", password,
                                      "seng302-2019-team700-test")
    cursor_artists = db.cursor()
    execute_artist_queries(cursor_artists, db_artists, number_artists, number_profiles)

    db_trips = pymysql.connect("mysql2.csse.canterbury.ac.nz", "seng302-team700", password,
                                      "seng302-2019-team700-test")
    cursor_trips = db.cursor()
    execute_trips_queries(cursor_trips, db_trips, number_trips, number_destinations, number_profiles)

    db_treasure_hunts = pymysql.connect("mysql2.csse.canterbury.ac.nz", "seng302-team700", password,
                                      "seng302-2019-team700-test")
    cursor_treasure_hunts = db.cursor()
    insert_treasure_hunts(cursor_treasure_hunts, db_treasure_hunts, number_treasure_hunts, number_profiles, number_destinations)

    # disconnect from server
    cursor.close()
    db.close()


main()
