""" A script for exploring inserting data to a MySql database
    Requires the pymysql package, this can be installed by:
    pip install pymysql
"""

import pymysql
import getpass


def executeQuery(cursor, query):
   """Run a given query on the database. The database must already be set up
      and a connection established.
      @param cursor - The cursor object to execute database calls
      @param query - The query to execute
   """
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
      print("ERROR: " + e + "\n")   


def getVersion(cursor):
   """This is an exmaple function to demonstrate successful connection,
      simply prints the current database version
      @param cursor - The cursor object to use for executing queries
   """
   # execute SQL query using execute() method.
   cursor.execute("SELECT VERSION()")
   
   # Fetch a single row using fetchone() method.
   data = cursor.fetchone()
   print ("Database version : %s " % data)
   
   
      
def main():
   """Set up DB connection, cursor and queries"""
   password = getpass.getpass() #Get password without echoing (showing) it on terminal
   
   # Open database connection
   db = pymysql.connect("mysql2.csse.canterbury.ac.nz","seng302-team700", password, "seng302-2019-team700-test" )
   
   # prepare a cursor object using cursor() method
   cursor = db.cursor()
   
   query = """INSERT INTO profile(first_name, middle_name, 
      last_name, email, password, birth_date, gender, time_created, soft_delete)
      VALUES ('{0}', '{1}', '{2}', '{3}', {4}, {5}, '{6}', {7}, {8})"""
   
   ###TODO 
   # READ JSON
   
   # PARSE
   
   # FORMAT query
   
   # Execute query
   #executeQuery(query)
   ### END TODO
   
   getVersion() #Example output
   
   # disconnect from server
   db.close()
