package companyz.dao;

import companyz.model.Employee;
import companyz.util.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private static final String BASE_SELECT =
        "SELECT DISTINCT e.empid, e.Fname, e.Lname, e.email, e.HireDate, e.Salary, e.SSN, e.addressID, " +
        "jt.job_title, d.Name AS divisionName " +
        "FROM employees e " +
        "LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
        "LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
        "LEFT JOIN employee_division ed ON e.empid = ed.empid " +
        "LEFT JOIN division d ON ed.div_ID = d.ID ";

    private Employee mapResultSet(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmpID(rs.getInt("empid"));
        emp.setFname(rs.getString("Fname"));
        emp.setLname(rs.getString("Lname"));
        emp.setEmail(rs.getString("email"));
        emp.setHireDate(rs.getDate("HireDate"));
        emp.setSalary(rs.getBigDecimal("Salary"));
        emp.setSsn(rs.getString("SSN"));
        emp.setAddressID(rs.getInt("addressID"));
        emp.setJobTitle(rs.getString("job_title"));
        emp.setDivisionName(rs.getString("divisionName"));
        return emp;
    }

    public Employee getByEmpID(int empID) throws SQLException {
        String sql = BASE_SELECT + "WHERE e.empid = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, empID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        }
        return null;
    }

    public List<Employee> searchByName(String name) throws SQLException {
        String sql = BASE_SELECT + "WHERE e.empid IN (SELECT empid FROM employees WHERE CONCAT(Fname,' ',Lname) LIKE ? OR Fname LIKE ? OR Lname LIKE ?)";
        List<Employee> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            String pattern = "%" + name + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Employee> searchBySSN(String ssn) throws SQLException {
        String sql = BASE_SELECT + "WHERE e.SSN = ?";
        List<Employee> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ssn);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(BASE_SELECT + "ORDER BY e.Lname, e.Fname")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        }
        return list;
    }

    public boolean updateEmployee(int empID, String fname, String lname, String email,
                                   BigDecimal salary, Date hireDate, String ssn) throws SQLException {
        String sql = "UPDATE employees SET Fname=?, Lname=?, email=?, Salary=?, HireDate=?, SSN=? WHERE empid=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, email);
            ps.setBigDecimal(4, salary);
            ps.setDate(5, hireDate);
            ps.setString(6, ssn);
            ps.setInt(7, empID);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteEmployee(int empID) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            for (String tbl : new String[]{"employee_job_titles", "employee_division", "payroll"}) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + tbl + " WHERE empid=?")) {
                    ps.setInt(1, empID);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE empid=?")) {
                ps.setInt(1, empID);
                int rows = ps.executeUpdate();
                if (rows == 0) { conn.rollback(); return false; }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public int addEmployee(String fname, String lname, String email, Date hireDate,
                            BigDecimal salary, String ssn, int addressID,
                            int jobTitleID, int divID) throws SQLException {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            int newEmpID;
            String empSql = "INSERT INTO employees (Fname, Lname, email, HireDate, Salary, SSN, addressID) VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(empSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, fname);
                ps.setString(2, lname);
                ps.setString(3, email);
                ps.setDate(4, hireDate);
                ps.setBigDecimal(5, salary);
                ps.setString(6, ssn);
                ps.setInt(7, addressID);
                ps.executeUpdate();
                ResultSet gen = ps.getGeneratedKeys();
                gen.next();
                newEmpID = gen.getInt(1);
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO employee_job_titles (empid, job_title_id) VALUES (?,?)")) {
                ps.setInt(1, newEmpID);
                ps.setInt(2, jobTitleID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO employee_division (empid, div_ID) VALUES (?,?)")) {
                ps.setInt(1, newEmpID);
                ps.setInt(2, divID);
                ps.executeUpdate();
            }
            conn.commit();
            return newEmpID;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public int updateSalaryByThreshold(BigDecimal threshold, double raisePercent) throws SQLException {
        String sql = "UPDATE employees SET Salary = Salary * (1 + ? / 100) WHERE Salary < ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, raisePercent);
            ps.setBigDecimal(2, threshold);
            return ps.executeUpdate();
        }
    }

    public List<Employee> getEmployeesBelowSalary(BigDecimal threshold) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE e.Salary < ? ORDER BY e.Salary";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setBigDecimal(1, threshold);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        }
        return list;
    }

    public void printJobTitles() throws SQLException {
        System.out.println("\n  Available Job Titles:");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT job_title_id, job_title FROM job_titles ORDER BY job_title_id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) System.out.printf("    [%d] %s%n", rs.getInt(1), rs.getString(2));
        }
    }

    public void printDivisions() throws SQLException {
        System.out.println("\n  Available Divisions:");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT ID, Name FROM division ORDER BY ID")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) System.out.printf("    [%d] %s%n", rs.getInt(1), rs.getString(2));
        }
    }
}
