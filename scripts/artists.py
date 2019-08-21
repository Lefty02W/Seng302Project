from readJSON import *


def get_artist_id(name, cursor, db):
    """Is a helper function for execute_artist_queries()
    Gets artist id by searching for the artist  name"""
    cursor.execute("SELECT artist_id from artist WHERE artist_name = '{}'".format(name))
    db.commit()
    id = cursor.fetchone()
    if id is not None:
        return id[0]
    else:
        return 0


def execute_artist_country_queries(countries, name, cursor, db):
    """Helper function for artist queries. Inserts the artist countries into 
    linking table. If the country is not in the database, it will be added"""
    
    for country in countries:
        try:
            cursor.execute("SELECT passport_country_id from passport_country WHERE passport_name = '{0}'".format(country))
            db.commit()
            results = cursor.fetchall()
            if results != ():
                result = results[0][0]
            else:
                # country is not already inserted into database
                cursor.execute("INSERT INTO passport_country(passport_name) VALUES ('{}')".format(country))
                db.commit()
                results = cursor.fetchall()
                if results != ():
                    result = results[0][0]
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("Failed to get or insert country " + "ERROR: " + str(e))
            return

        try:
            cursor.execute("SELECT artist_id from artist WHERE artist_name = '{0}'".format(name))
            db.commit()
            id = cursor.fetchone()
    
            cursor.execute("SELECT artist_id from artist_country WHERE artist_id = '{0}'".format(id[0]))
            db.commit()
            exists = cursor.fetchone()
            if exists is None:
                # Not already inserted
                cursor.execute(
                    "INSERT INTO artist_country (artist_id, country_id) VALUES ('{0}', '{1}')".format(id[0], result))
                db.commit()
                print("Successfully inserted artist country")
            else:
                print("Artist country already exists (query still successful)")
        except Exception as e:
            db.rollback()
            print("Failed to insert artist country " + "ERROR: " + str(e))
        

def get_profile_id(cursor, db, emails):
    """Is a helper function for execute_artist_queries()
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


def execute_genre_queries(genres, name, cursor, db):
    """Helper function for inserting artists. Adds artist's genres to database.
    Genre added to database if it does not exist"""
    for genre in genres:
        try:
            cursor.execute("SELECT genre_id from music_genre WHERE genre = '{0}'".format(genre))
            db.commit()
            results = cursor.fetchall()
            if results != ():
                result = results[0][0]
            else:
                # Genre not in database
                cursor.execute("INSERT INTO music_genre(genre) VALUES ('{}')".format(genre))
                db.commit()
        except Exception as e:
            # Rollback on error
            db.rollback()
            print("Failed to get or insert genre " + "ERROR: " + str(e))
            return

        try:
            cursor.execute("SELECT artist_id from artist WHERE artist_name = '{0}'".format(name))
            db.commit()
            id = cursor.fetchone()
    
            cursor.execute("SELECT artist_id from artist_genre WHERE artist_id = '{0}'".format(id[0]))
            db.commit()
            exists = cursor.fetchone()
            if exists is None:
                # case is not already inserted
                cursor.execute(
                    "INSERT INTO artist_genre (artist_id, genre_id) VALUES ('{0}', '{1}')".format(id[0], result))
                db.commit()
                print("Successfully inserted artist genre")
            else:
                print("Artist genre already exists (query still successful)")
        except Exception as e:
            db.rollback()
            print("Failed to insert artist genre " + "ERROR: " + str(e))


def execute_create_artist_profile(cursor, db, name, profile_id):
    """A helper function for execute_artist_queries, adds an artist_profile"""
    try:
        cursor.execute("SELECT artist_id from artist_profile WHERE artist_id = (SELECT artist_id from artist WHERE artist_name = '{0}') and profile_id = '{1}'".format(name, profile_id))
        db.commit()
        id = cursor.fetchone()
        if id is not None:
            print("Artist_profile already exists")
        else:
            cursor.execute("INSERT INTO artist_profile (artist_id, profile_id) VALUES ((SELECT artist_id from artist WHERE artist_name = '{0}'), '{1}')".format(name, profile_id))
            db.commit()
            print("Successfully inserted artist profile")
    except Exception as e:
        db.rollback()
        print("Failed to insert artist profile ERROR: " + str(e))


def execute_artist_queries(cursor, db, number_artists, number_profiles):
    """Execute artist queries to insert the artist. Other functions called to 
    insert additional information in linking tables."""
    print("\n----------Artists----------")
    artist_list = read_artists(number_artists)
    if number_profiles < 1:
        print("Number of profiles inserted must be at least 1 to create a artist : ERROR")
        return "Error"
    emails = read_profile_emails(number_profiles)
    for artist in artist_list:
        try:
            artist_exists = get_artist_id(artist[0], cursor, db) != 0
            if artist_exists:
                print("\nArtist already exists")
            else:
                cursor.execute("INSERT INTO artist(artist_name, biography, facebook_link, instagram_link, " \
                    + "spotify_link, twitter_link, website_link, verified,  members) " + \
                    "VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}', '{8}')".format(artist[0], artist[1], artist[2],
                                                                                             artist[3], artist[4], artist[5],  artist[6],
                                                                                             1, artist[7]))
                db.commit()
                print("\nArtist inserted successfully!")
                execute_create_artist_profile(cursor, db, artist[0], get_profile_id(cursor, db, emails))
                execute_artist_country_queries(artist[8], artist[0], cursor, db)
                execute_genre_queries(artist[9], artist[0], cursor, db)
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("Failed to insert, rolling back \n")
            print("ERROR: ", e)

