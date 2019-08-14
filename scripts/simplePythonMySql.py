""" A script for exploring inserting data to a MySql database
    Requires the pymysql package, this can be installed by:
    pip install pymysql
"""

import pymysql
import getpass
import bcrypt
from datetime import date
import datetime

global db


def executeQuery(cursor, query):
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


def getVersion(cursor):
    """This is an exmaple function to demonstrate successful connection,
       simply prints the current database version
       @param cursor - The cursor object to use for executing queries
    """
    # execute SQL query using execute() method.
    cursor.execute("SELECT VERSION()")

    # Fetch a single row using fetchone() method.
    data = cursor.fetchone()
    print("Database version : %s " % data)


def hashPassword(password):
    SALT = "$2a$12$nODuNzk9U7Hrq6DgspSP4."
    return bcrypt.hashpw(password.encode("utf8"), SALT.encode("utf8")).decode("utf8")


def main():
    global db
    """Set up DB connection, cursor and queries"""


    password = getpass.getpass()  # Get password without echoing (showing) it on terminal

    # Open database connection
    db = pymysql.connect("mysql2.csse.canterbury.ac.nz", "seng302-team700", password, "seng302-2019-team700-test")

    # prepare a cursor object using cursor() method
    cursor = db.cursor()

    firstName = "python"
    middleName = "3"
    lastName = "script"
    email = "python@gmail.com"
    password = hashPassword("password")
    birthDate = datetime.datetime(1980, 10, 25, 17, 30)
    gender = "Male"
    timeCreate = date.today()
    query = "INSERT INTO profile(first_name, middle_name, last_name, email, password, birth_date, gender)" \
          + " VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}')".format(firstName, middleName, lastName, email, password, timeCreate, gender)

    print("QUERY")
    print(query)
    print("OVER")
    ###TODO
    # READ JSON

    # PARSE

    # FORMAT query

    # Execute query
    executeQuery(cursor, query)
    ### END TODO

    # getVersion(cursor) #Example output

    # disconnect from server
    db.close()

main()
