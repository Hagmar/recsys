package recsys.domain;

import recsys.core.Configuration;
import recsys.core.ItemSimilarityFunction;

import java.io.Serializable;
import java.util.Map;

/**
 * Computes the similarity between two users.
 */
public class MovieSimilarity implements ItemSimilarityFunction<Movie>, Serializable{

    private static final double YEAR_WEIGHT = Configuration.MOVIE_GENRE_SIMILARITY_WEIGHT;
    private static final double GENRE_WEIGHT = 1 - YEAR_WEIGHT;

    public double similarity(Movie m1, Movie m2) {
        if (m1 == null || m2 == null)
            return 0;

        double yearSimilarity;
        if (m1.getYear() == 0) {
            yearSimilarity = 0;
        } else {
            yearSimilarity = Math.abs(m1.getYear() - m2.getYear());
            yearSimilarity = 1-(Math.pow(yearSimilarity, 2)/2500.0);
            yearSimilarity = Math.max(yearSimilarity, 0);
        }
        double genreSimilarity = cosineSimilarity(m1, m2);
        return yearSimilarity*YEAR_WEIGHT+genreSimilarity*GENRE_WEIGHT;
    }

    private double cosineSimilarity(Movie m1, Movie m2) {
        int scalarProduct = 0;
        int length1 = 0, length2 = 0;   // ||u1||, ||u2|| length of rating vectors u1 and u2 (squared)
        boolean[] m1genres = m1.getGenres();
        boolean[] m2genres = m2.getGenres();
        for (int i = 0; i < 19; i++) {
            if (m1genres[i] == m2genres[i]) {
                if (m1genres[i]) {
                    scalarProduct++;
                    length1++;
                    length2++;
                }
            } else if (m1genres[i]) {
                length1++;
            } else {
                length2++;
            }
        }

        // Not defined for zero lengths
        if (length1 == 0 || length2 == 0)
            return 0;

        return scalarProduct / (Math.sqrt(length1) * Math.sqrt(length2));
    }
}
