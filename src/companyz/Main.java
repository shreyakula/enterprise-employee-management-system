package companyz;

import companyz.controller.EmployeeController;
import companyz.controller.HRAdminController;
import companyz.controller.LoginController;
import companyz.model.User;
import companyz.util.DBConnection;
import companyz.util.InputUtil;

public class Main {

    public static void main(String[] args) {

        System.out.println("\n  Connecting to Company Z database...");
        try {
            DBConnection.getConnection();
            System.out.println("  Database connection established.\n");
        } catch (Exception e) {
            System.out.println("  ERROR: Could not connect to database.");
            System.out.println("  " + e.getMessage());
            System.out.println("\n  Please ensure MySQL is running and 'companyz' database exists.");
            System.exit(1);
        }

        LoginController loginCtrl = new LoginController();

        boolean appRunning = true;

        while (appRunning) {

            User user = loginCtrl.login();

            if (user == null) {
                appRunning = false;
            } else {

        
                if (user.getEmpID() == 1 || user.getEmpID() == 0) {
                    System.out.println("\n  [DEBUG] Launching HR Admin Menu...\n");
                    new HRAdminController(user).run();
                } else {
                    System.out.println("\n  [DEBUG] Launching General Employee Menu...\n");
                    new EmployeeController(user).run();
                }

                // ask for next login
                boolean again = InputUtil.readYesNo("\n  Would you like to log in as a different user?");
                if (!again) {
                    appRunning = false;
                }
            }
        }

        System.out.println("\n  Thank you for using Company Z Employee Management System. Goodbye!");
        DBConnection.closeConnection();
    }
}