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
        args.dataset = dataset_name

    if not args.database:
        args.database = os.path.join(args.dataset, 'movielens.db')

    if args.mode == '100K':
        generate_database_100K(args.dataset, args.database)
    elif args.mode == '1M':
        generate_database_1M(args.dataset, args.database)
    else:
        print('Other dataset formats not implemented yet. Stick to 100K')


def parse_args():
    parser = argparse.ArgumentParser(description='Create database from MovieLens dataset')
    parser.add_argument('dataset', help='Zip-file containing the dataset')
    parser.add_argument('database', nargs='?', help='The file in which to store the database. Defaults to "movielens.db", in the current working directory')
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
    parse_genre_data_100K(dataset, c)
    parse_movie_data_100K(dataset, c)
    parse_user_data_100K(dataset, c, occupation_map)
    parse_rating_data_100K(dataset, c)

    conn.commit()
    conn.close()

def parse_occupation_data_100K(dataset, c):
    occupation_map = {}
    insert_occupation_query = 'INSERT INTO occupations(occupation) VALUES (?)'
    with open(os.path.join(dataset, 'u.occupation'), 'r') as occFile:
        for line in occFile:
            if line.rstrip():
                occupation = line.rstrip()
                c.execute(insert_occupation_query, (occupation,))
                occupation_map[occupation] = c.lastrowid

    return occupation_map

def parse_genre_data_100K(dataset, c):
    insert_genre_query = 'INSERT INTO genres(id, name) VALUES (?, ?)'
    with open(os.path.join(dataset, 'u.genre'), 'r') as genreFile:
        for line in genreFile:
            if line.rstrip():
                [genre, gid] = line.rstrip().split('|')
                c.execute(insert_genre_query, (gid, genre))

def parse_movie_data_100K(dataset, c):
    insert_movie_query = 'INSERT INTO movies(id, title, releasedate) VALUES (?, ?, ?)'
    insert_moviegenre_query = 'INSERT INTO moviegenres(movie, genre) VALUES (?, ?)'
    with open(os.path.join(dataset, 'u.item'), 'r') as movieFile:
        for line in movieFile:
            if line.rstrip():
                splitline = line.rstrip().split('|')
                movie_id = splitline[0]
                title = splitline[1]
                releasedate = splitline[2]
                c.execute(insert_movie_query, (movie_id, title, releasedate))

                genres = splitline[5:]
                for (genre, isGenre) in enumerate(genres):
                    if isGenre == '1':
                        c.execute(insert_moviegenre_query, (movie_id, genre))

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
                c.execute(insert_user_query,
                        (user_id, age, gender, occupation, zipcode))

def parse_rating_data_100K(dataset, c):
    insert_rating_query = 'INSERT INTO ratings(user, movie, rating, timestamp) VALUES (?, ?, ?, ?)'
    with open(os.path.join(dataset, 'u.data'), 'r') as ratingFile:
        for line in ratingFile:
            if line.rstrip():
                splitline = line.rstrip().split('\t')
                user = splitline[0]
                movie = splitline[1]
                rating = splitline[2]
                tstamp = splitline[3]
                c.execute(insert_rating_query, (user, movie, rating, tstamp))

def generate_database_1M(dataset, database):
    if os.path.isfile(database):
        os.remove(database)

    conn = sqlite3.connect(database)
    conn.text_factory = str
    c = conn.cursor()

    create_tables(c)

    create_occupation_table_1M(c)
    genre_map = make_genre_map(c)
    parse_movie_data_1M(dataset, genre_map, c)
    parse_user_data_1M(dataset, c)
    parse_rating_data_1M(dataset, c)

    conn.commit()
    conn.close()

def create_occupation_table_1M(c):
    insert_occupation_query = 'INSERT INTO occupations(id, occupation) VALUES (?, ?)'
    occupations = ['other', 'academic/educator', 'artist', 'clerical/admin',
            'college/grad student', 'customer service', 'doctor/health care',
            'executive/managerial', 'farmer', 'homemaker', 'K-12 student',
            'lawyer', 'programmer', 'retured', 'sales/marketing',
            'scientist', 'self-employed', 'technician/engineer',
            'tradesman/craftsman', 'unemployed', 'writer']
    for occ_id, occupation in enumerate(occupations):
        c.execute(insert_occupation_query, (occ_id, occupation))

