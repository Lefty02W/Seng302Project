from readJSON import *

def get_destination_id(cursor, db, destName):
    """Is a helper function for insert_treasure_hunts()
    Returns a destination id for the treasure hunt to be linked to"""
    try:
        cursor.execute("SELECT destination_id from destination WHERE name = '{0}'".format(destName))
        db.commit()
        profile_id = cursor.fetchone()[0]
        return profile_id
    except Exception as e:
        db.rollback()
        print("failed to get destination for treasure hunt " + e)
        return 0

def get_profile_id(cursor, db, emails):
    """Is a helper function for insert_treasure_hunts()
    Returns a profile id for the treasure hunt to be linked to"""
    email = random.choice(emails)
    try:
        cursor.execute("SELECT profile_id from profile WHERE email = '{0}'".format(email))
        db.commit()
        profile_id = cursor.fetchone()[0]
        return profile_id
    except Exception as e:
        db.rollback()
        print("failed to get random profile for treasure hunt " + e)
        return 0

def get_hunt_id(riddle, destination, cursor, db):
    """Is a helper function for insert_treasure_hunts()
    Gets hunt id by searching for the riddle"""
    cursor.execute("SELECT riddle from treasure_hunt WHERE riddle = '{0}' and destinations = (SELECT destination_id from destination WHERE name = '{1}')".format(riddle, destination))
    db.commit()
    id = cursor.fetchone()
    if id is not None:
        return id[0]
    else:
        return 0

def insert_treasure_hunts(cursor, db, num_hunts, number_profiles):
    print("\n----------Treasure Hunts----------")
    if num_hunts == 0:
        return "No treasure hunts are inserted"
    hunt_list = read_treasure_hunts(num_hunts)
    if number_profiles < 1:
        print("Number of profiles inserted must be at least 1 to create a artist : ERROR")
        return "Error"
    emails = read_profile_emails(number_profiles)
    for hunt in hunt_list:
        try:
            if get_hunt_id(hunt[0], hunt[3], cursor, db) != 0:
                print("\nTreasure Hunt '"+hunt[0]+"' already exists")
            else:
                cursor.execute("INSERT INTO treasure_hunt(riddle, start_date, end_date, destination_id, " \
                               + "profile_id) VALUES ('{0}', (select NOW()), '2020-09-01', '{1}', '{2}')".format(hunt[0],
                                                                                                 get_destination_id(cursor, db, hunt[3]),
                                                                                                 get_profile_id(cursor, db, emails)))
                db.commit()
                print("\nTreasure Hunt inserted successfully")
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("Failed to insert, rolling back \n")
            print("ERROR: ", e)
