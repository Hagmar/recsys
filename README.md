Recommender system
===

## Run

### Load your dataset
Download the dataset from [GroupLens](http://www.grouplens.org/node/73). Extract the dataset .zip folder in the root of this project, so that you have a folder /ml-100k for instance.

### From command line
Run your compiled `Main` class from the terminal (or your IDE).

Available commands:

* `predict <user-id> <item-id>` | Predicts the rating for a user and an item.
* `recommend <user-id> <limit-items>` | Returns a *limit-items* number of items that are have the highest predicted rating.

### Cross-validation using Weka libraries
Run `weka/TestRunner` to perform a k-fold cross validation.

### Configuration
Change how the recommender system should behave by modifying the constants in the class `core/Configuration`. The properties let you enable/disable features and tune parameters for the prediction.

## Development setup
### Libraries
Make sure the `*.jar` files in the `libs` folder are included as libraries in your project. Add those manually in your IDE.

### Weka database
The file `DatabaseUtils.props` contains the path to your database file (relative from root folder). Change this path if the database is not found when running Weka tests. 

## Project structure
The `core` package contains domain independent classes and interfaces for the recommender system.

Put domain specific classes in the `domain` package. These classes are implementations of the interfaces in `core`.

The class `core/RecommenderSystem` is the main interface for the recommender system functionality. `Main` runs a command line interface that is using an instance of `core/RecommenderSystsem`.
