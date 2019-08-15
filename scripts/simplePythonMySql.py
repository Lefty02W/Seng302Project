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

global db


def get_version(cursor):
    """This is an exmaple function to demonstrate successful connection,
       simply prints the current database version
       @param cursor - The cursor object to use for executing queries
    """
    # execute SQL query using execute() method.
    cursor.execute("SELECT VERSION()")

    # Fetch a single row using fetchone() method.
    data = cursor.fetchone()
    print("Database version : %s " % data)


def execute_query(cursor, query):
    """Run a given query on the database. The database must already be set up
       and a connection established.
       @param cursor - The cursor object to execute database calls
       @param query - The query to execute
    """
    global db
    try:
        # Execute the SQL command
        cursor.execute(query)
        # Commit your changes in the database
        db.commit()
        print("Query executed successfully!")
    except Exception as e:
        # Rollback in case there is any error
        db.rollback()
        print("Failed to insert, rolling back\n")
        print("ERROR: ", e, "\n")


def main():
    global db
    """Set up DB connection, cursor and queries"""

    password = getpass.getpass()  # Get password without echoing (showing) it on terminal

    # Open database connection
    db = pymysql.connect("mysql2.csse.canterbury.ac.nz", "seng302-team700", password, "seng302-2019-team700-test")

    # prepare a cursor object using cursor() method
    cursor = db.cursor()

    # get queries
    # profile_list = read_profiles()
    # profile_query = create_profile_queries(profile_list)
    # print(profile_query)
    # destination_list = read_destinations()
    # destination_query = create_destination_queries(destination_list)
    # print(destination_query)

    # Execute query
    execute_query(cursor, destination_query)

    # disconnect from server
    db.close()


main()
