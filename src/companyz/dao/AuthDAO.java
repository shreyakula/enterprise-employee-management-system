package companyz.dao;

import companyz.model.User;
import companyz.util.DBConnection;
import java.sql.*;

public class AuthDAO {

    public User authenticate(int empID, String password) throws SQLException {

        String sql = "SELECT e.empid, e.Fname, e.Lname, e.email, e.SSN " +
                     "FROM employees e WHERE e.empid = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setInt(1, empID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String ssn = rs.getString("SSN");
                String fullName = rs.getString("Fname") + " " + rs.getString("Lname");
                String email = rs.getString("email");

                // ✅ Password = last 4 digits of SSN
                String last4 = (ssn != null && ssn.length() >= 4)
                        ? ssn.substring(ssn.length() - 4)
                        : ssn;

                if (!password.equals(last4)) return null;

                // 🔥 FINAL ROLE LOGIC (ONLY THIS)
                User.Role role = (empID == 1)
                        ? User.Role.HR_ADMIN
                        : User.Role.GENERAL_EMPLOYEE;

                return new User(empID, email, role, fullName);
            }
        }

        return null;
    }

    public User authenticateDemo(int empID, String password) throws SQLException {

        // Demo login
        if (empID == 0 && password.equals("admin")) {
            return new User(0, "admin", User.Role.HR_ADMIN, "HR Administrator");
        }

        return authenticate(empID, password);
    }
}