package account.controllers;

import account.config.CheckPassword;
import account.dto.NewPassword;
import account.dto.AppUser;
import account.dto.UserAdapter;
import account.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
public class AuthController {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/api/auth/signup")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public ResponseEntity<?> signup(@Valid @RequestBody AppUser appUser, Errors errors) {


        if (errors.hasErrors()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
        }

        if (CheckPassword.checkPassWords(appUser.getPassword(), passwordEncoder)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }


        if (userRepository.findAppUserByEmail(appUser.getEmail().toLowerCase()).isPresent()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }


        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setEmail(appUser.getEmail().toLowerCase());
        AppUser user = userRepository.save(appUser);


        return ResponseEntity.ok(user);


    }

    @PostMapping("/api/auth/changepass")
    public ResponseEntity<?> changePass(@AuthenticationPrincipal UserAdapter userDetails, @RequestBody @Valid NewPassword newPassword, Errors errors) {


        if (errors.hasErrors()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
        }

        if (CheckPassword.checkPassWords(newPassword.new_password(), passwordEncoder)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }


        if (passwordEncoder.matches(newPassword.new_password(), userDetails.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }


        userDetails.setPassword(passwordEncoder.encode(newPassword.getNew_password()));


        userRepository.save(userDetails.getUser());

        return ResponseEntity.ok(new CustomResponse(userDetails.getEmail(), "The password has been updated successfully"));

    }


    record CustomResponse(String email, String status) {
    }


    public record CustomStatus(String status) {

    }


}



