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
import org.apache.catalina.authenticator.BasicAuthenticator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Serial
    private static final long serialVersionUID = -8970718410437077606L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final EventRepository eventRepository;


    public UserAuthenticationEntryPoint(EventRepository eventRepository) {

        this.eventRepository = eventRepository;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String subject = "Anonymous";
if(!(request.getHeader("Authorization")==null)){
    String code = request.getHeader("Authorization").substring(6);
    byte []decoded= Base64.getDecoder().decode(code);
    String res = new String(decoded);
    subject = res.split(":")[0];
}



        eventRepository.save(new SecurityEvent("LOGIN_FAILED",subject, request.getRequestURI(), request.getRequestURI()));



        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.findAndRegisterModules();

        OBJECT_MAPPER.writeValue(response.getOutputStream(), new CustomErrorMessage(401, LocalDateTime.now(), "Unauthorized", "Unauthorized", request.getRequestURI()));
    }
}
