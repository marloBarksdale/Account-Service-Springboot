package account.controllers;

import account.config.CheckPassword;
import account.dto.*;
import account.repositories.EventRepository;
import account.repositories.GroupRepository;
import account.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
public class AuthController {


    private UserRepository userRepository;

    private GroupRepository groupRepository;
    private PasswordEncoder passwordEncoder;


    private EventRepository eventRepository;


    public AuthController(UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventRepository = eventRepository;
    }

    @PostMapping("/api/auth/signup")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public ResponseEntity<?> signup(@Valid @RequestBody AppUser appUser, Errors errors, @AuthenticationPrincipal UserAdapter adaptedUser, HttpServletRequest request) {


        if (errors.hasErrors()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error");
        }

        if (CheckPassword.checkPassWords(appUser.getPassword(), passwordEncoder)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }


        if (userRepository.findAppUserByEmailIgnoreCase(appUser.getEmail().toLowerCase()).isPresent()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }


        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setEmail(appUser.getEmail().toLowerCase());
        appUser.setAccountNonLocked(true);


        if (userRepository.findAll().isEmpty()) {

            Group group = groupRepository.findByCode("administrator");
            appUser.addUserGroups(group);

        } else {
            updateCustomerGroup(appUser);
        }
        AppUser user = userRepository.save(appUser);

        eventRepository.save(new SecurityEvent("CREATE_USER", user.getEmail(), request.getRequestURI()));


        return ResponseEntity.ok(user);


    }

    @PostMapping("/api/auth/changepass")
    public ResponseEntity<?> changePass(@AuthenticationPrincipal UserAdapter userDetails, @RequestBody @Valid NewPassword newPassword, Errors errors, HttpServletRequest request) {


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
        eventRepository.save(new SecurityEvent("CHANGE_PASSWORD", userDetails.getEmail(), userDetails.getEmail(), request.getRequestURI()));


        return ResponseEntity.ok(new CustomResponse(userDetails.getEmail(), "The password has been updated successfully"));

    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllusers() {


        return ResponseEntity.ok(userRepository.findAll());
    }

    private void updateCustomerGroup(AppUser user) {
        Group group = groupRepository.findByCode("user");
        user.addUserGroups(group);
    }


    record CustomResponse(String email, String status) {
    }

    public record CustomStatus(String status) {

    }


}



