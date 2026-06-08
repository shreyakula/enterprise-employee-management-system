package companyz.service;

import companyz.dao.EmployeeDAO;
import companyz.model.Employee;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO dao = new EmployeeDAO();

    public Employee searchByEmpID(int empID) throws SQLException { return dao.getByEmpID(empID); }
    public List<Employee> searchByName(String name) throws SQLException { return dao.searchByName(name); }
    public List<Employee> searchBySSN(String ssn) throws SQLException { return dao.searchBySSN(ssn); }
    public List<Employee> getAllEmployees() throws SQLException { return dao.getAllEmployees(); }

    public String updateEmployee(int empID, String fname, String lname, String email,
                                  BigDecimal salary, Date hireDate, String ssn) throws SQLException {
        if (fname == null || fname.isBlank()) return "Invalid input for First Name.";
        if (lname == null || lname.isBlank()) return "Invalid input for Last Name.";
        if (email == null || !email.contains("@")) return "Invalid input for Email. Must contain '@'.";
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) return "Invalid input for Salary. Salary must be greater than 0.";
        if (hireDate == null) return "Invalid input for Hire Date.";
        if (dao.getByEmpID(empID) == null) return "No employee found with empID " + empID + ". No changes were made.";
        dao.updateEmployee(empID, fname, lname, email, salary, hireDate, ssn);
        return null;
    }

    public String deleteEmployee(int empID) throws SQLException {
        if (dao.getByEmpID(empID) == null) return "No employee found with empID " + empID + ". No action taken.";
        dao.deleteEmployee(empID);
        return null;
    }

    public int addEmployee(String fname, String lname, String email, Date hireDate,
                            BigDecimal salary, String ssn, int addressID,
                            int jobTitleID, int divID) throws SQLException {
        return dao.addEmployee(fname, lname, email, hireDate, salary, ssn, addressID, jobTitleID, divID);
    }

    public String updateSalaryByThreshold(BigDecimal threshold, double raisePercent) throws SQLException {
        if (threshold == null || threshold.compareTo(BigDecimal.ZERO) <= 0)
            return "Invalid input. Salary threshold must be greater than 0.";
        if (raisePercent <= 0)
            return "Invalid input. Raise percentage must be greater than 0.";
        int rows = dao.updateSalaryByThreshold(threshold, raisePercent);
        return rows == 0 ? "NO_MATCH" : String.valueOf(rows);
    }

    public List<Employee> getEmployeesBelowSalary(BigDecimal threshold) throws SQLException {
        return dao.getEmployeesBelowSalary(threshold);
    }

    public void printJobTitles() throws SQLException { dao.printJobTitles(); }
    public void printDivisions() throws SQLException { dao.printDivisions(); }
}
