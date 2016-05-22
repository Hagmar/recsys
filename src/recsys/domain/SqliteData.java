package recsys.domain;

import recsys.core.Data;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SqliteData implements Data<User, Integer>, Serializable {

    private static final String SELECT_SQL = "SELECT user, movie, rating, gender, age FROM ratings " +
            "JOIN users ON user = users.id";

    private transient Connection conn;
    private transient PreparedStatement getUserRatingsStmt;
    private transient PreparedStatement getUserItemRatingStmt;
    private transient PreparedStatement getUserRatingsByItemStmt;
    private transient String getAverageRatingsSql;

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
            conn = DriverManager.getConnection(databaseUri);
            getUserRatingsStmt = conn.prepareStatement(SELECT_SQL + " WHERE user = ?");
            getUserItemRatingStmt = conn.prepareStatement(SELECT_SQL + " WHERE user = ? AND movie = ?");
            getUserRatingsByItemStmt = conn.prepareStatement(SELECT_SQL +
                    " WHERE user IN (SELECT user FROM ratings WHERE movie = ?) ORDER BY user");
            getAverageRatingsSql = "SELECT movie, AVG(rating) as avg_rating FROM ratings" +
                    " WHERE movie IN (%s) GROUP BY movie";
        } catch (SQLException e) {
            System.out.println("Could not connect to database. " + e.getMessage());
        }
    }

    @Override
    public Map<Integer, Double> getRatings(User user) {
        Map<Integer, Double> result = new HashMap<>();
        try {
            getUserRatingsStmt.setInt(1, user.getId());
            ResultSet rs = getUserRatingsStmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("movie"), rs.getDouble("rating"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Double getRating(User user, Integer item) {
        try {
            getUserItemRatingStmt.setInt(1, user.getId());
            getUserItemRatingStmt.setInt(2, item);
            ResultSet rs = getUserItemRatingStmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("rating");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<User, Map<Integer, Double>> getUserRatingsByItem(Integer item) {
        Map<User, Map<Integer, Double>> result = new HashMap<>();
        try {
            getUserRatingsByItemStmt.setInt(1, item);
            ResultSet rs = getUserRatingsByItemStmt.executeQuery();
            User currentUser = null;
            Map<Integer, Double> ratings = null;
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
                ratings.put(rs.getInt("movie"), rs.getDouble("rating"));
            }
            result.put(currentUser, ratings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<Integer, Double> getAverageRatings(Collection<Integer> items) {
        StringBuilder builder = new StringBuilder();
        for (Integer item : items) {
            builder.append(item).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        String sql = String.format(getAverageRatingsSql, builder.toString());
        Map<Integer, Double> result = new HashMap<>();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("movie"), rs.getDouble("avg_rating"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("user"), rs.getBoolean("gender"), rs.getInt("age"));
    }
}
