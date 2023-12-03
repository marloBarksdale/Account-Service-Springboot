package account.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.YearMonth;


@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"period", "employee"}))
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long payment_id;

    @NotEmpty
    @NotNull
    private String employee;


    @JsonFormat(pattern = "MM-yyyy")
    @NotNull
    private LocalDate period;


    @Min(value = 0, message = "Salary must be non negative")

    @NotNull
    private Long salary;


    public Payment() {
    }

    public Payment(String employee, LocalDate period, Long salary) {

        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public long getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(long payment_id) {
        this.payment_id = payment_id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPeriod() {


        return capitalize(this.period.getMonth().toString()) + "-" + this.period.getYear();
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public LocalDate getTruePeriod() {

        return this.period;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Long getLongSalary() {
        return this.salary;
    }

    public void setPeriod(YearMonth period) {


        this.period = LocalDate.of(period.getYear(), period.getMonth(), 1);
    }

    public String getSalary() {


        return this.salary / 100 + " dollar(s) " + this.salary % 100 + " cent(s)";
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }


    @Override
    public String toString() {
        return "PaymentsDto{" + "payment_id=" + payment_id + ", employee='" + employee + '\'' + ", period=" + period + ", salary=" + salary + '}';
    }


    public String capitalize(String inputString) {


        inputString = inputString.toLowerCase();
        // get the first character of the inputString
        char firstLetter = inputString.charAt(0);

        // convert it to an UpperCase letter
        char capitalFirstLetter = Character.toUpperCase(firstLetter);

        // return the output string by updating
        //the first char of the input string
        return inputString.replace(inputString.charAt(0), capitalFirstLetter);
    }
}
