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

## Development setup
### Libraries
Make sure the `*.jar` files in the `libs` folder are included as libraries in your project. Add those manually in your IDE.

## Project structure
The `core` package contains domain independent classes and interfaces for the recommender system.

Put domain specific classes in the `domain` package. These classes are implementations of the interfaces in `core`.

The class `core/RecommenderSystem` is the main interface for the recommender system functionality. `Main` runs a command line interface that is using an instance of `core/RecommenderSystsem`.
