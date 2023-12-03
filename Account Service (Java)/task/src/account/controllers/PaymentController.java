package account.controllers;

import account.dto.Payment;
import account.dto.UserAdapter;
import account.repositories.PaymentRepository;
import account.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@Validated

public class PaymentController {


    private final PaymentRepository paymentRepository;
    private UserRepository userRepository;

    @JsonFormat(pattern = "MM-yyyy")
    private LocalDate searchPeriod;

    public PaymentController(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/acct/payments")
    @PreAuthorize("hasAuthority('ACCOUNTANT')")
    public ResponseEntity<?> savePayments(@RequestBody(required = false) List<Payment> payments, Errors errors) {

        if (errors.hasErrors()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
        }


        paymentRepository.saveAll(payments);


        return ResponseEntity.ok(new AuthController.CustomStatus("Added successfully!"));
    }


//    @GetMapping("/api/acct/payments")
//    @PreAuthorize("hasAuthority('ACCOUNTANT')")
//
//    public ResponseEntity<?> getPayments(@AuthenticationPrincipal UserAdapter user) {
//
//
//        List<Payment> payments = paymentRepository.findAll();
//
//
//        return ResponseEntity.ok(payments);
//    }


    @PutMapping("/api/acct/payments")
    @PreAuthorize("hasAuthority('ACCOUNTANT')")
    public ResponseEntity<?> updateSalary(@Valid @RequestBody Payment paymentUpdate) {


        var payment = paymentRepository.findByEmployeeAndPeriod(paymentUpdate.getEmployee(), paymentUpdate.getTruePeriod());


        if (payment.isPresent()) {


            payment.get().setSalary(paymentUpdate.getLongSalary());

            paymentRepository.save(payment.get());

            return ResponseEntity.ok(new AuthController.CustomStatus("Updated successfully!"));
        }


        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The combination of user and period does not exist");
    }


    @GetMapping("/api/empl/payment")
    @PreAuthorize("hasAnyAuthority('USER', 'ACCOUNTANT')")
    public ResponseEntity<?> getPaymentInfo(@AuthenticationPrincipal UserAdapter user, @DateTimeFormat(pattern = "MM-yyyy") YearMonth period) {


        if (period == null) {

            List<Payment> payments = paymentRepository.findPaymentsByEmployeeOrderByPeriodDesc(user.getEmail()).get();


            return ResponseEntity.ok(payments.stream().map(p -> new PaymentResponse(user.getUsername(), user.getLastname(), p.getPeriod(), p.getSalary())));
        } else {

            setPeriod(period);

            var payment = paymentRepository.findByEmployeeAndPeriod(user.getEmail(), searchPeriod);
            if (payment.isPresent())

                return ResponseEntity.ok(new PaymentResponse(user.getUsername(), user.getLastname(), payment.get().getPeriod(), payment.get().getSalary()));

            else
                return ResponseEntity.ok(null);
        }


    }


    public LocalDate getLocalDate(String period) {

        var data = period.split("-");

        int month = Integer.parseInt(data[0]);
        int year = Integer.parseInt(data[1]);


        return LocalDate.of(year, month, 1);


    }

    @JsonFormat(pattern = "MM-yyyy")
    public void setPeriod(@JsonFormat(pattern = "MM-yyyy") YearMonth period) {


        this.searchPeriod = LocalDate.of(period.getYear(), period.getMonth(), 1);
    }

    public record PaymentResponse(String name, String lastname, String period, String salary) {
    }

}




