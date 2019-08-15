import json
import random
import bcrypt
from datetime import *


def hash_password(password):
    SALT = "$2a$12$nODuNzk9U7Hrq6DgspSP4."
    return bcrypt.hashpw(password.encode("utf8"), SALT.encode("utf8")).decode("utf8")


def get_date(json_string):
    date_parts = json_string.split('-')
    original_year = int(date_parts[2])
    if original_year <= (date.today().year - 2000):
        year = original_year + 2000
    else:
        year = original_year + 1900
    return date(year, int(date_parts[1]), int(date_parts[0]))


def read_profiles():
    """Reads the file profiles.JSON and converts the json into a list of profiles with the data"""
    profiles = []
    with open('profiles.JSON') as json_file:
        data = json.load(json_file)
        for i in range(2):
            profile = []
            profile.append(data[i]['first_name'])
            profile.append(data[i]['middle_name'])
            profile.append(data[i]['last_name'])
            profile.append(data[i]['first_name'] + "@gmail.com")
            profile.append(hash_password("password"))
            profile.append(get_date(data[i]['date_of_birth']))
            profile.append(data[i]['gender'])
            profile.append(data[i]['nationality'])
            profile.append(data[i]['passport_country'])
            profile.append(data[i]['traveller_type'])

            profiles.append(profile)
    return profiles


def read_destinations():
    """Reads the file destinations.JSON and converts the json into a list of destinations
    Note: the profileId is decided randomly by a number between 1 and 2000 as there should be 2000 profiles inserted
    above"""
    destinations = []
    with open('destinations.JSON') as json_file:
        data = json.load(json_file)
        for i in range(2):
            destination = []
            # TODO: change this so that we get the max profileId
            # random range would then be (max - numProfilesInserted, max)
            destination.append(random.randint(1, 2000))
            destination.append(data[i]["name"])
            destination.append(data[i]["type"])
            destination.append(data[i]['country'])
            destination.append(data[i]['district'])
            destination.append(data[i]['crd_latitude'])
            destination.append(data[i]['crd_longitude'])

            destinations.append(destination)
    return destinations


def create_profile_queries(profiles):
    """Takes in a list of profiles and uses the data to create a query"""
    query = 'INSERT INTO profile(first_name, middle_name, last_name, email, password, birth_date, gender) VALUES '
    for profile in profiles:
        if profile[1] is None:
            profile[1] = ''
        query += "('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}'),".format(profile[0], profile[1], profile[2],
                                                                           profile[3], profile[4], profile[5],
                                                                           profile[6])
    new_query = query[0:-1]
    new_query += ';'
    # TODO: add insert statements for nationalities ect
    return new_query


def create_destination_queries(destinations):
    """Takes in a list of destinations and uses the data to create a query"""
    query = 'INSERT INTO destination(profile_id, name, type, country, district, latitude, longitude) VALUES '
    for destination in destinations:
        query += "('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}'),".format(destination[0], destination[1],
                                                                             destination[2],
                                                                             destination[3], destination[4],
                                                                             destination[5],
                                                                             destination[6])
    new_query = query[0:-1]
    new_query += ';'
    return new_query


def read_artists():
    """Reads a JSON file holding artists and converts it to a list of artists"""
    artists = []
    with open('artists.JSON') as json_file:
        data = json.load(json_file)
        for i in range(2):
            artist = []
            artist.append(data[i]['artist_name'])
            artist.append(data[i]['biography'])
            artist.append(data[i]['facebook_link'])
            artist.append(data[i]['instagram_link'])
            artist.append(data[i]['spotify_link'])
            artist.append(data[i]['twitter_link'])
            artist.append(data[i]['website_link'])
            
            # Add members as comma separated string
            members_string = ""
            for member in data[i]['members']:
                members_string += member['name'] + ", "
            members_string = members_string[0:-2]
            artist.append(members_string)
            
            #Add list of countries
            countries = []
            for country in data[i]['countries']:
                countries.append(country['country'])
            artist.append(countries)
            
            # Add list of genres
            genres = []
            for genre in data[i]['genres']:
                genres.append(genre['genre'])
            artist.append(genres)            

            artists.append(artist)
    return artists    


def create_artist_queries(artists):
    """Takes in a list of artists to create a query"""
    query = 'INSERT INTO artist(artist_name, biography, facebook_link, instagram_link, ' \
        + 'spotify_link, twitter_link, website_link, members) VALUES '
    
    for artist in artists:
        query += "('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}', '{7}'),".format(artist[0], artist[1], artist[2],
                                                                           artist[3], artist[4], artist[5],
                                                                           artist[6], artist[7])
    query = query[0:-1]
    query += ';'
    # TODO: query for countries and genres
    return query        