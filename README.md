Recommender system
===

## Run

### From command line
Run your compiled `Main` class from the terminal (or your IDE).

Available commands:

* `predict <user-id> <item-id>` | Predicts the rating for a user and an item.
* `recommend <user-id> <limit-items>` | Returns a *limit-items* number of items that are have the highest predicted rating.

## For developers
The `core` package contains domain independent classes and interfaces for the recommender system. Implement these interfaces in classes for the specific domain in the root (or another) package.

The class `core/RecommenderSystem` is the main interface for the recommender system functionality. `Main` is supposed to run a command line interface using an instance of `core/RecommenderSystsem`.
