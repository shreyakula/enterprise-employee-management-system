package companyz.controller;

import companyz.model.Employee;
import companyz.model.Payroll;
import companyz.model.User;
import companyz.service.EmployeeService;
import companyz.service.PayrollService;
import companyz.util.InputUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class HRAdminController {
    private final EmployeeService empService = new EmployeeService();
    private final PayrollService payService = new PayrollService();
    private final User user;

    public HRAdminController(User user) { this.user = user; }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = InputUtil.readInt("  Enter choice: ");
            System.out.println();
            try {
                switch (choice) {
                    case 1 -> searchEmployee();
                    case 2 -> updateEmployee();
                    case 3 -> deleteEmployee();
                    case 4 -> addEmployee();
                    case 5 -> updateSalaryByThreshold();
                    case 6 -> viewAllPayHistory();
                    case 7 -> reportMonthlyByJobTitle();
                    case 8 -> reportMonthlyByDivision();
                    case 9 -> reportNewHires();
                    case 0 -> { running = false; System.out.println("  Logging out..."); }
                    default -> System.out.println("  Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("  Database error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║    COMPANY Z - HR ADMIN MENU         ║");
        System.out.printf( "║  Logged in as: %-22s║%n", user.getFullName());
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. Search Employee                  ║");
        System.out.println("║  2. Update Employee Info             ║");
        System.out.println("║  3. Delete Employee                  ║");
        System.out.println("║  4. Add New Employee                 ║");
        System.out.println("║  5. Update Salary by Threshold       ║");
        System.out.println("║  6. View All Pay Statement History   ║");
        System.out.println("║  7. Monthly Total Pay by Job Title   ║");
        System.out.println("║  8. Monthly Total Pay by Division    ║");
        System.out.println("║  9. New Hires by Date Range          ║");
        System.out.println("║  0. Logout                           ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private void searchEmployee() throws SQLException {
        System.out.println("  ─── Search Employee ───");
        System.out.println("  1. By Employee ID");
        System.out.println("  2. By Name");
        System.out.println("  3. By SSN");
        int choice = InputUtil.readInt("  Choose search type: ");

        switch (choice) {
            case 1 -> {
                int id = InputUtil.readInt("  Enter Employee ID: ");
                Employee e = empService.searchByEmpID(id);
                if (e == null) System.out.println("  No employee found with empID " + id + ".");
                else { System.out.println("\n  ─── Employee Found ───"); System.out.println(e); }
            }
            case 2 -> {
                String name = InputUtil.readLine("  Enter name (or partial): ");
                List<Employee> results = empService.searchByName(name);
                if (results.isEmpty()) System.out.println("  No employees found.");
                else { System.out.println("\n  ─── Results (" + results.size() + ") ───"); results.forEach(System.out::println); }
            }
            case 3 -> {
                String ssn = InputUtil.readLine("  Enter SSN: ");
                List<Employee> results = empService.searchBySSN(ssn);
                if (results.isEmpty()) System.out.println("  No employees found.");
                else { System.out.println("\n  ─── Results (" + results.size() + ") ───"); results.forEach(System.out::println); }
            }
            default -> System.out.println("  Invalid choice.");
        }
        InputUtil.pressEnter();
    }

    private void updateEmployee() throws SQLException {
        System.out.println("  ─── Update Employee Info ───");
        int empID = InputUtil.readInt("  Enter Employee ID to update: ");
        Employee emp = empService.searchByEmpID(empID);
        if (emp == null) {
            System.out.println("  No employee found with empID " + empID + ". No changes were made.");
            InputUtil.pressEnter(); return;
        }
        System.out.println("\n  Current record:" + emp);
        System.out.println("\n  Enter new values (press ENTER to keep current):");

        String fname = promptOrKeep("  First Name [" + emp.getFname() + "]: ", emp.getFname());
        String lname = promptOrKeep("  Last Name [" + emp.getLname() + "]: ", emp.getLname());
        String email = promptOrKeep("  Email [" + emp.getEmail() + "]: ", emp.getEmail());
        String salaryStr = promptOrKeep("  Salary [" + emp.getSalary() + "]: ", emp.getSalary().toString());
        String hireDateStr = promptOrKeep("  Hire Date [" + emp.getHireDate() + "]: ", emp.getHireDate().toString());
        String ssn = promptOrKeep("  SSN [" + emp.getSsn() + "]: ", emp.getSsn());

        BigDecimal salary;
        try { salary = new BigDecimal(salaryStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid salary."); InputUtil.pressEnter(); return; }

        Date hireDate;
        try { hireDate = Date.valueOf(hireDateStr); }
        catch (IllegalArgumentException e) { System.out.println("  Invalid date."); InputUtil.pressEnter(); return; }

        String error = empService.updateEmployee(empID, fname, lname, email, salary, hireDate, ssn);
        if (error != null) {
            System.out.println("  " + error);
        } else {
            System.out.println("  Employee record for " + fname + " " + lname + " (empID: " + empID + ") updated successfully.");
            System.out.println(empService.searchByEmpID(empID));
        }
        InputUtil.pressEnter();
    }

    private void deleteEmployee() throws SQLException {
        System.out.println("  ─── Delete Employee ───");
        int empID = InputUtil.readInt("  Enter Employee ID to delete: ");
        Employee emp = empService.searchByEmpID(empID);
        if (emp == null) {
            System.out.println("  No employee found with empID " + empID + ". No action taken.");
            InputUtil.pressEnter(); return;
        }
        System.out.println("\n  Employee found:" + emp);
        boolean confirm = InputUtil.readYesNo("\n  Are you sure you want to delete this employee?");
        if (!confirm) {
            System.out.println("  Deletion cancelled. No changes were made.");
            InputUtil.pressEnter(); return;
        }
        String error = empService.deleteEmployee(empID);
        if (error != null) System.out.println("  " + error);
        else System.out.println("  Employee " + emp.getFullName() + " (empID: " + empID + ") has been successfully deleted.");
        InputUtil.pressEnter();
    }

    private void addEmployee() throws SQLException {
        System.out.println("  ─── Add New Employee ───");
        System.out.println("  (Employee ID will be auto-generated)\n");

        String fname = InputUtil.readLine("  First Name: ");
        String lname = InputUtil.readLine("  Last Name: ");
        String email = InputUtil.readLine("  Email: ");
        Date hireDate = InputUtil.readDate("  Hire Date");
        BigDecimal salary = InputUtil.readDecimal("  Salary: ");
        String ssn = InputUtil.readLine("  SSN (###-##-####): ");
        int addressID = InputUtil.readInt("  Address ID (use existing, e.g. 1): ");

        empService.printJobTitles();
        int jobTitleID = InputUtil.readInt("  Job Title ID: ");

        empService.printDivisions();
        int divID = InputUtil.readInt("  Division ID: ");

        int newID = empService.addEmployee(fname, lname, email, hireDate, salary, ssn, addressID, jobTitleID, divID);
        System.out.println("\n  New employee added! Auto-generated Employee ID: " + newID);
        System.out.println(empService.searchByEmpID(newID));
        InputUtil.pressEnter();
    }

    private void updateSalaryByThreshold() throws SQLException {
        System.out.println("  ─── Update Salary by Threshold ───");
        BigDecimal threshold = InputUtil.readDecimal("  Enter salary threshold (e.g. 50000): ");
        double percent = InputUtil.readDouble("  Enter raise percentage (e.g. 10 for 10%): ");

        List<Employee> affected = empService.getEmployeesBelowSalary(threshold);
        if (affected.isEmpty()) {
            System.out.println("  No employees found with salary less than $" + threshold + ". No updates were made.");
            InputUtil.pressEnter(); return;
        }

        System.out.println("\n  " + affected.size() + " employee(s) will receive a " + percent + "% raise:");
        System.out.printf("  %-5s %-25s %12s%n", "ID", "Name", "Current Salary");
        System.out.println("  " + "-".repeat(44));
        for (Employee e : affected)
            System.out.printf("  %-5d %-25s %12.2f%n", e.getEmpID(), e.getFullName(), e.getSalary());

        boolean confirm = InputUtil.readYesNo("\n  Confirm salary update?");
        if (!confirm) { System.out.println("  Update cancelled."); InputUtil.pressEnter(); return; }

        String result = empService.updateSalaryByThreshold(threshold, percent);
        if (result.startsWith("Invalid")) {
            System.out.println("  " + result);
        } else if (result.equals("NO_MATCH")) {
            System.out.println("  No employees found below threshold. No updates were made.");
        } else {
            System.out.println("  " + result + " employee(s) received a " + percent + "% salary increase.");
            System.out.printf("%n  %-5s %-25s %12s%n", "ID", "Name", "New Salary");
            System.out.println("  " + "-".repeat(44));
            for (Employee e : affected) {
                BigDecimal newSalary = e.getSalary().multiply(BigDecimal.valueOf(1 + percent / 100));
                System.out.printf("  %-5d %-25s %12.2f%n", e.getEmpID(), e.getFullName(), newSalary);
            }
        }
        InputUtil.pressEnter();
    }

    private void viewAllPayHistory() throws SQLException {
        System.out.println("  ─── All Employee Pay Statement History ───");
        List<Payroll> records = payService.getAllPayHistory();
        if (records.isEmpty()) { System.out.println("  No payroll records found."); }
        else {
            System.out.printf("  %-8s %-25s %-12s %12s %12s%n", "Pay ID", "Employee", "Pay Date", "Earnings", "Net Pay");
            System.out.println("  " + "-".repeat(71));
            for (Payroll p : records)
                System.out.printf("  %-8d %-25s %-12s %12.2f %12.2f%n",
                    p.getPayID(), p.getEmpName(), p.getPayDate(), p.getEarnings(), p.getNetPay());
        }
        InputUtil.pressEnter();
    }

    private void reportMonthlyByJobTitle() throws SQLException {
        System.out.println("  ─── Monthly Total Pay by Job Title ───");
        int year = InputUtil.readInt("  Enter year (e.g. 2026): ");
        int month = InputUtil.readInt("  Enter month (1-12): ");
        payService.printMonthlyByJobTitle(year, month);
        InputUtil.pressEnter();
    }

    private void reportMonthlyByDivision() throws SQLException {
        System.out.println("  ─── Monthly Total Pay by Division ───");
        int year = InputUtil.readInt("  Enter year (e.g. 2026): ");
        int month = InputUtil.readInt("  Enter month (1-12): ");
        payService.printMonthlyByDivision(year, month);
        InputUtil.pressEnter();
    }

    private void reportNewHires() throws SQLException {
        System.out.println("  ─── New Employee Hires by Date Range ───");
        String start = InputUtil.readLine("  Start date (YYYY-MM-DD): ");
        String end = InputUtil.readLine("  End date (YYYY-MM-DD): ");
        payService.printNewHires(start, end);
        InputUtil.pressEnter();
    }

    private String promptOrKeep(String prompt, String current) {
        String input = InputUtil.readLine(prompt);
        return input.isEmpty() ? current : input;
    }
}
