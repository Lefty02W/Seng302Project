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


def read_profiles(number_profiles):
    """Reads the file profiles.JSON and converts the json into a list of profiles with the data"""
    profiles = []
    emails = []
    with open('profiles.JSON') as json_file:
        data = json.load(json_file)
        if number_profiles is True:
            number_profiles = len(data)
        elif number_profiles > len(data):
            number_profiles = len(data)
        for i in range(number_profiles):
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
            #print(i, " out of " + range(number_profiles))
    return profiles


def read_profile_emails(number_profiles):
    """Reads the profiles file to get just the emails of the profiles"""
    profile_emails = []
    with open("profiles.JSON") as json_file:
        data = json.load(json_file)
        if number_profiles is True:
            number_profiles = len(data)
        elif number_profiles > len(data):
            number_profiles = len(data)
        for i in range(number_profiles):
            profile_emails.append(data[i]['first_name'] + "." + data[i]['last_name'] + "@gmail.com")
    return profile_emails


def public_or_private(destination_name):
    """Takes a destination name, searches for the destination in a list and assigns it a public value if is in list as
    it will be used by a trip"""
    public_destination_list = ["15 Mile Creek", "Adams Bank", "Ahimanawa", "Akapatiki Flat", "Alexandra Stream", "Also Gully", "Anawhenua Stream", "Annette Plateau", "Apakura Stream", "Arapaoa River Scenic Reserve", "Arch Hill", "Arnold River", "Ashbys Pit", "Auckland", "Awahoa Bay", "Awapuni", "Axis Spur", "Balcairn", "Banks Range", "Barron Saddle", "Baylys Beach Post Office", "Beelzebub Glacier", "Benford Creek", "Beta Creek", "Big Gully West Branch", "Birchalls Gully", "Blackcleugh Burn", "Black Ridge", "Dead Creek", "Deep Creek", "Demon Gap Icefall", "Devils Punchbowl", "Dillons Stream", "Dockeys Stream", "Donald Stream", "Doubtless Bay", "Driblet Creek", "Duck Creek", "Dunedin", "Earnslaw Park Recreation Reserve", "East Windward Island", "Eight Mile Creek or Close Burn", "Elms Creek", "Entrance Shoal", "Ethne Stream", "Flat Stream", "Flowers Creek", "Gentle Annie Saddle", "Gibsons Creek", "Glamour Torrent", "Gleniti", "Goat Creek", "Goldney Glacier", "Gorge Creek", "Gradwell", "Grants Stream", "Green Hill", "Hillcrest Creek", "Hinge Bay", "Hohonu Range", "Home Creek", "Hoopers Inlet", "Horokiri Wildlife Management Reserve", "Hospital Hill", "Huaero Stream", "Hukainui Point", "Hunter River", "Husband Creek", "Ihawhanui Pa", "Inverness Stream", "Island Hill Homestead", "Jack Creek", "Kaipaki", "Kaitane Stream", "Kaituna Stream", "Kakahi Stream", "Manganuku Stream", "Mangaongaonga Stream", "Mangapapa", "Moengawahine Stream", "Mokauiti", "Molesworth Stream", "Monument Harbour", "Moropunga Island", "Motu", "Motukauatirahi/Cass Bay", "Motungarara Island", "Moturoa Island (Tower Rock)", "Motu Wai Island (Red Island)", "Mount Ambrose", "Mount Beautiful", "Mount Calliope", "Mount Cook", "Pelorus Sound", "Perry Creek", "Piano Creek", "Puhikereru", "Pukeareinga", "Pukekarara", "Pukemiki", "Pukepoto Stream", "Puketapu Pa", "Rakis Table", "Ranger Spur", "Rangiriri West", "Rangiwaea Scenic Reserve", "Raroa Railway Station", "Raukumara Conservation Park", "Rawtor Creek", "Stony Creek", "Stranraer Hill", "Sugar Loaf", "Sunshine", "Swampy Stream", "Table Creek", "Taho Flats", "Taieri River Scenic Reserve", "Taitapu", "Takapurau", "Talus Creek", "Tangent Creek", "Tapanui Hill", "Tapui Stream", "Taranaki Point", "Tarere", "Tatapouri", "Taumarere", "Taunoa Stream", "Taurangatao Spur", "Tauwhare Stream", "Tawhiti", "Te Ahuahu", "Te Apu", "Te Awaatu Channel (The Gut) Marine Reserve", "Te Harua Stream", "Te Horo Marae", "Te Iwi Roa", "Te Karaka Point", "Te Koere Creek", "Te Kumi", "Te Mari", "Te More", "Te Nunuhe Rock", "Te Papiri Point", "Te Tarata Ridge"]
    if destination_name in public_destination_list:
        return 1
    else:
        return 1


def read_destinations(number_destinations):
    """Reads the file destinations.JSON and converts the json into a list of destinations"""
    destinations = []
    with open('destinations.JSON') as json_file:
        data = json.load(json_file)
        if number_destinations is True:
            number_destinations = len(data)
        elif number_destinations > len(data):
            number_destinations = len(data)
        for i in range(number_destinations):
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


def read_destination_names(number_destinations):
    """Reads the destinations file to get just the names of the destinations"""
    destination_names = []
    with open("destinations.JSON") as json_file:
        data = json.load(json_file)
        if number_destinations is True:
            number_destinations = len(data)
        elif number_destinations > len(data):
            number_destinations = len(data)
        for i in range(number_destinations):
            destination_names.append(data[i]['name'])
    return destination_names


def select_2random_destinations(destination_names):
    """Returns 2 randomly selected destinations from the list that are not the same as each other"""
    dest = random.randint(1, len(destination_names))
    dest2 = dest
    while dest2 == dest:
        dest2 = random.randint(1, len(destination_names))
    return [destination_names[dest-1], destination_names[dest2-1]]


def read_trips(number_trips, number_destinations):
    """Reads the file trips.JSON and converts the json into a list of trips"""
    trips = []

    destination_names = read_destination_names(number_destinations)
    with open('trips.JSON') as json_file:
        data = json.load(json_file)
        if number_trips is True:
            number_trips = len(data)
        elif number_trips > len(data):
            number_trips = len(data)
        for i in range(number_trips):
            trip = []
            destination_name_add = []
            trip.append(data[i]['trip_name'])
            destination_name_add = select_2random_destinations(destination_names)
            trip.append(destination_name_add)
            trips.append(trip)
    return trips

def read_treasure_hunts(number_hunts, number_destinations):
    """Reads the file treasureHunts.JSON and converts the json into a list of hunts"""
    hunts = []
    destination_names = read_destination_names(number_destinations)
    with open('treasureHunts.JSON') as json_file:
        data = json.load(json_file)
        if number_hunts is True:
            number_hunts = len(data)
        elif number_hunts > len(data):
            number_hunts = len(data)
        for i in range(number_hunts):
            hunt = []
            hunt.append(data[i]['riddle'])
            hunt.append(data[i]['start_date'])
            hunt.append(data[i]['end_date'])
            hunt.append(destination_names[random.randint(1, len(destination_names)) - 1])
            hunts.append(hunt)
    return hunts



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


def read_artists(number_artists):
    """Reads a JSON file holding artists and converts it to a list of artists"""
    artists = []
    with open('artists.JSON') as json_file:
        data = json.load(json_file)
        if number_artists is True:
            number_artists = len(data)
        elif number_artists > len(data):
            number_artists = len(data)
        for i in range(number_artists):
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