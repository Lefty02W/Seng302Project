from readJSON import *

def execute_traveller_types_queries(traveller_type, destination, cursor, db):
    """finds the destination_id and then inserts a link from the destination to the traveller type in
    destination_traveller_types provided it is not already inserted"""


def get_profile_id():
    """Returns a profile id for the destination to be linked to"""
    # TODO: change this so that we get the max profileId
    # random range would then be (max - numProfilesInserted, max)
    return 49 # atm 49 is a valid profile - temp fix to test functionality

# destination queries
def execute_destination_queries(cursor, db):
    """Inserts the destination query to insert the destination and then calls functions to insert additional information
     in linking tables.
    Note: if destination already exists in table will not insert destination and result in an error which is
    handled"""
    destination_list = read_destinations()
    for destination in destination_list:
        # TODO: check for duplicates
        try:
            cursor.execute("INSERT INTO destination (profile_id, name, type, country, district, latitude, longitude)"
                           " VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}')".format(get_profile_id(),
                                                                                              destination[0],
                                                                                              destination[1],
                                                                                              destination[2],
                                                                                              destination[3],
                                                                                              destination[4],
                                                                                              destination[5]))
            db.commit()
            print("\nDestination inserted successfully!\n")
        except Exception as e:
            # Rollback in case there is any error
            db.rollback()
            print("\nFailed to insert, rolling back ")
            print("ERROR: ", e)
