package recsys.domain;

import recsys.core.Data;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SqliteData implements Data<Integer, Integer>, Serializable {

    private static final String SELECT_SQL = "SELECT user, movie, rating FROM ratings";

    private PreparedStatement getUserRatingsStmt;
    private PreparedStatement getUserItemRatingStmt;
    private PreparedStatement getUsersStmt;
    private PreparedStatement getItemRatingsStmt;

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
            getUsersStmt = conn.prepareStatement("SELECT user FROM ratings");
            getItemRatingsStmt = conn.prepareStatement(SELECT_SQL + " WHERE movie = ?");
        } catch (SQLException e) {
            System.out.println("Could not connect to database. " + e.getMessage());
        }
    }

    @Override
    public Map<Integer, Integer> getRatings(Integer user) {
        Map<Integer, Integer> result = new HashMap<>();
        try {
            getUserRatingsStmt.setInt(1, user);
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
    public Integer getRating(Integer user, Integer item) {
        try {
            getUserItemRatingStmt.setInt(1, user);
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
    public Collection<Integer> getUsers() {
        Collection<Integer> users = new ArrayList<>();
        try {
            ResultSet rs = getUsersStmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getInt("user"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Map<Integer, Integer> getItemRatings(Integer item) {
        Map<Integer, Integer> result = new HashMap<>();
        try {
            getItemRatingsStmt.setInt(1, item);
            ResultSet rs = getItemRatingsStmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("user"), rs.getInt("rating"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
