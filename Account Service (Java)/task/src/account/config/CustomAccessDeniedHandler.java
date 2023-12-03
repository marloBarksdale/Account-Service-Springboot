package account.config;

import account.dto.SecurityEvent;
import account.dto.UserAdapter;
import account.exceptions.CustomErrorMessage;
import account.repositories.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component


public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    private final EventRepository eventRepository;


    public CustomAccessDeniedHandler(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        UserAdapter activeUser = (UserAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        eventRepository.save(new SecurityEvent("ACCESS_DENIED", activeUser.getEmail(), request.getRequestURI(), request.getRequestURI()));

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        CustomErrorMessage jsonResponse = new CustomErrorMessage(403, LocalDateTime.now(), "Forbidden", "Access Denied!", request.getRequestURI());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), jsonResponse);
    }


}