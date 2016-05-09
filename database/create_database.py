#!/usr/bin/python

import sqlite3
import argparse
import zipfile
import os

def main():
    args = parse_args()

    dataset_path = os.path.basename(args.dataset)
    if not args.d:
        extract_dataset(args.dataset, dataset_path)

    if not args.database:
        args.database = dataset_path + '/movielens.db'

    if args.mode == '100K':
        generate_database_100K(dataset_path, args.database)
    else:
        print('Other dataset formats not implemented yet. Stick to 100K')


def parse_args():
    parser = argparse.ArgumentParser(description='Create database from MovieLens dataset')
    parser.add_argument('dataset', help='Zip-file containing the dataset')
    parser.add_argument('database', nargs='?', help='The file in which to store the database. Defaults to the same filename as the dataset, in the current working directory')
    parser.add_argument('-d', '--d', action='store_true', help='Interpret provided dataset as a folder where data has already been extracted')
    parser.add_argument('--mode', choices=['100K', '1M', '10M', '20M'], default='100K', help='Specify which format the dataset is in')

    args = parser.parse_args()
    return args


def extract_dataset(dataset, dataset_path):
    if not os.path.isdir(dataset_path):
        os.mkdir(dataset_path)

    with zipfile.ZipFile(dataset, 'r') as datazip:
        datazip.extractall(dataset_path)


def generate_database_100K(dataset_path, database):
    if os.path.isfile(database):
        os.remove(database)

    conn = sqlite3.connect(database)
    conn.text_factory = str
    c = conn.cursor()

    create_tables(c)

    occupation_map = parse_occupation_data(dataset_path, c)
    parse_genre_data(dataset_path, c)
    parse_movie_data(dataset_path, c)
    parse_user_data(dataset_path, c, occupation_map)
    parse_rating_data(dataset_path, c)

    conn.commit()
    conn.close()

def parse_occupation_data(dataset_path, c):
    occupation_map = {}
    insert_occupation_query = 'INSERT INTO occupations(occupation) VALUES (?)'
    with open(os.path.join(dataset_path, 'ml-100k/u.occupation'), 'r') as occFile:
        for line in occFile:
            if line.rstrip():
                occupation = line.rstrip()
                c.execute(insert_occupation_query, (occupation,))
                occupation_map[occupation] = c.lastrowid

    return occupation_map

def parse_genre_data(dataset_path, c):
    insert_genre_query = 'INSERT INTO genres(id, name) VALUES (?, ?)'
    with open(os.path.join(dataset_path, 'ml-100k/u.genre'), 'r') as genreFile:
        for line in genreFile:
            if line.rstrip():
                [genre, gid] = line.rstrip().split('|')
                c.execute(insert_genre_query, (gid, genre))

def parse_movie_data(dataset_path, c):
    insert_movie_query = 'INSERT INTO movies(id, title, releasedate) VALUES (?, ?, ?)'
    insert_moviegenre_query = 'INSERT INTO moviegenres(movie, genre) VALUES (?, ?)'
    with open(os.path.join(dataset_path, 'ml-100k/u.item'), 'r') as movieFile:
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

def parse_user_data(dataset_path, c, occupation_map):
    insert_user_query = 'INSERT INTO users(id, age, gender, occupation, zipcode) VALUES (?, ?, ?, ?, ?)'
    with open(os.path.join(dataset_path, 'ml-100k/u.user'), 'r') as userFile:
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

def parse_rating_data(dataset_path, c):
    insert_rating_query = 'INSERT INTO ratings(user, movie, rating, timestamp) VALUES (?, ?, ?, ?)'
    with open(os.path.join(dataset_path, 'ml-100k/u.data'), 'r') as ratingFile:
        for line in ratingFile:
            if line.rstrip():
                splitline = line.rstrip().split('\t')
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
