package account.controllers;

import account.dto.Payment;
import account.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class UserExistConstraintValidator implements ConstraintValidator<UserExistConstraint, List<Payment>> {


    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserExistConstraint contactNumber) {


    }


    @Override
    public boolean isValid(List<Payment> values, ConstraintValidatorContext context) {


        for (var payment : values) {
            if (!userRepository.existsAppUserByEmail(payment.getEmployee())) {
                return false;
            }

        }
        return true;
    }


}