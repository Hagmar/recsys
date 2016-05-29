package recsys.domain;

import recsys.core.Configuration;
import recsys.core.Data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryData implements Data<User, Movie>, Serializable {

    /** Contains item ratings per user, as Map<User, Map<Item, Rating> */
    private Map<User, Map<Movie, Double>> userRatings = new HashMap<>();
    /** Contains item ratings per item, as Map<Item, Map<User, Rating> */
    private Map<Movie, Map<User, Double>> itemRatings = new HashMap<>();
    /** Sum of all ratings. Used for average. */
    private Map<Movie, Double> itemRatingSum = new HashMap<>();
    /** Number of ratings. Used for average. */
    private Map<Movie, Integer> itemRatingCount = new HashMap<>();

    /** Cache for user userRatings per item, as Map<Item, Map<User, Rating> */
    private Map<Movie, Map<User, Map<Movie, Double>>> itemCache = new HashMap<>();

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
    public InMemoryData(Map<User, Map<Movie, Double>> ratings) {
        this.userRatings = ratings;
    }

    /**
     * Creates an empty data container.
     */
    public InMemoryData() {}

    @Override
    public Map<Movie, Double> getRatings(User user) {
        return userRatings.get(user);
    }

    @Override
    public Double getRating(User user, Movie item) {
        Map<Movie, Double> userRatings = getRatings(user);
        if (userRatings == null)
            return null;
        return userRatings.get(item);
    }

    @Override
    public Map<User, Map<Movie, Double>> getUserRatingsByItem(Movie item) {
        Map<User, Map<Movie, Double>> result = itemCache.get(item);
        if (result == null) {
            result = new HashMap<>();
            Map<User, Double> users = itemRatings.get(item);
            if (users != null) {
                for (User user : users.keySet()) {
                    result.put(user, userRatings.get(user));
                }
            }
            itemCache.put(item, result);
        }
        return result;
    }

    @Override
    public Map<Movie, Double> getAverageRatings(Collection<Movie> integers) {
        Map<Movie, Double> average = new HashMap<>();
        for (Movie item : integers) {
            average.put(item, getAverage(item));
        }
        return average;
    }

    public void add(User user, Movie item, double rating) {
        Map<Movie, Double> uRatings = userRatings.get(user);
        if (uRatings == null) {
            uRatings = new HashMap<>();
            userRatings.put(user, uRatings);
        }
        uRatings.put(item, rating);

        Map<User, Double> iRatings = itemRatings.get(item);
        if (iRatings == null) {
            iRatings = new HashMap<>();
            itemRatings.put(item, iRatings);
        }
        iRatings.put(user, rating);

        Double sum = itemRatingSum.get(item);
        Integer count = itemRatingCount.get(item);
        itemRatingSum.put(item, (sum != null ? sum : 0) + rating);
        itemRatingCount.put(item, (count != null ? count : 0) + 1);
    }

    private double getAverage(Movie item) {
        Integer count = itemRatingCount.get(item);
        return count != null ? itemRatingSum.get(item) / count : Configuration.DEFAULT_AVERAGE;
    }

    private void parseData(String filepath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\\W");
                User user = new User(Integer.parseInt(columns[0]));
                Movie movie = new Movie(Integer.parseInt(columns[1]));
                add(user, movie, Integer.parseInt(columns[2]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
