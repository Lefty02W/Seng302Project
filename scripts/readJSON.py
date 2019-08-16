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
    emails = []
    with open('profiles.JSON') as json_file:
        data = json.load(json_file)
        for i in range(2):
            email = data[i]['first_name'] + "." + data[i]['last_name'] + "@gmail.com"
            profile = []
            profile.append(data[i]['first_name'])
            profile.append(data[i]['middle_name'])
            profile.append(data[i]['last_name'])
            profile.append(email)
            profile.append(hash_password("password"))
            profile.append(get_date(data[i]['date_of_birth']))
            profile.append(data[i]['gender'])
            profile.append(data[i]['nationality'])
            profile.append(data[i]['passport_country'])
            profile.append(data[i]['traveller_type'])
            emails.append(email)
            profiles.append(profile)
    return profiles, emails


def public_or_private(destination_name):
    """Takes a destination name, searches for the destination in a list and assigns it a public value if is in list as
    it will be used by a trip"""
    public_destination_list = ["15 Mile Creek", "Adams Bank", "Ahimanawa", "Akapatiki Flat", "Alexandra Stream", "Also Gully", "Anawhenua Stream", "Annette Plateau", "Apakura Stream", "Arapaoa River Scenic Reserve", "Arch Hill", "Arnold River", "Ashbys Pit", "Auckland", "Awahoa Bay", "Awapuni", "Axis Spur", "Balcairn", "Banks Range", "Barron Saddle", "Baylys Beach Post Office", "Beelzebub Glacier", "Benford Creek", "Beta Creek", "Big Gully West Branch", "Birchalls Gully", "Blackcleugh Burn", "Black Ridge", "Dead Creek", "Deep Creek", "Demon Gap Icefall", "Devils Punchbowl", "Dillons Stream", "Dockeys Stream", "Donald Stream", "Doubtless Bay", "Driblet Creek", "Duck Creek", "Dunedin", "Earnslaw Park Recreation Reserve", "East Windward Island", "Eight Mile Creek or Close Burn", "Elms Creek", "Entrance Shoal", "Ethne Stream", "Flat Stream", "Flowers Creek", "Gentle Annie Saddle", "Gibsons Creek", "Glamour Torrent", "Gleniti", "Goat Creek", "Goldney Glacier", "Gorge Creek", "Gradwell", "Grants Stream", "Green Hill", "Hillcrest Creek", "Hinge Bay", "Hohonu Range", "Home Creek", "Hoopers Inlet", "Horokiri Wildlife Management Reserve", "Hospital Hill", "Huaero Stream", "Hukainui Point", "Hunter River", "Husband Creek", "Ihawhanui Pa", "Inverness Stream", "Island Hill Homestead", "Jack Creek", "Kaipaki", "Kaitane Stream", "Kaituna Stream", "Kakahi Stream", "Manganuku Stream", "Mangaongaonga Stream", "Mangapapa", "Moengawahine Stream", "Mokauiti", "Molesworth Stream", "Monument Harbour", "Moropunga Island", "Motu", "Motukauatirahi/Cass Bay", "Motungarara Island", "Moturoa Island (Tower Rock)", "Motu Wai Island (Red Island)", "Mount Ambrose", "Mount Beautiful", "Mount Calliope", "Mount Cook", "Pelorus Sound", "Perry Creek", "Piano Creek", "Puhikereru", "Pukeareinga", "Pukekarara", "Pukemiki", "Pukepoto Stream", "Puketapu Pa", "Rakis Table", "Ranger Spur", "Rangiriri West", "Rangiwaea Scenic Reserve", "Raroa Railway Station", "Raukumara Conservation Park", "Rawtor Creek", "Stony Creek", "Stranraer Hill", "Sugar Loaf", "Sunshine", "Swampy Stream", "Table Creek", "Taho Flats", "Taieri River Scenic Reserve", "Taitapu", "Takapurau", "Talus Creek", "Tangent Creek", "Tapanui Hill", "Tapui Stream", "Taranaki Point", "Tarere", "Tatapouri", "Taumarere", "Taunoa Stream", "Taurangatao Spur", "Tauwhare Stream", "Tawhiti", "Te Ahuahu", "Te Apu", "Te Awaatu Channel (The Gut) Marine Reserve", "Te Harua Stream", "Te Horo Marae", "Te Iwi Roa", "Te Karaka Point", "Te Koere Creek", "Te Kumi", "Te Mari", "Te More", "Te Nunuhe Rock", "Te Papiri Point", "Te Tarata Ridge"]
    if destination_name in public_destination_list:
        return 1
    else:
        return random.randint(0, 1)


def read_destinations():
    """Reads the file destinations.JSON and converts the json into a list of destinations
    Note: the profileId is decided randomly by a number between 1 and 2000 as there should be 2000 profiles inserted
    above"""
    destinations = []
    with open('destinations.JSON') as json_file:
        data = json.load(json_file)
        for i in range(2):
            destination = []
            destination.append(data[i]["name"])
            destination.append(data[i]["type"])
            destination.append(data[i]['country'])
            destination.append(data[i]['district'])
            destination.append(data[i]['crd_latitude'])
            destination.append(data[i]['crd_longitude'])
            destination.append(public_or_private(data[i]['name']))

            destinations.append(destination)
    destinations = add_traveller_type_to_destinations(destinations)
    return destinations


def add_traveller_type_to_destinations(destinations):
    switcher = {
        1: "gap year",
        2: "frequent weekender",
        3: "thrillSeeker",
        4: "groupie",
        5: "functional/business traveller",
        6: "holidaymaker",
        7: "backpacker"
    }
    for destination in destinations:
        random_traveller_type = switcher.get(random.randint(1, 7))
        destination.append(random_traveller_type)
    return destinations


# def create_profile_queries(profile):
#     """Takes in a list of profiles and uses the data to create a query"""
#     query = 'INSERT INTO profile(first_name, middle_name, last_name, email, password, birth_date, gender) VALUES '
#     if profile[1] is None:
#         profile[1] = ''
#     query += "(?, ?, ?, ?, ?, ?, ?)", (profile[0], profile[1], profile[2], profile[3], profile[4], profile[5],
#                                        profile[6])
#     query += ' WHERE NOT EXISTS (Select * from profile where (email = ?));'
#
#     # TODO: add insert statements for nationalities ect
#     return query


# def create_destination_queries(destinations):
#     """Takes in a list of destinations and uses the data to create a query"""
#     query = 'INSERT INTO destination(profile_id, name, type, country, district, latitude, longitude) VALUES '
#     for destination in destinations:
#         query += "('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}'),".format(destination[0], destination[1],
#                                                                              destination[2],
#                                                                              destination[3], destination[4],
#                                                                              destination[5],
#                                                                              destination[6])
#     new_query = query[0:-1]
#     new_query += ';'
#     return new_query


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