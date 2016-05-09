package recsys.domain;

import recsys.core.Data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class InMemoryData implements Data<Integer, Integer>, Serializable {

    private Map<Integer, Map<Integer, Integer>> ratings = new HashMap<>();

    /**
     * Creates a data container and loads the data from a .csv file.
     * @param filepath The .csv file to load the data from.
     */
    public InMemoryData(String filepath) {
        parseData(filepath);
        System.out.println("Data contains ratings for " + ratings.size() + " users.");
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
