#!/usr/bin/python

import sqlite3
import argparse
import zipfile
import os

def main():
    args = parse_args()

    if not args.d:
        extract_dataset(args.dataset)
        dataset_name = os.path.splitext(os.path.basename(args.dataset))[0]
        if args.mode == '10M':
            args.dataset = 'ml-10M100K'
        else:
            args.dataset = dataset_name

    if not args.database:
        args.database = os.path.join(args.dataset, 'movielens.db')

    if args.mode == '100K':
        generate_database_100K(args.dataset, args.database)
    elif args.mode == '1M':
        generate_database_1M(args.dataset, args.database)
    elif args.mode == '10M':
        generate_database_10M(args.dataset, args.database)
    else:
        print('Other dataset formats not implemented yet. Stick to 100K, 1M or 10M')


def parse_args():
    parser = argparse.ArgumentParser(description='Create database from MovieLens dataset')
    parser.add_argument('dataset', help='Zip-file containing the dataset')
    parser.add_argument('database', nargs='?', help='The file in which to store the database. Defaults to "movielens.db", in the extracted directory')
    parser.add_argument('-d', '--d', action='store_true', help='Interpret provided dataset as a folder where data has already been extracted instead of as a zip')
    parser.add_argument('--mode', choices=['100K', '1M', '10M', '20M'], default='100K', help='Specify which format the dataset is in')

    args = parser.parse_args()
    return args


def extract_dataset(dataset):
    with zipfile.ZipFile(dataset, 'r') as datazip:
        datazip.extractall()


def generate_database_100K(dataset, database):
    if os.path.isfile(database):
        os.remove(database)

    conn = sqlite3.connect(database)
    conn.text_factory = str
    c = conn.cursor()

    create_tables(c)

    occupation_map = parse_occupation_data_100K(dataset, c)
    parse_movie_data_100K(dataset, c)
    parse_user_data_100K(dataset, c, occupation_map)
    parse_rating_data_100K(dataset, c)

    conn.commit()
    conn.close()

def parse_occupation_data_100K(dataset, c):
    occupation_map = {}
    with open(os.path.join(dataset, 'u.occupation'), 'r') as occFile:
        for line in occFile:
            if line.rstrip():
                occupation = line.rstrip()
                occupation_map[occupation] = c.lastrowid

    return occupation_map

def parse_movie_data_100K(dataset, c):
    insert_movie_query = '''INSERT INTO movies VALUES
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)'''
    with open(os.path.join(dataset, 'u.item'), 'r') as movieFile:
        for line in movieFile:
            movie_data = [0]*22
            if line.rstrip():
                splitline = line.rstrip().split('|')
                movie_id = splitline[0]
                title = splitline[1]
                releasedate = splitline[2]
                movie_data[0] = movie_id
                movie_data[1] = title
                movie_data[2] = releasedate[-4:]

                genres = splitline[5:]
                for (genre, isGenre) in enumerate(genres):
                    if isGenre == '1':
                        movie_data[genre+3] = True
                c.execute(insert_movie_query, movie_data)

def parse_user_data_100K(dataset, c, occupation_map):
    insert_user_query = 'INSERT INTO users(id, age, gender, occupation, zipcode) VALUES (?, ?, ?, ?, ?)'
    with open(os.path.join(dataset, 'u.user'), 'r') as userFile:
        for line in userFile:
            if line.rstrip():
                splitline = line.rstrip().split('|')
                user_id = splitline[0]
                age = splitline[1]
                gender = splitline[2] == 'F'
                occupation = occupation_map[splitline[3]]
                zipcode = splitline[4]
                try:
                    zipcode = int(zipcode)
                except:
                    zipcode = 0
                c.execute(insert_user_query,
                        (user_id, age, gender, occupation, zipcode))

def parse_rating_data_100K(dataset, c):
    insert_rating_query = 'INSERT INTO ratings(user, movie, rating) VALUES (?, ?, ?)'
    with open(os.path.join(dataset, 'u.data'), 'r') as ratingFile:
        for line in ratingFile:
            if line.rstrip():
                splitline = line.rstrip().split('\t')
                user = splitline[0]
                movie = splitline[1]
                rating = splitline[2]
                c.execute(insert_rating_query, (user, movie, rating))

def generate_database_1M(dataset, database):
    if os.path.isfile(database):
        os.remove(database)

    conn = sqlite3.connect(database)
    conn.text_factory = str
    c = conn.cursor()

    create_tables(c)

    genre_map = make_genre_map_1M(c)
    parse_movie_data_1M_10M(dataset, genre_map, c)
    parse_user_data_1M(dataset, c)
    parse_rating_data_1M(dataset, c)

    conn.commit()
    conn.close()

def make_genre_map_1M(c):
    genre_map = {}
    genres = ["Action", "Adventure", "Animation", "Children's", "Comedy",
            "Crime", "Documentary", "Drama", "Fantasy", "Film-Noir", "Horror",
            "Musical", "Mystery", "Romance", "Sci-Fi", "Thriller", "War",
            "Western"]
    for (gid, genre) in enumerate(genres):
        genre_map[genre] = gid

    return genre_map

