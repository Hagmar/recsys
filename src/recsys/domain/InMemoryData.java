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
    private Map<User, Map<Integer, Integer>> userRatings = new HashMap<>();
    /** Contains item ratings per item, as Map<Item, Map<User, Rating> */
    private Map<Integer, Map<User, Integer>> itemRatings = new HashMap<>();

    /** Cache for user userRatings per item, as Map<Item, Map<User, Rating> */
    private Map<Integer, Map<User, Map<Integer, Integer>>> itemCache = new HashMap<>();

    /**
     * Creates a data container and loads the data from a .csv file.
     * @param filepath The .csv file to load the data from.
     */
    public InMemoryData(String filepath) {
        parseData(filepath);
        System.out.println("Data contains userRatings for " + userRatings.size() + " users.");
    }

    /**
     * Creates a data container with injected data.
     */
    public InMemoryData(Map<User, Map<Integer, Integer>> ratings) {
        this.userRatings = ratings;
    }

    /**
     * Creates an empty data container.
     */
    public InMemoryData() {}

    @Override
    public Map<Integer, Integer> getRatings(User user) {
        return userRatings.get(user);
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
            Map<User, Integer> users = itemRatings.get(item);
            if (users != null) {
                for (User user : users.keySet()) {
                    result.put(user, userRatings.get(user));
                }
            }
            itemCache.put(item, result);
        }
        return result;
    }

    public void add(User user, int item, int rating) {
        Map<Integer, Integer> uRatings = userRatings.get(user);
        if (uRatings == null) {
            uRatings = new HashMap<>();
            userRatings.put(user, uRatings);
        }
        uRatings.put(item, rating);

        Map<User, Integer> iRatings = itemRatings.get(item);
        if (iRatings == null) {
            iRatings = new HashMap<>();
            itemRatings.put(item, iRatings);
        }
        iRatings.put(user, rating);
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
