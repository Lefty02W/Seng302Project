from readJSON import *

# profile queries
def execute_traveller_type_queries(traveller_type, email, cursor, db):
    """Helper function for execute profile queries
    will break up traveller types if more than one, then add linking tables to the traveller types"""
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
        # get profile
        cursor.execute("SELECT profile_id from profile WHERE email = '{0}'".format(email))
        db.commit()
        id = cursor.fetchone()
        # see if profile_traveller_type already exists
        cursor.execute("SELECT profile_traveller_type_id from profile_traveller_type WHERE profile = '{}'".format(id[0]))
        db.commit()
        if cursor.fetchone() is None:
            cursor.execute("INSERT INTO profile_traveller_type (profile, traveller_type) VALUES ('{}', '{}')".format(id[0], switcher.get(traveller_type)))
            db.commit()
            return "Successfully inserted Traveller Types"
        else:
            return "Traveller type already exists (query still successful)"
    except Exception as e:
        # Rollback in case there is any error
        db.rollback()
        return "Failed to insert traveller types ERROR: " + e


def execute_passports_queries(passport_country, email, cursor, db):
    """Helper function for execute profile queries
        Will check if the requested passport exists and then will insert a linking table between the passport and the
        profile. If didn't exist will create it"""
    try:
        cursor.execute("SELECT passport_country_id from passport_country WHERE passport_name = '{0}'".format(passport_country))
        db.commit()
        results = cursor.fetchall()
        if results != ():
            result = results[0][0]
        else:
            # case passport is not already inserted into database
            cursor.execute("INSERT INTO passport_country (passport_name) VALUES ('{}')".format(passport_country))
            db.commit()
            cursor.execute(
                "SELECT passport_country_id from passport_country WHERE passport_name = '{0}'".format(passport_country))
            db.commit()
            results = cursor.fetchall()
            result = results[0][0]
    except Exception as e:
        # Rollback in case there is any error
        db.rollback()
        return "Failed to get or insert nationality_id " + "ERROR: " + e

    try:
        cursor.execute("SELECT profile_id from profile WHERE email = '{0}'".format(email))
        db.commit()
        id = cursor.fetchone()

        cursor.execute("SELECT profile from profile_passport_country WHERE profile = '{0}'".format(id[0]))
        db.commit()
        exists = cursor.fetchone()
        if exists is None:
            # case is not already inserted
            cursor.execute(
                "INSERT INTO profile_passport_country (profile, passport_country) VALUES ('{0}', '{1}')".format(id[0], result))
            db.commit()
            return "successfully inserted passport"
        else:
            return "Passport already exists (query still successful)"
    except Exception as e:
        db.rollback()
        return "failed to insert passport " + "ERROR: " + e


def execute_nationalities_queries(nationality, email, cursor, db):
    """Helper function for execute profile queries
    Will check if the requested nationality exists and then will insert a linking table between the nationality and the
    profile. If didn't exist will create it"""
    try:
        cursor.execute("SELECT nationality_id from nationality WHERE nationality_name = '{0}'".format(nationality))
        db.commit()
        results = cursor.fetchall()
        if results != ():
            result = results[0][0]
        else:
            # case nationality is not already inserted into database
            cursor.execute("INSERT INTO nationality (nationality_name) VALUES ('{}')".format(nationality))
            db.commit()
            cursor.execute(
                "SELECT nationality_id from nationality WHERE nationality_name = '{0}'".format(nationality))
            db.commit()
            results = cursor.fetchall()
            result = results[0][0]
    except Exception as e:
        # Rollback in case there is any error
        db.rollback()
        return "Failed to get or insert nationality_id " + "ERROR: " + e

    try:
        cursor.execute("SELECT profile_id from profile WHERE email = '{0}'".format(email))
        db.commit()
        id = cursor.fetchone()

        cursor.execute("SELECT profile from profile_nationality WHERE profile = '{0}'".format(id[0]))
        db.commit()
        exists = cursor.fetchone()
        if exists is None:
            #case is not already inserted
            cursor.execute(
                "INSERT INTO profile_nationality (profile, nationality) VALUES ('{0}', '{1}')".format(id[0], result))
            db.commit()
            return "successfully inserted nationality"
        else:
            return "Nationality already exists (query still successful)"
    except Exception as e:
        db.rollback()
        return "failed to insert profile_nationality " + "ERROR: " + e


def execute_profile_queries(cursor, db):
    """Inserts the profile query to insert the profile and then calls functions to insert additional information in
    linking tables.
    Note: if profile already exists in table will not insert profile and result in an error which is handled"""
    profile_list = read_profiles()
    for profile in profile_list:
        if profile[1] is None:
            profile[1] = ''
        try:
            cursor.execute("INSERT INTO profile (first_name, middle_name, last_name, email, password, birth_date, gender)"
                           " VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}')".format(profile[0], profile[1],
                                                                                              profile[2], profile[3],
                                                                                              profile[4], profile[5],
                                                                                              profile[6]))
            db.commit()
            print("\nProfile inserted successfully!")
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("\nFailed to insert, rolling back ")
            print("ERROR: ", e)

        print(execute_nationalities_queries(profile[7], profile[3], cursor, db))
        print(execute_passports_queries(profile[8], profile[3], cursor, db))
        print(execute_traveller_type_queries(profile[9], profile[3], cursor, db))
