from readJSON import *


def get_trip_id(name, profile_id, cursor, db):
    """Is a helper function for execute_trips_query()
    Gets unique trip id for the trip name and profile_id"""
    print(name, profile_id)
    cursor.execute("SELECT trip_id from trip WHERE name = '{}' AND profile_id = '{}'".format(name, profile_id))
    db.commit()
    id = cursor.fetchone()
    if id is not None:
        return id[0]
    else:
        return 0


def get_profile_id(emails, cursor, db):
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


def execute_trip_dest_queries(destinations, trip_id, cursor, db):
    """For each destination the trip visits, will insert into the linking table the trip_id and destination_id"""
    for destination in destinations:
        try:
            print(destination)
            cursor.execute("INSERT INTO trip_destination (trip_id, destination_id) VALUES ('{0}', (SELECT destination_id from destination WHERE name = '{1}' AND visible = 1))".format(trip_id, destination))
            db.commit()
            print("Successfully inserted tripDestination")
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("Failed to insert tripDestination " + "ERROR: ", e)


def execute_trips_queries(cursor, db, number_trips, number_destinations):
    """Executes the rips query and calls helper functions to insert linking tables"""
    print("\n----------trips----------")
    if number_destinations < 2:
        print("Number of destinations inserted must be more than 2 to create a trip : ERROR")
        return "Error"
    trips_list = read_trips(number_trips, number_destinations)
    emails = read_profiles()[1]
    for trip in trips_list:
        profile_id = get_profile_id(emails, cursor, db)
        try:
            # see it trip already exists
            trip_exists = get_trip_id(trip[0], profile_id, cursor, db) != 0
            if trip_exists:
                print("\ntrip already exists")
            else:
                cursor.execute("INSERT INTO trip (name, profile_id) VALUES ('{}', '{}')".format(trip[0], profile_id))
                db.commit()
                trip_id = get_trip_id(trip[0], profile_id, cursor, db)
                print("\nTrip inserted successfully")
                print(execute_trip_dest_queries(trip[1], trip_id, cursor, db))
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("\nFailed to insert, rolling back ")
            print("ERROR: ", e)
