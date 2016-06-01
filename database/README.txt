The script "create_database.py" can be used to create a SQLite database from the MovieLens datasets.

A short description of the command line interface can be shown by running the script with the help flag, '-h'. A more detailed description is included below.

To run, one must provide the path to the dataset as a command line argument. The script interprets the provided argument as the path to the zipped dataset and unzips the contents in the current working directory. If one wishes to provide a dataset which is already unzipped, then the '-d' flag can be provided, causing the first argument to be interpreted as a path to the extracted folder, and will skip the extraction step.

By default, the script saves the created database as 'movielens.db' in the extracted folder. If a second argument is provided, this will be used as the path and filename of the database file instead.

Finally, the MovieLens datasets' formats differ between the different sizes. To work around this, the script must be provided with a switch specifying the mode in which to run. This is done with '--mode XXX' Currently the script supports the 100K, 1M, 10M and 20M datasets, and these are the mode names as well. The mode defaults to 100K.

Extract the 100K dataset from zip, create database in extracted folder
    ./create_database.py path/to/dataset.zip

Extract 1M dataset, save database as 'mydata.db'
    ./create_database.py --mode 1M path/to/1Mdataset.zip mydata.db

Create database from previously extracted 10M dataset, save in the same folder
    ./create_database.py --mode 10M -d path/to/extracted/dataset