def make_genre_map(c):
    genre_map = {}
    genres = ["Action", "Adventure", "Animation", "Children's", "Comedy",
            "Crime", "Documentary", "Drama", "Fantasy", "Film-Noir", "Horror",
            "Musical", "Mystery", "Romance", "Sci-Fi", "Thriller", "War",
            "Western"]
    insert_genre_query = 'INSERT INTO genres(id, name) VALUES (?, ?)'
    for (gid, genre) in enumerate(genres):
        genre_map[genre] = gid
        c.execute(insert_genre_query, (gid, genre))

    return genre_map

def parse_movie_data_1M(dataset, genre_map, c):
    insert_movie_query = 'INSERT INTO movies(id, title, releasedate) VALUES (?, ?, ?)'
    insert_moviegenre_query = 'INSERT INTO moviegenres(movie, genre) VALUES (?, ?)'
    with open(os.path.join(dataset, 'movies.dat'), 'r') as movieFile:
        for line in movieFile:
            if line.rstrip():
                splitline = line.rstrip().split('::')
                movie_id = splitline[0]
                title = splitline[1]
                releasedate = title[-5:-1]
                c.execute(insert_movie_query, (movie_id, title, releasedate))

                genres = splitline[2]
                for genre in genres.split('|'):
                    try:
                        genre_id = genre_map[genre]
                        c.execute(insert_moviegenre_query, (movie_id, genre))
                    except:
                        print("Error, invalid genre \"%s\" for movie %s" % (genre, movie_id))

def parse_user_data_1M(dataset, c):
    insert_user_query = 'INSERT INTO users(id, age, gender, occupation, zipcode) VALUES (?, ?, ?, ?, ?)'
    with open(os.path.join(dataset, 'users.dat'), 'r') as userFile:
        for line in userFile:
            if line.rstrip():
                splitline = line.rstrip().split('::')
                user_id = splitline[0]
                gender = splitline[1] == 'F'
                age = splitline[2]
                occupation = splitline[3]
                zipcode = splitline[4]
                c.execute(insert_user_query,
                        (user_id, age, gender, occupation, zipcode))

def parse_rating_data_1M(dataset, c):
    insert_rating_query = 'INSERT INTO ratings(user, movie, rating, timestamp) VALUES (?, ?, ?, ?)'
    with open(os.path.join(dataset, 'ratings.dat'), 'r') as ratingFile:
        for line in ratingFile:
            if line.rstrip():
                splitline = line.rstrip().split('::')
                user = splitline[0]
                movie = splitline[1]
                rating = splitline[2]
                tstamp = splitline[3]
                c.execute(insert_rating_query, (user, movie, rating, tstamp))

def create_tables(c):
    c.execute('''CREATE TABLE occupations(id INTEGER PRIMARY KEY ASC,
            occupation TEXT)''')
    c.execute('''CREATE TABLE movies(id INTEGER PRIMARY KEY,
            title TEXT, releasedate TEXT)''')
    c.execute('''CREATE TABLE genres(id INTEGER PRIMARY KEY ASC, name TEXT)''')
    c.execute('''CREATE TABLE moviegenres(movie INTEGER REFERENCES movies(id),
            genre INTEGER REFERENCES genres(id))''')
    c.execute('''CREATE TABLE users(id INTEGER PRIMARY KEY,
            age INTEGER NOT NULL, gender BOOLEAN,
            occupation INTEGER REFERENCES occupations(id), zipcode CHAR(5))''')
    c.execute('''CREATE TABLE ratings(user INTEGER REFERENCES user(id),
            movie INTEGER REFERENCES movies(id), rating INTEGER NOT NULL,
            timestamp INTEGER)''')


if __name__ == '__main__':
    main()
