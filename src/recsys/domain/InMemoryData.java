package recsys.domain;

import recsys.core.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InMemoryData implements Data<User, Integer>, Serializable {

    /** Contains item ratings per user, as Map<User, Map<Item, Rating> */
    private Map<User, Map<Integer, Integer>> ratings = new HashMap<>();

    /** Cache for user ratings per item, as Map<Item, Map<User, Rating> */
    private Map<Integer, Map<User, Map<Integer, Integer>>> itemCache = new HashMap<>();

    /**
     * Creates a data container and loads the data from a .csv file.
     * @param filepath The .csv file to load the data from.
     */
    public InMemoryData(String filepath) {
        parseData(filepath);
        System.out.println("Data contains ratings for " + ratings.size() + " users.");
    }

    /**
     * Creates a data container with injected data.
     */
    public InMemoryData(Map<User, Map<Integer, Integer>> ratings) {
        this.ratings = ratings;
    }

    /**
     * Creates an empty data container.
     */
    public InMemoryData() {}

    @Override
    public Map<Integer, Integer> getRatings(User user) {
        return ratings.get(user);
    }

    @Override
    public Integer getRating(User user, Integer item) {
        Map<Integer, Integer> userRatings = getRatings(user);
        if (userRatings == null)
            return null;
        return userRatings.get(item);
    }

    @Override
    public Map<User, Map<Integer, Integer>> getUserRatingsByItem(Integer item) {
        Map<User, Map<Integer, Integer>> result = itemCache.get(item);
        if (result == null) {
            result = new HashMap<>();
            for (Map.Entry<User, Map<Integer, Integer>> entry : ratings.entrySet()) {
                Integer rating = getRating(entry.getKey(), item);
                if (rating != null) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            itemCache.put(item, result);
        }
        return result;
    }

    public void add(User user, int item, int rating) {
        Map<Integer, Integer> userRatings = ratings.get(user);
        if (userRatings == null) {
            userRatings = new HashMap<>();
            ratings.put(user, userRatings);
        }
        userRatings.put(item, rating);
    }

    private void parseData(String filepath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\\W");
                User user = new User(Integer.parseInt(columns[0]));
                add(user, Integer.parseInt(columns[1]), Integer.parseInt(columns[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
