from readJSON import *
import random


def get_event_id(event_name, cursor, db):
    """Search in the database to see if an event with that name already exists, return true if yes, false if not"""
    try:
        cursor.execute("SELECT event_id from events WHERE event_name = '{}'".format(event_name))
        db.commit()
        id = cursor.fetchone()[0]
        if id is not None:
            return id
        else:
            return 0
    except Exception as e:
        print("Error checking if event with name exists")
        return True


def execute_event_type_queries(event_id, cursor, db):
    """Executes event type queries to insert the event types into the database"""
    try:
        cursor.execute("SELECT event_id from event_type WHERE event_id = '{}'".format(event_id))
        db.commit()
        id = cursor.fetchone()[0]
        if id is not None:
            random.randint(1,4)
            cursor.execute("INSERT INTO event_type (event_id, type_id) VALUES ('{}', '{}')".format(event_id, random.randint(1, 4)))
            db.commit()
    except Exception as e:
        db.rollback()
        print("\nError inserting event types")



def execute_event_queries(cursor, db, number_events, number_artists, number_destinations):
    """Execute event queries to insert the events. Other functionality is called to insert additional information in
    linking tables"""
    print("\n---------Events---------")
    if number_events == 0:
        return "No events are inserted"
    events_list = read_events(number_events)
    if number_artists < 1:
        print("Number of artists inserted must be atleast 1 to create an event : ERROR")
        return "Error"
    if number_destinations < 1:
        print("Number of destinations inserted must be atleast 1 to create an event : ERROR")
        return "Error"
    artists = read_artists() # todo make this work
    for event in events_list:
        try:
            if get_event_id(event[0], cursor, db) == 0:
                cursor.execute("INSERT INTO events (event_name, description, destination_id, start_date, end_date, age_restriction) VALUES ('{}', '{}')".format(event[0], event[1], event[2], event[3], event[4]))
                db.commit()
                event_id = get_event_id(event[0], cursor, db)
                execute_event_type_queries(event_id, cursor, db)
                #insert event genres
                #insert event artists
        except Exception as e:
            db.rollback()
            print("\nFailed to insert, rolling back")
            print("Error: ", e)