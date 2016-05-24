package recsys.domain;

import java.io.Serializable;

/**
 * @author Anton Jansson.
 */
public class User implements Serializable {
    private final int id;
    private final boolean gender;
    private final int age;
    private final int zipcode;
    private final int occupation;

    public User(int id) {
        this(id, false, 0);
    }

    public User(int id, boolean gender, int age) {
        this(id, gender, age, 0, 0);
    }

    public User(int id, boolean gender, int age, int zipcode, int occupation) {
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.zipcode = zipcode;
        this.occupation = occupation;
    }

    public int getId() {
        return id;
    }

    public boolean getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public int getZipcode() { return zipcode; }

    public int getOccupation() { return occupation; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
