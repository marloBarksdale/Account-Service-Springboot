package account.services;

import account.dto.AppUser;
import account.dto.UserAdapter;
import account.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService {
    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {


        AppUser user = userRepository.findAppUserByEmail(email.toLowerCase()).orElseThrow(()->new UsernameNotFoundException("Not found"));


        return new UserAdapter(user);
    }
}
