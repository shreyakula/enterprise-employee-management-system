package companyz.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Employee {
    private int empID;
    private String fname;
    private String lname;
    private String email;
    private Date hireDate;
    private BigDecimal salary;
    private String ssn;
    private int addressID;
    private String jobTitle;
    private String divisionName;

    public Employee() {}

    public int getEmpID() { return empID; }
    public void setEmpID(int empID) { this.empID = empID; }
    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }
    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }
    public String getFullName() { return fname + " " + lname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }
    public int getAddressID() { return addressID; }
    public void setAddressID(int addressID) { this.addressID = addressID; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getDivisionName() { return divisionName; }
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }

    // Unused address fields kept for compatibility
    public void setStreet(String s) {}
    public String getStreet() { return ""; }
    public void setCity(String s) {}
    public void setState(String s) {}
    public void setZip(String s) {}
    public void setDob(java.sql.Date d) {}
    public void setMobilePhone(String s) {}
    public void setEmergencyContactName(String s) {}
    public void setEmergencyContactPhone(String s) {}
    public String getMobilePhone() { return ""; }
    public String getEmergencyContactName() { return ""; }
    public String getEmergencyContactPhone() { return ""; }

    @Override
    public String toString() {
        return String.format(
            "\n  Employee ID : %d\n  Name        : %s\n  Email       : %s\n" +
            "  Hire Date   : %s\n  Salary      : $%.2f\n  SSN         : %s\n" +
            "  Address ID  : %d\n  Job Title   : %s\n  Division    : %s",
            empID, getFullName(), email,
            hireDate, salary,
            ssn != null ? "***-**-" + (ssn.length() >= 4 ? ssn.substring(ssn.length()-4) : ssn) : "N/A",
            addressID,
            jobTitle != null ? jobTitle : "N/A",
            divisionName != null ? divisionName : "N/A"
        );
    }
}
