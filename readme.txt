Recommender System

Linnéa Claesson	-- 5105556 
Magnus Hagmar 	-- 5088131
Anton Jansson 	-- 5088141 
Sara Mumtaz 	-- 5039270 

Setting up of the environment:
1. Download the zip-file ml-100k.zip from GroupLens' web page: http://grouplens.org/datasets/movielens/ (Last verified on 1 June). Extract to the project root.

2. Run the m-file data_analysis.m using MatLab. (OBS! This will overwrite u.data in ml-100k, if you want to run the script again you have to copy in a new version of the original u.data first.)

3. Make sure project root is the current working directory for all of the following steps.

4. Run "create_database.py" (in the "database" folder) on the new u.data by typing the following into your terminal:
    "database/create_database.py -d ml-100k/"

5. Run "compile.sh". This downloads dependencies and compiles the Java application.


Run:
- Evaluation: 10-fold cross validation (comparison with Linear regression)
  1. Run "run.sh".

- Command line interface - for testing individual predictions
  1. Run "run.sh cli".
