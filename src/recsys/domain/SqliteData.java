package recsys.domain;

import recsys.core.Data;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SqliteData implements Data<User, Integer>, Serializable {

    private static final String SELECT_SQL = "SELECT user, movie, rating, gender, age FROM ratings " +
            "JOIN users ON user = users.id";

    private transient PreparedStatement getUserRatingsStmt;
    private transient PreparedStatement getUserItemRatingStmt;
    private transient PreparedStatement getUserRatingsByItemStmt;

    /**
     * Creates a data container and loads the data from a .csv file.
     */
    public SqliteData() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("DatabaseUtils.props"));
        } catch (IOException e) {
            System.out.println("File DatabaseUtils.props must exist in root folder. " + e.getMessage());
        }

        String databasePath = prop.getProperty("jdbcURL");
        loadDatabase(databasePath);
    }

    private void loadDatabase(String databaseUri) {
        try {
            Connection conn = DriverManager.getConnection(databaseUri);
            getUserRatingsStmt = conn.prepareStatement(SELECT_SQL + " WHERE user = ?");
            getUserItemRatingStmt = conn.prepareStatement(SELECT_SQL + " WHERE user = ? AND movie = ?");
            getUserRatingsByItemStmt = conn.prepareStatement(SELECT_SQL +
                    " WHERE user IN (SELECT user FROM ratings WHERE movie = ?) ORDER BY user");
        } catch (SQLException e) {
            System.out.println("Could not connect to database. " + e.getMessage());
        }
    }

    @Override
    public Map<Integer, Integer> getRatings(User user) {
        Map<Integer, Integer> result = new HashMap<>();
        try {
            getUserRatingsStmt.setInt(1, user.getId());
            ResultSet rs = getUserRatingsStmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("movie"), rs.getInt("rating"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer getRating(User user, Integer item) {
        try {
            getUserItemRatingStmt.setInt(1, user.getId());
            getUserItemRatingStmt.setInt(2, item);
            ResultSet rs = getUserItemRatingStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("rating");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<User, Map<Integer, Integer>> getUserRatingsByItem(Integer item) {
        Map<User, Map<Integer, Integer>> result = new HashMap<>();
        try {
            getUserRatingsByItemStmt.setInt(1, item);
            ResultSet rs = getUserRatingsByItemStmt.executeQuery();
            User currentUser = null;
            Map<Integer, Integer> ratings = null;
            int currentUserId = -1;
            while (rs.next()) {
                if (currentUserId != rs.getInt("user")) {
                    if (currentUser != null) {
                        result.put(currentUser, ratings);
                    }
                    currentUser = mapUser(rs);
                    currentUserId = currentUser.getId();
                    ratings = new HashMap<>();
                }
                ratings.put(rs.getInt("movie"), rs.getInt("rating"));
            }
            result.put(currentUser, ratings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("user"), rs.getBoolean("gender"), rs.getInt("age"));
    }
}
