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
    """Executes event type queries to insert the event types into the linking table"""
    try:
        cursor.execute("SELECT event_id from event_type WHERE event_id = '{}'".format(event_id))
        db.commit()
        id = cursor.fetchone()[0]
        if id is not None:
            cursor.execute("INSERT INTO event_type (event_id, type_id) VALUES ('{}', '{}')".format(event_id, random.randint(1, 4)))
            db.commit()
    except Exception as e:
        db.rollback()
        print("\nError inserting event types")


def execute_event_genres_queries(event_id, cursor, db):
    """Executes event genre queries to insert the event genres into the linking table"""
    try:
        cursor.execute("SELECT event_id from event_genres WHERE event_id = '{}'".format(event_id))
        db.commit()
        id = cursor.fetchone()[0]
        if id is not None:
            cursor.execute("INSERT INTO event_genre (event_id, genre_id) VALUES ('{}', '{}')".format(event_id, random.randint(1, 20)))
            db.commit()
    except Exception as e:
        db.rollback()
        print("\nError inserting event genres")


def execute_event_artists_queries(event_id, artist_id, cursor, db):
    """Executes event artists queries to insert the event artists into the linking table"""
    if artist_id != 0:
        try:
            cursor.execute("SELECT event_id from event_artists WHERE event_id = '{}'".format(event_id))
            db.commit()
            id = cursor.fetchone()[0]
            cursor.execute("INSERT INTO event_artists (artist_id, event_id) VALUES ('{}', '{}')".format(artist_id, event_id))
            db.commt()
        except Exception as e:
            db.rollback()
            print("\nError inserting event artists")
    else:
        print("\nError inserting event artists: Cannot get artist id")


def get_artist_id(artists, cursor, db):
    """Gets an id of a random artist"""
    artist = artists[random.randint(0, len(artists) -1 )]
    try:
        cursor.execute("SELECT artist_id from artist WHERE artist_name = '{}'".format(artist[0]))
        db.commit()
        id = cursor.fetchone()[0]
        if id is not None:
            return id
        else:
            return 0


def execute_event_queries(cursor, db, number_events, number_artists, number_destinations):
    """Execute event queries to insert the events. Other functionality is called to insert additional information in
    linking tables"""
    print("\n---------Events---------")
    if number_events == 0:
        return "No events are inserted"
    events_list = read_events(number_events)
    if number_artists < 1:
        print("Number of artists inserted must be at least 1 to create an event : ERROR")
        return "Error"
    if number_destinations < 1:
        print("Number of destinations inserted must be at least 1 to create an event : ERROR")
        return "Error"
    artists = read_artists(number_artists)
    for event in events_list:
        try:
            if get_event_id(event[0], cursor, db) == 0:
                cursor.execute("INSERT INTO events (event_name, description, destination_id, start_date, end_date, age_restriction) VALUES ('{}', '{}')".format(event[0], event[1], event[2], event[3], event[4]))
                db.commit()
                event_id = get_event_id(event[0], cursor, db)
                execute_event_type_queries(event_id, cursor, db)
                execute_event_genres_queries(event_id, cursor, db)
                execute_event_artists_queries(event_id, artist_ids, cursor, db)
        except Exception as e:
            db.rollback()
            print("\nFailed to insert, rolling back")
            print("Error: ", e)