def parse_movie_data_1M_10M(dataset, genre_map, c):
    insert_movie_query = '''INSERT INTO movies VALUES
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)'''
    with open(os.path.join(dataset, 'movies.dat'), 'r') as movieFile:
        for line in movieFile:
            movie_data = [0]*22
            if line.rstrip():
                splitline = line.rstrip().split('::')
                movie_id = splitline[0]
                title = splitline[1]
                releasedate = title[-5:-1]

                movie_data[0] = movie_id
                movie_data[1] = title
                movie_data[2] = releasedate

                genres = splitline[2]
                for genre in genres.split('|'):
                    try:
                        genre_id = genre_map[genre]
                        movie_data[genre_id+3] = 1
                    except:
                        print("Error, invalid genre \"%s\" for movie %s" % (genre, movie_id))
                c.execute(insert_movie_query, movie_data)

def parse_user_data_1M(dataset, c):
    insert_user_query = 'INSERT INTO users(id, age, gender, occupation, zipcode) VALUES (?, ?, ?, ?, ?)'
    with open(os.path.join(dataset, 'users.dat'), 'r') as userFile:
        for line in userFile:
            if line.rstrip():
                splitline = line.rstrip().split('::')
                user_id = splitline[0]
                gender = splitline[1] == 'F'
                age = splitline[2]
                occupation = int(splitline[3])+1
                zipcode = splitline[4]
                try:
                    zipcode = int(zipcode)
                except:
                    zipcode = 0
                c.execute(insert_user_query,
                        (user_id, age, gender, occupation, zipcode))

def parse_rating_data_1M(dataset, c):
    insert_rating_query = 'INSERT INTO ratings(user, movie, rating) VALUES (?, ?, ?)'
    with open(os.path.join(dataset, 'ratings.dat'), 'r') as ratingFile:
        for line in ratingFile:
            if line.rstrip():
                splitline = line.rstrip().split('::')
                user = splitline[0]
                movie = splitline[1]
                rating = splitline[2]
                c.execute(insert_rating_query, (user, movie, rating))


def generate_database_10M(dataset, database):
    if os.path.isfile(database):
        os.remove(database)

    conn = sqlite3.connect(database)
    conn.text_factory = str
    c = conn.cursor()

    create_tables(c)

    genre_map = make_genre_map_10M(c)
    insers_users_10M(c)
    parse_movie_data_1M_10M(dataset, genre_map, c)
    parse_rating_data_10M(dataset, c)

    conn.commit()
    conn.close()


def make_genre_map_10M(c):
    genre_map = {}
    genres = ["Action", "Adventure", "Animation", "Children", "Comedy",
            "Crime", "Documentary", "Drama", "Fantasy", "Film-Noir", "Horror",
            "IMAX", "Musical", "Mystery", "Romance", "Sci-Fi", "Thriller",
            "War", "Western"]
    insert_genre_query = 'INSERT INTO genres(id, name) VALUES (?, ?)'
    for (gid, genre) in enumerate(genres):
        genre_map[genre] = gid
        c.execute(insert_genre_query, (gid, genre))

    return genre_map

def insers_users_10M(c):
    insert_user_query = 'INSERT INTO users(id, age, gender, occupation, zipcode) VALUES (?, ?, ?, ?, ?)'
    for user in range(1, 71568):
        c.execute(insert_user_query, (user, None, None, None, None))

def parse_rating_data_10M(dataset, c):
    insert_rating_query = 'INSERT INTO ratings(user, movie, rating) VALUES (?, ?, ?)'
    with open(os.path.join(dataset, 'ratings.dat'), 'r') as ratingFile:
        for line in ratingFile:
            if line.rstrip():
                splitline = line.rstrip().split('::')
                user = splitline[0]
                movie = splitline[1]
                rating = int(round(float(splitline[2])))
                c.execute(insert_rating_query, (user, movie, rating))

def create_tables(c):
    c.execute('''CREATE TABLE movies(id INTEGER PRIMARY KEY,
            title TEXT NOT NULL, releasedate INTEGER,
            g_1 BOOLEAN DEFAULT FALSE,
            g_2 BOOLEAN DEFAULT FALSE,
            g_3 BOOLEAN DEFAULT FALSE,
            g_4 BOOLEAN DEFAULT FALSE,
            g_5 BOOLEAN DEFAULT FALSE,
            g_6 BOOLEAN DEFAULT FALSE,
            g_7 BOOLEAN DEFAULT FALSE,
            g_8 BOOLEAN DEFAULT FALSE,
            g_9 BOOLEAN DEFAULT FALSE,
            g_10 BOOLEAN DEFAULT FALSE,
            g_11 BOOLEAN DEFAULT FALSE,
            g_12 BOOLEAN DEFAULT FALSE,
            g_13 BOOLEAN DEFAULT FALSE,
            g_14 BOOLEAN DEFAULT FALSE,
            g_15 BOOLEAN DEFAULT FALSE,
            g_16 BOOLEAN DEFAULT FALSE,
            g_17 BOOLEAN DEFAULT FALSE,
            g_18 BOOLEAN DEFAULT FALSE,
            g_19 BOOLEAN DEFAULT FALSE)''')
    c.execute('''CREATE TABLE users(id INTEGER PRIMARY KEY,
            age INTEGER, gender BOOLEAN,
            occupation INTEGER REFERENCES occupations(id), zipcode INTEGER)''')
    c.execute('''CREATE TABLE ratings(user INTEGER REFERENCES user(id),
            movie INTEGER NOT NULL REFERENCES movies(id),
            rating DOUBLE NOT NULL)''')


if __name__ == '__main__':
    main()
