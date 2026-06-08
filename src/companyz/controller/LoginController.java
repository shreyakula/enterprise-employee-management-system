package companyz.controller;

import companyz.dao.AuthDAO;
import companyz.model.User;
import companyz.util.InputUtil;
import java.sql.SQLException;

public class LoginController {
    private final AuthDAO authDAO = new AuthDAO();

    public User login() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║    COMPANY Z - EMPLOYEE MANAGEMENT   ║");
        System.out.println("║             SYSTEM LOGIN             ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("\n  Login with your Employee ID and password.");
        System.out.println("  (Password = last 4 digits of your SSN)");
        System.out.println("  (HR Demo: ID=0, Password=admin)\n");

        int attempts = 0;
        while (attempts < 3) {
            try {
                int empID = InputUtil.readInt("  Employee ID: ");
                String password = InputUtil.readLine("  Password: ");

                User user = authDAO.authenticateDemo(empID, password);
                if (user != null) {
                    System.out.println("\n  ✓ Login successful! Welcome, " + user.getFullName() + ".");
                    System.out.println("  Role: " + (user.isAdmin() ? "HR Administrator" : "General Employee"));
                    return user;
                } else {
                    attempts++;
                    System.out.println("  ✗ Invalid credentials. Attempt " + attempts + " of 3.");
                }
            } catch (SQLException e) {
                System.out.println("  Database error: " + e.getMessage());
                return null;
            }
        }
        System.out.println("  Too many failed attempts. Exiting.");
        return null;
    }
}
