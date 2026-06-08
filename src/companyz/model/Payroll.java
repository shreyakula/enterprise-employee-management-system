package companyz.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Payroll {
    private int payID;
    private Date payDate;
    private BigDecimal earnings;
    private BigDecimal fedTax;
    private BigDecimal fedMed;
    private BigDecimal fedSS;
    private BigDecimal stateTax;
    private BigDecimal retire401k;
    private BigDecimal healthCare;
    private int empID;

    // Joined
    private String empName;

    public Payroll() {}

    public int getPayID() { return payID; }
    public void setPayID(int payID) { this.payID = payID; }

    public Date getPayDate() { return payDate; }
    public void setPayDate(Date payDate) { this.payDate = payDate; }

    public BigDecimal getEarnings() { return earnings; }
    public void setEarnings(BigDecimal earnings) { this.earnings = earnings; }

    public BigDecimal getFedTax() { return fedTax; }
    public void setFedTax(BigDecimal fedTax) { this.fedTax = fedTax; }

    public BigDecimal getFedMed() { return fedMed; }
    public void setFedMed(BigDecimal fedMed) { this.fedMed = fedMed; }

    public BigDecimal getFedSS() { return fedSS; }
    public void setFedSS(BigDecimal fedSS) { this.fedSS = fedSS; }

    public BigDecimal getStateTax() { return stateTax; }
    public void setStateTax(BigDecimal stateTax) { this.stateTax = stateTax; }

    public BigDecimal getRetire401k() { return retire401k; }
    public void setRetire401k(BigDecimal retire401k) { this.retire401k = retire401k; }

    public BigDecimal getHealthCare() { return healthCare; }
    public void setHealthCare(BigDecimal healthCare) { this.healthCare = healthCare; }

    public int getEmpID() { return empID; }
    public void setEmpID(int empID) { this.empID = empID; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public BigDecimal getNetPay() {
        BigDecimal deductions = fedTax.add(fedMed).add(fedSS).add(stateTax).add(retire401k).add(healthCare);
        return earnings.subtract(deductions);
    }

    @Override
    public String toString() {
        return String.format(
            "\n  Pay ID      : %d\n  Pay Date    : %s\n  Earnings    : $%.2f\n" +
            "  Fed Tax     : $%.2f\n  Fed Med     : $%.2f\n  Fed SS      : $%.2f\n" +
            "  State Tax   : $%.2f\n  401k        : $%.2f\n  Health Care : $%.2f\n" +
            "  Net Pay     : $%.2f",
            payID, payDate, earnings, fedTax, fedMed, fedSS,
            stateTax, retire401k, healthCare, getNetPay()
        );
    }
}
