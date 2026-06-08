package companyz.model;

public class User {
    public enum Role { HR_ADMIN, GENERAL_EMPLOYEE }

    private int empID;
    private String username; // email used as username
    private Role role;
    private String fullName;

    public User(int empID, String username, Role role, String fullName) {
        this.empID = empID;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
    }

    public int getEmpID() { return empID; }
    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public String getFullName() { return fullName; }
    public boolean isAdmin() { return role == Role.HR_ADMIN; }
}
