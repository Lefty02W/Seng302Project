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
    # Set the number of each data to be inserted, max is length of json around 2000 each roughly - doesnt matter if
    # exceed this number. Set to true if you want all data to be inserted, set to 0 if you don't want any
    number_profiles = 0
    number_destinations = 0
    number_artists = 0
    number_trips = True

    execute_profile_queries(cursor, db, number_profiles)
    execute_destination_queries(cursor, db, number_destinations)
    execute_artist_queries(cursor, db, number_artists)
    execute_trips_queries(cursor, db, number_trips, number_destinations)

    # disconnect from server
    cursor.close()
    db.close()


main()
