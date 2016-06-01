package recsys.domain;

import recsys.core.Configuration;
import recsys.core.Data;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SqliteData implements Data<User, Movie>, Serializable {

    private static final String SELECT_SQL = "SELECT user, movie, rating, gender, age, zipcode, occupation, movies.* " +
            "FROM ratings JOIN users ON user = users.id JOIN movies ON movie = movies.id";

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
    public Map<Movie, Double> getRatings(User user) {
        Map<Movie, Double> result = new HashMap<>();
        try {
            getUserRatingsStmt.setInt(1, user.getId());
            ResultSet rs = getUserRatingsStmt.executeQuery();
            while (rs.next()) {
                result.put(mapMovie(rs), rs.getDouble("rating"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Double getRating(User user, Movie item) {
        try {
            getUserItemRatingStmt.setInt(1, user.getId());
            getUserItemRatingStmt.setInt(2, item.getId());
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
    public Map<User, Map<Movie, Double>> getUserRatingsByItem(Movie item) {
        Map<User, Map<Movie, Double>> result = new HashMap<>();
        try {
            getUserRatingsByItemStmt.setInt(1, item.getId());
            ResultSet rs = getUserRatingsByItemStmt.executeQuery();
            User currentUser = null;
            Map<Movie, Double> ratings = null;
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
                ratings.put(mapMovie(rs), rs.getDouble("rating"));
            }
            result.put(currentUser, ratings);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<Movie, Double> getAverageRatings(Collection<Movie> items) {
        StringBuilder builder = new StringBuilder();
        for (Movie item : items) {
            builder.append(item.getId()).append(',');
        }
        builder.deleteCharAt(builder.length() - 1);
        String sql = String.format(getAverageRatingsSql, builder.toString());
        Map<Movie, Double> result = new HashMap<>();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(mapMovie(rs), rs.getDouble("avg_rating"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Movie getItem(int id) {
        try {
            PreparedStatement stmt = conn.prepareStatement(SELECT_SQL + " WHERE movie = ? LIMIT 1");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return mapMovie(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUser(int id) {
        try {
            PreparedStatement stmt = conn.prepareStatement(SELECT_SQL + " WHERE user = ? LIMIT 1");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return mapUser(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("user"), rs.getBoolean("gender"), rs.getInt("age"),
                rs.getInt("zipcode"), rs.getInt("occupation"));
    }

    private Movie mapMovie(ResultSet rs) throws SQLException {
        boolean[] genres = new boolean[Configuration.NUMBER_OF_GENRES];
        for (int i = 0; i < Configuration.NUMBER_OF_GENRES; i++) {
            genres[i] = rs.getInt("g_" + (i + 1)) > 0;
        }
        return new Movie(rs.getInt("movie"), rs.getInt("releasedate"), genres);
    }
}
