package recsys.domain;

import recsys.core.Data;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryData implements Data<Integer, Integer>, Serializable {

    /** Contains item ratings per user, as Map<User, Map<Item, Rating> */
    private Map<Integer, Map<Integer, Integer>> ratings = new HashMap<>();

    /** Cache for user ratings per item, as Map<Item, Map<User, Rating> */
    private Map<Integer, Map<Integer, Integer>> itemCache = new HashMap<>();

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
    public InMemoryData(Map<Integer, Map<Integer, Integer>> ratings) {
        this.ratings = ratings;
    }

    /**
     * Creates an empty data container.
     */
    public InMemoryData() {}

    @Override
    public Map<Integer, Integer> getRatings(Integer user) {
        return ratings.get(user);
    }

    @Override
    public Integer getRating(Integer user, Integer item) {
        Map<Integer, Integer> userRatings = getRatings(user);
        if (userRatings == null)
            return null;
        return userRatings.get(item);
    }

    @Override
    public Collection<Integer> getUsers() {
        return ratings.keySet();
    }

    @Override
    public Map<Integer, Integer> getItemRatings(Integer item) {
        Map<Integer, Integer> result = itemCache.get(item);
        if (result == null) {
            result = new HashMap<>();
            for (Integer u : getUsers()) {
                Integer rating = getRating(u, item);
                if (rating != null) {
                    result.put(u, rating);
                }
            }
            itemCache.put(item, result);
        }
        return result;
    }

    public void add(int user, int item, int rating) {
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
                add(Integer.parseInt(columns[0]), Integer.parseInt(columns[1]), Integer.parseInt(columns[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
