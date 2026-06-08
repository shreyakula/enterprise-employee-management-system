package companyz.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Scanner;

public class InputUtil {
    private static final Scanner sc = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a whole number.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a number.");
            }
        }
    }

    public static BigDecimal readDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a decimal number (e.g. 50000.00).");
            }
        }
    }

    public static Date readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = sc.nextLine().trim();
            try {
                return Date.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid date. Use format YYYY-MM-DD.");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equals("Y")) return true;
            if (input.equals("N")) return false;
            System.out.println("  Please enter Y or N.");
        }
    }

    public static void pressEnter() {
        System.out.print("\n  Press ENTER to continue...");
        sc.nextLine();
    }
}
