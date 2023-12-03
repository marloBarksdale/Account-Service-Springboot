package account.controllers;


import account.dto.AppUser;
import account.dto.Group;
import account.dto.SecurityEvent;
import account.dto.UserAdapter;
import account.repositories.EventRepository;
import account.repositories.GroupRepository;
import account.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AdminController {


    private UserRepository userRepository;


    private GroupRepository groupRepository;

    private EventRepository eventRepository;


    public AdminController(UserRepository userRepository, GroupRepository groupRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/api/admin/**")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")

    public ResponseEntity<List<AppUser>> getAllUsersForAdmin() {


        List<AppUser> body = userRepository.findAllByOrderByIdAsc().orElse(new ArrayList<>());
//        body = body.stream().filter(x -> !((new UserAdapter(x))).getAuthoritiesList().contains("ADMINISTRATOR")).toList();
        return ResponseEntity.ok(body);
    }


    @DeleteMapping("/api/admin/user/{email}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @Transactional

    public ResponseEntity<?> deleteUserByEmail(@PathVariable @Valid @Email(regexp = ".*@acme.com") String email, @AuthenticationPrincipal UserAdapter user, HttpServletRequest request) {


        if (user.getEmail().equals(email)) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }


        if (userRepository.deleteByEmailIgnoreCase(email) == 1) {


            eventRepository.save(new SecurityEvent("DELETE_USER", user.getEmail(), email, request.getRequestURI()));

            return ResponseEntity.ok(new CustomResponse(email, "Deleted successfully!"));
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");

    }


    @PutMapping("/api/admin/user/role")
    @Transactional
    public ResponseEntity<?> changeUserRole(@RequestBody RoleUpdate roleUpdate, @AuthenticationPrincipal UserAdapter subject, HttpServletRequest request) {


        String operation = roleUpdate.operation.toUpperCase();
        Group group = groupRepository.findByCode(roleUpdate.role.toLowerCase());
        String email = roleUpdate.user;

        if (!("GRANT".equals(operation) || "REMOVE".equals(operation))) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Operation!");
        }


        if (group == null) {

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        String role = group.getCode();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);

        AppUser user = userRepository.findAppUserByEmailIgnoreCase(email).orElse(null);


        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        var userRoles = user.getUserGroups();
        var adaptedUser = new UserAdapter(user);
        if ("REMOVE".equals(operation)) {


            if (!adaptedUser.getAuthoritiesList().contains(simpleGrantedAuthority.getAuthority().toUpperCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
            }


            if ("ADMINISTRATOR".equalsIgnoreCase(simpleGrantedAuthority.getAuthority())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            }

            if (userRoles.size() == 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
            }


            user.getUserGroupsSet().remove(group);

            String object = "Remove role " + role.toUpperCase() + " from " + user.getEmail();


            eventRepository.save(new SecurityEvent("REMOVE_ROLE", subject.getEmail(), object, request.getRequestURI()));

        }


        if ("GRANT".equals(operation)) {


            if (adaptedUser.getAuthoritiesList().contains(simpleGrantedAuthority.getAuthority().toUpperCase())) {

                return ResponseEntity.ok(user);
            }


            if (adaptedUser.getAuthoritiesList().contains(new SimpleGrantedAuthority("ADMINISTRATOR").getAuthority()) && !"ADMINISTRATOR".equalsIgnoreCase(simpleGrantedAuthority.getAuthority())) {


                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");

            } else {

                if ("ADMINISTRATOR".equalsIgnoreCase(simpleGrantedAuthority.getAuthority()))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");

            }

            user.addUserGroups(group);

            String object = "Grant role " + role.toUpperCase() + " to " + user.getEmail();

            eventRepository.save(new SecurityEvent("GRANT_ROLE", subject.getEmail(), object, request.getRequestURI()));


        }


        user = userRepository.save(user);


        return ResponseEntity.ok(user);

    }


    record CustomResponse(String user, String status) {
    }


    public record RoleUpdate(String user, String role, String operation) {


    }

}
