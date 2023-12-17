import java.io.Serializable;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User implements Serializable {

    private final String username;
    private String id;

    public User(String username, String id) {

        this.username = username;
        this.id = id;
    }
    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }


}
// dvd class: represent each movie. ( title, price)


