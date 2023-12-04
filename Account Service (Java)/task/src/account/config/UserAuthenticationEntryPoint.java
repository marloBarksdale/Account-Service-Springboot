package account.config;

import account.dto.AppUser;
import account.dto.SecurityEvent;
import account.dto.UserAdapter;
import account.exceptions.CustomErrorMessage;
import account.repositories.EventRepository;
import account.repositories.UserRepository;
import account.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
@Service
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Serial
    private static final long serialVersionUID = -8970718410437077606L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final UserDetailsServiceImpl userDetailsService;


    public UserAuthenticationEntryPoint(EventRepository eventRepository, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {

        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String subject = "";
        if (!(request.getHeader("Authorization") == null)) {
            String code = request.getHeader("Authorization").substring(6);
            byte[] decoded = Base64.getDecoder().decode(code);
            String res = new String(decoded);
            subject = res.split(":")[0];
        }


        AppUser user = userRepository.findAppUserByEmailIgnoreCase(subject).orElse(null);


        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.findAndRegisterModules();

        if (user != null) {
            UserAdapter userAdapter = new UserAdapter(user);

            if (userAdapter.isEnabled() && user.isAccountNonLocked()) {

                if (user.getFailedAttempt() < UserDetailsServiceImpl.MAX_FAILED_ATTEMPTS) {
                    userDetailsService.increaseFailedAttempts(user);
                    eventRepository.save(new SecurityEvent("LOGIN_FAILED", user.getEmail(), request.getRequestURI(), request.getRequestURI()));
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    OBJECT_MAPPER.writeValue(response.getOutputStream(), new CustomErrorMessage(401, LocalDateTime.now(), "Unauthorized", "Unauthorized", request.getRequestURI()));
                    return;
                } else {


                    if (user.getUserGroups().contains("ROLE_ADMINISTRATOR")) {

                        userDetailsService.resetFailedAttempts(user.getEmail());

                    }
                    userDetailsService.lock(user);
                    eventRepository.save(new SecurityEvent("LOGIN_FAILED", user.getEmail(), request.getRequestURI(), request.getRequestURI()));
                    eventRepository.save(new SecurityEvent("BRUTE_FORCE", user.getEmail(), request.getRequestURI(), request.getRequestURI()));
                    eventRepository.save(new SecurityEvent("LOCK_USER", user.getEmail(), "Lock user "+user.getEmail(), request.getRequestURI()));


                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    OBJECT_MAPPER.writeValue(response.getOutputStream(), new CustomErrorMessage(401, LocalDateTime.now(), "Unauthorized", "Unauthorized", request.getRequestURI()));
                    return;
                }


            } else if (!user.isAccountNonLocked()) {

                if (userDetailsService.unlockWhenTimeExpired(user)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    OBJECT_MAPPER.writeValue(response.getOutputStream(), new CustomErrorMessage(401, LocalDateTime.now(), "Unauthorized", "Unauthorized", request.getRequestURI()));
                }


                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                OBJECT_MAPPER.writeValue(response.getOutputStream(), new CustomErrorMessage(401, LocalDateTime.now(), "Unauthorized", "User account is locked", request.getRequestURI()));
                return;


            }
        } else if (!subject.isEmpty() && !subject.isBlank()) {

            eventRepository.save(new SecurityEvent("LOGIN_FAILED", subject, request.getRequestURI(), request.getRequestURI()));
        }


        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OBJECT_MAPPER.writeValue(response.getOutputStream(), new CustomErrorMessage(401, LocalDateTime.now(), "Unauthorized", "Unauthorized", request.getRequestURI()));
    }


}
