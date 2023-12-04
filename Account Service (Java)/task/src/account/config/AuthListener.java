package account.config;

import account.dto.UserAdapter;
import account.repositories.UserRepository;
import account.services.UserDetailsServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthListener {

    private UserDetailsServiceImpl userDetailsService;
    private UserRepository userRepository;

    public AuthListener(UserDetailsServiceImpl userDetailsService, UserRepository userRepository) {

        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }


    @EventListener
    @Transactional
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {


        UserAdapter user = (UserAdapter) event.getAuthentication().getPrincipal();


        userDetailsService.resetFailedAttempts(user.getEmail());


        userRepository.save(user.getUser());

    }



}
