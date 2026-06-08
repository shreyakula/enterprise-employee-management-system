package companyz.service;

import companyz.dao.PayrollDAO;
import companyz.model.Payroll;

import java.sql.SQLException;
import java.util.List;

public class PayrollService {
    private final PayrollDAO dao = new PayrollDAO();

    public List<Payroll> getPayHistory(int empID) throws SQLException {
        return dao.getPayHistoryByEmployee(empID);
    }

    public List<Payroll> getAllPayHistory() throws SQLException {
        return dao.getAllPayHistory();
    }

    public void printMonthlyByJobTitle(int year, int month) throws SQLException {
        dao.printMonthlyTotalByJobTitle(year, month);
    }

    public void printMonthlyByDivision(int year, int month) throws SQLException {
        dao.printMonthlyTotalByDivision(year, month);
    }

    public void printNewHires(String startDate, String endDate) throws SQLException {
        dao.printNewHiresByDateRange(startDate, endDate);
    }
}
