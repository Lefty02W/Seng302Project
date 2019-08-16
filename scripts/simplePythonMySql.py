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

    # queries
    execute_profile_queries(cursor, db)
    # execute_destination_queries(cursor, db)

    # destination_query = create_destination_queries(destination_list)
    # print(destination_query)

    # Execute query
    # execute_query(cursor, profile_query)

    # disconnect from server
    cursor.close()
    db.close()


main()
