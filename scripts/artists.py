from readJSON import *


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
                # case nationality is not already inserted into database
                cursor.execute("INSERT INTO passport_country(passport_name) VALUES ('{}')".format(country))
                db.commit()
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            return "Failed to get or insert country " + "ERROR: " + e

        try:
            cursor.execute("SELECT artist_id from artist WHERE artist_name = '{0}'".format(name))
            db.commit()
            id = cursor.fetchone()
    
            cursor.execute("SELECT artist_id from artist_country WHERE artist_id = '{0}'".format(id[0]))
            db.commit()
            exists = cursor.fetchone()
            if exists is None:
                # case is not already inserted
                cursor.execute(
                    "INSERT INTO artist_country (artist_id, country_id) VALUES ('{0}', '{1}')".format(id[0], result))
                db.commit()
                return "Successfully inserted artist country"
            else:
                return "Artist country already exists (query still successful)"
        except Exception as e:
            db.rollback()
            return "Failed to insert artist country " + "ERROR: " + str(e)


def execute_artist_queries(cursor, db):
    """Execute artist queries to insert the artist. Other functions called to 
    insert additional information in linking tables."""
    
    artist_list = read_artists()

    for artist in artist_list:
        try:
            cursor.execute("INSERT INTO artist(artist_name, biography, facebook_link, instagram_link, " \
                + "spotify_link, twitter_link, website_link, members) " + \
                "VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}')".format(artist[0], artist[1], artist[2],
                                                                                         artist[3], artist[4], artist[5],  artist[6],
                                                                                         artist[7]))
            db.commit()
            print("Artist(s) inserted successfully!\n")
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("Failed to insert, rolling back \n")
            print("ERROR: ", e)

        print(execute_artist_country_queries(artist[7], artist[0],cursor, db))
        # TODO: artist genre queries
        #execute_genre_queries(artist[8], artist[0], cursor, db)