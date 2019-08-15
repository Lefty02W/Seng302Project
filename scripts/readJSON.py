import json

with open('profiles.JSON') as json_file:
    data = json.load(json_file)
    print(len(data))

    for i in range(len(data)):
        first_name = data[i]['first_name']
        middle_name = data[i]['middle_name']
        last_name = data[i]['last_name']
        email = data[i]['first_name'] + "@gmail.com"
        password = "password"
        birth_date = data[i]['date_of_birth']
        gender = data[i]['gender']

        nationality = data[i]['nationality']
        passport_country = data[i]['passport_country']
        traveller_type = data[i]['traveller_type']
        query = "INSERT INTO profile(first_name, middle_name, last_name, email, password, birth_date, gender)" \
          + " VALUES ('{0}', '{1}', '{2}', '{3}', '{4}', '{5}', '{6}')".format(first_name, middle_name, last_name, email, password, birth_date, gender)
        print(query)

