package recsys.domain;

import java.io.Serializable;

/**
 * @author Magnus Hagmar.
 */
public class Movie implements Serializable {
    private final int id;
    private final int year;
    private boolean[] genres;

    public Movie(int id) {
        this(id, 0, null);
    }

    public Movie(int id, int year, boolean[] genres) {
        this.id = id;
        this.year = year;
        this.genres = genres;
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public boolean[] getGenres() {
        return genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
