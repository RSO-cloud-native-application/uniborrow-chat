package si.fri.rso.uniborrow.chat.services.dtos;

public class UniborrowUserRequest {
    private Integer userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    public UniborrowUserRequest() {}

    public UniborrowUserRequest(
            Integer userId,
            String username,
            String firstName,
            String lastName,
            String email) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
