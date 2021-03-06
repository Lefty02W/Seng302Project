from readJSON import *
import random


# destination queries
def get_destination_id(name, type, country, cursor, db):
    """Is a helper function for execute_destination_queries()
    Gets destination id by searching for the destination  name, type and country (as these features make it
    unique)"""
    cursor.execute("SELECT destination_id from destination WHERE name = '{}' AND type = '{}' AND country = '{}'".format(name, type, country))
    db.commit()
    id = cursor.fetchone()
    if id is not None:
        return id[0]
    else:
        return 0



def execute_traveller_types_queries(traveller_type, destination_id, cursor, db):
    """Is a helper function for execute_destination_queries()
    finds the destination_id and then inserts a link from the destination to the traveller type in
    destination_traveller_types provided it is not already inserted"""
    switcher = {
            "gap year": 1,
            "frequent weekender": 2,
            "thrillSeeker": 3,
            "groupie": 4,
            "functional/business traveller": 5,
            "holidaymaker": 6,
            "backpacker": 7
        }
    try:
        # see if destination_traveller_type already exists
        cursor.execute("SELECT id from destination_traveller_type WHERE destination_id = '{}'".format(destination_id))
        db.commit()
        if cursor.fetchone() is None:
            cursor.execute("INSERT INTO destination_traveller_type (destination_id, traveller_type_id) VALUES ('{}', '{}')".format(destination_id, switcher.get(traveller_type)))
            db.commit()
            return "Successfully inserted Traveller Types"
        else:
            return "Traveller type already exists (query still successful)"
    except Exception as e:
            # Rollback in case there is any error
        db.rollback()
        return "Failed to insert traveller types ERROR: " + e


def get_profile_id(cursor, db, emails):
    """Is a helper function for execute_destination_queries()
    Returns a profile id for the destination to be linked to"""
    email = random.choice(emails)
    try:
        cursor.execute("SELECT profile_id from profile WHERE email = '{0}'".format(email))
        db.commit()
        profile_id = cursor.fetchone()[0]
        return profile_id
    except Exception as e:
        db.rollback()
        print("failed to get max profile" + e)
        return 0


def execute_destination_queries(cursor, db, number_destinations, number_profiles):
    """Inserts the destination query to insert the destination and then calls functions to insert additional information
     in linking tables.
    Note: if destination already exists in table will not insert destination and result in an error which is
    handled"""
    print("\n----------Destinations----------")
    if number_destinations == 0:
        return "No destinations are inserted"
    destination_list = read_destinations(number_destinations)
    if number_profiles < 1:
        print("Number of profiles inserted must be at least 1 to create a destination : ERROR")
        return "Error"
    emails = read_profile_emails(number_profiles)
    for destination in destination_list:
        try:
            destination_exists = get_destination_id(destination[0], destination[1], destination[2], cursor, db) != 0
            if destination_exists:
                print("\nDestination already exists")
            else:
                cursor.execute(
                    "INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude, visible)"
                    " VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}')".format(get_profile_id(cursor, db, emails),
                                                                                       destination[0],
                                                                                       destination[1],
                                                                                       destination[2],
                                                                                       destination[3],
                                                                                       destination[4],
                                                                                       destination[5],
                                                                                       destination[6]))
                db.commit()
                print("\nDestination inserted successfully!")
                destination_id = get_destination_id(destination[0], destination[1], destination[2], cursor, db)
                print(execute_traveller_types_queries(destination[7], destination_id, cursor, db))
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("\nFailed to insert, rolling back ")
            print("ERROR: ", e)
