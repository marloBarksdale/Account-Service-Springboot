package account.repositories;

import account.dto.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {


    Optional<Payment> findPaymentByEmployee(String employeeEmail);

    Optional<Payment> findByEmployeeAndPeriod(String employee, LocalDate period);


    Optional<List<Payment>> findPaymentsByEmployeeAndPeriod(String employee, LocalDate period);


    Optional<List<Payment>> findPaymentsByEmployeeOrderByPeriodDesc(String employee);

}
