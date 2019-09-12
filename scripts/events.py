from readJSON import *


def event_name_exists(event_name, cursor, db):
    """Search in the database to see if an event with that name already exists, return true if yes, false if not"""
    try:
        cursor.execute("SELECT event_name from events WHERE event_name = '{}'".format(event_name))
        db.commit()
        name = cursor.fetchone()[0]
        if name is not None:
            return True
        else:
            return False
    except Exception as e:
        print("Error checking if event with name exists")
        return True


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
            if event_name_exists(event[0], cursor, db):
                cursor.execute("INSERT INTO events (event_name, description, destination_id, start_date, end_date, age_restriction) VALUES ('{}', '{}')".format(event[0], event[1], event[2], event[3], event[4]))
                db.commit()
                #insert event type
                #insert event genres
                #insert event artists
        except Exception as e:
            db.rollback()
            print("\nFailed to insert, rolling back")
            print("Error: ", e)