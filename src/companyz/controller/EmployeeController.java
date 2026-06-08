package companyz.controller;

import companyz.model.Employee;
import companyz.model.Payroll;
import companyz.model.User;
import companyz.service.EmployeeService;
import companyz.service.PayrollService;
import companyz.util.InputUtil;

import java.sql.SQLException;
import java.util.List;

public class EmployeeController {
    private final EmployeeService empService = new EmployeeService();
    private final PayrollService payService = new PayrollService();
    private final User user;

    public EmployeeController(User user) {
        this.user = user;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = InputUtil.readInt("  Enter choice: ");
            System.out.println();
            try {
                switch (choice) {
                    case 1 -> viewMyData();
                    case 2 -> searchMyData();
                    case 3 -> viewMyPayHistory();
                    case 0 -> { running = false; System.out.println("  Logging out..."); }
                    default -> System.out.println("  Invalid choice. Please try again.");
                }
            } catch (SQLException e) {
                System.out.println("  Database error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║    COMPANY Z - EMPLOYEE PORTAL       ║");
        System.out.printf( "║  Welcome, %-27s║%n", user.getFullName());
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1. View My Personal Data            ║");
        System.out.println("║  2. Search My Data                   ║");
        System.out.println("║  3. View My Pay History              ║");
        System.out.println("║  0. Logout                           ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private void viewMyData() throws SQLException {
        System.out.println("  ─── My Personal Information ───");
        Employee emp = empService.searchByEmpID(user.getEmpID());
        if (emp == null) System.out.println("  Unable to retrieve your data. Please contact HR.");
        else System.out.println(emp);
        InputUtil.pressEnter();
    }

    private void searchMyData() throws SQLException {
        System.out.println("  ─── Search My Data ───");
        System.out.println("  (You can only view your own record)");
        int id = InputUtil.readInt("  Enter your Employee ID: ");

        if (id != user.getEmpID()) {
            System.out.println("  Access denied. You may only view your own data.");
            InputUtil.pressEnter(); return;
        }

        Employee emp = empService.searchByEmpID(id);
        if (emp == null) System.out.println("  No record found with empID " + id + ".");
        else System.out.println(emp);
        InputUtil.pressEnter();
    }

    private void viewMyPayHistory() throws SQLException {
        System.out.println("  ─── My Pay History ───");
        List<Payroll> records = payService.getPayHistory(user.getEmpID());
        if (records.isEmpty()) {
            System.out.println("  No payroll records found for your account.");
        } else {
            System.out.println("  Pay statements sorted by most recent date:");
            System.out.printf("  %-8s %-12s %12s %12s%n", "Pay ID", "Pay Date", "Earnings", "Net Pay");
            System.out.println("  " + "-".repeat(46));
            for (Payroll p : records) {
                System.out.printf("  %-8d %-12s %12.2f %12.2f%n",
                    p.getPayID(), p.getPayDate(), p.getEarnings(), p.getNetPay());
            }
            System.out.println();
            // Show detail on request
            boolean detail = InputUtil.readYesNo("  View full detail of a pay statement?");
            if (detail) {
                int payID = InputUtil.readInt("  Enter Pay ID: ");
                records.stream().filter(p -> p.getPayID() == payID).findFirst()
                    .ifPresentOrElse(
                        p -> System.out.println(p),
                        () -> System.out.println("  Pay statement not found.")
                    );
            }
        }
        InputUtil.pressEnter();
    }
}
