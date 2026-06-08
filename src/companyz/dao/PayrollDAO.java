package companyz.dao;

import companyz.model.Payroll;
import companyz.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    public List<Payroll> getPayHistoryByEmployee(int empID) throws SQLException {
        String sql = "SELECT DISTINCT p.*, CONCAT(e.Fname,' ',e.Lname) AS empName FROM payroll p " +
                     "JOIN employees e ON p.empid = e.empid " +
                     "WHERE p.empid = ? ORDER BY p.pay_date DESC";
        List<Payroll> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, empID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        }
        return list;
    }

    public List<Payroll> getAllPayHistory() throws SQLException {
        String sql = "SELECT DISTINCT p.*, CONCAT(e.Fname,' ',e.Lname) AS empName FROM payroll p " +
                     "JOIN employees e ON p.empid = e.empid ORDER BY p.pay_date DESC";
        List<Payroll> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        }
        return list;
    }

    public void printMonthlyTotalByJobTitle(int year, int month) throws SQLException {
        String sql = "SELECT jt.job_title, SUM(p.earnings) AS total_earnings, COUNT(DISTINCT p.empid) AS num_employees " +
                     "FROM payroll p " +
                     "JOIN employees e ON p.empid = e.empid " +
                     "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                     "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
                     "WHERE YEAR(p.pay_date) = ? AND MONTH(p.pay_date) = ? " +
                     "GROUP BY jt.job_title ORDER BY total_earnings DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%n  %-30s %10s %12s%n", "Job Title", "Employees", "Total Pay");
            System.out.println("  " + "-".repeat(54));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  %-30s %10d %12.2f%n",
                    rs.getString("job_title"),
                    rs.getInt("num_employees"),
                    rs.getDouble("total_earnings"));
            }
            if (!found) System.out.println("  No payroll data found for " + year + "-" + month + ".");
        }
    }

    public void printMonthlyTotalByDivision(int year, int month) throws SQLException {
        String sql = "SELECT d.Name AS division, SUM(p.earnings) AS total_earnings, COUNT(DISTINCT p.empid) AS num_employees " +
                     "FROM payroll p " +
                     "JOIN employees e ON p.empid = e.empid " +
                     "JOIN employee_division ed ON e.empid = ed.empid " +
                     "JOIN division d ON ed.div_ID = d.ID " +
                     "WHERE YEAR(p.pay_date) = ? AND MONTH(p.pay_date) = ? " +
                     "GROUP BY d.Name ORDER BY total_earnings DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%n  %-30s %10s %12s%n", "Division", "Employees", "Total Pay");
            System.out.println("  " + "-".repeat(54));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  %-30s %10d %12.2f%n",
                    rs.getString("division"),
                    rs.getInt("num_employees"),
                    rs.getDouble("total_earnings"));
            }
            if (!found) System.out.println("  No payroll data found for " + year + "-" + month + ".");
        }
    }

    public void printNewHiresByDateRange(String startDate, String endDate) throws SQLException {
        String sql = "SELECT e.empid, e.Fname, e.Lname, e.email, e.HireDate, jt.job_title, d.Name AS division " +
                     "FROM employees e " +
                     "LEFT JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                     "LEFT JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
                     "LEFT JOIN employee_division ed ON e.empid = ed.empid " +
                     "LEFT JOIN division d ON ed.div_ID = d.ID " +
                     "WHERE e.HireDate BETWEEN ? AND ? ORDER BY e.HireDate";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            System.out.printf("%n  %-5s %-25s %-30s %-25s %-12s%n", "ID", "Name", "Email", "Job Title", "Hire Date");
            System.out.println("  " + "-".repeat(97));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("  %-5d %-25s %-30s %-25s %-12s%n",
                    rs.getInt("empid"),
                    rs.getString("Fname") + " " + rs.getString("Lname"),
                    rs.getString("email"),
                    rs.getString("job_title") != null ? rs.getString("job_title") : "N/A",
                    rs.getDate("HireDate"));
            }
            if (!found) System.out.println("  No new hires found between " + startDate + " and " + endDate + ".");
        }
    }

    private Payroll mapResultSet(ResultSet rs) throws SQLException {
        Payroll p = new Payroll();
        p.setPayID(rs.getInt("payID"));
        p.setPayDate(rs.getDate("pay_date"));
        p.setEarnings(rs.getBigDecimal("earnings"));
        p.setFedTax(rs.getBigDecimal("fed_tax"));
        p.setFedMed(rs.getBigDecimal("fed_med"));
        p.setFedSS(rs.getBigDecimal("fed_SS"));
        p.setStateTax(rs.getBigDecimal("state_tax"));
        p.setRetire401k(rs.getBigDecimal("retire_401k"));
        p.setHealthCare(rs.getBigDecimal("health_care"));
        p.setEmpID(rs.getInt("empid"));
        p.setEmpName(rs.getString("empName"));
        return p;
    }
}