import json


def read_profiles():
    """Reads the file profiles.JSON and converts the json into profile insert statements with the data"""
    with open('profiles.JSON') as json_file:
        data = json.load(json_file)

        for i in range(len(data)):
            first_name = data[i]['first_name']
            middle_name = data[i]['middle_name']
            last_name = data[i]['last_name']
            email = data[i]['first_name'] + "@gmail.com"
            password = "password"
            birth_date = data[i]['date_of_birth']
            gender = data[i]['gender']

            # todo, find way to get relevant profileId and insert these, could use data[i]['id'], even if corresponds
            # to wrong profile
            nationality = data[i]['nationality']
            passport_country = data[i]['passport_country']
            traveller_type = data[i]['traveller_type']

            query = "INSERT INTO profile(first_name, middle_name, last_name, email, password, birth_date, gender)" \
              + " VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}')".format(first_name, middle_name, last_name, email, password, birth_date, gender)
            print(query)


def read_destinations():
    """Reads the file destinations.JSON and converts the json into destination insert statements with the data
    Note: the profileId is decided randomly by a number between 1 and 2000 as there should be 2000 profiles inserted
    above"""
    with open('destinations.JSON') as json_file:
        data = json.load(json_file)

        for i in range(len(data)):
            profile_id = random.randint(1, 2000)
            name = data[i]["name"]
            type = data[i]["type"]
            district = data[i]['district']
            latitude = data[i]['crd_latitude']
            longitude = data[i]['crd_longitude']
            country = data[i]['country']

            query = "INSERT INTO destination(profile_id, name, type, country, district, latitude, longitude)"\
                + "VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}')".format(profile_id, name, type, country, district, latitude, longitude)
            print(query)


# read_profiles()
read_destinations()
