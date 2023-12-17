import java.io.Serializable;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class Employee implements Serializable {

    private final String email;
    private final String fullName;

    public Employee(String email, String fullName) {

        this.email = email;
        this.fullName = fullName;
    }
    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }


}



