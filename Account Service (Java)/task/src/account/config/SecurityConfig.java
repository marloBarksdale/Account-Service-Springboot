package account.config;

import account.repositories.EventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;


    public SecurityConfig(CustomAccessDeniedHandler customAccessDeniedHandler, UserAuthenticationEntryPoint userAuthenticationEntryPoint) {

        this.customAccessDeniedHandler = customAccessDeniedHandler;


        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   EventRepository eventRepository) throws Exception {
        http.httpBasic(r -> r.authenticationEntryPoint(userAuthenticationEntryPoint))

                .csrf(AbstractHttpConfigurer::disable) // For Postman
                .headers(headers -> headers.frameOptions().disable()) // For the H2 console
                .authorizeHttpRequests(auth -> auth  // manage access
                        .requestMatchers("/error", "/actuator/shutdown").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/changepass").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/user/role").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/user/access").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAuthority("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyAuthority("USER", "ACCOUNTANT")
                        .requestMatchers(HttpMethod.POST, "/api/empl/payment").hasAnyAuthority("ACCOUNTANT")
                        .requestMatchers("/api/security/**").authenticated()
                        /*.requestMatchers( "/api/acct/payments)").hasAuthority("ACCOUNTANT")*/
                        .requestMatchers("/api/acct/payments)").hasAuthority("ACCOUNTANT")
//                        .requestMatchers("/api/security/events").hasAuthority("AUDITOR")
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .anyRequest().authenticated()
                ).sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.accessDeniedHandler(customAccessDeniedHandler))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(userAuthenticationEntryPoint));


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console/**"), new AntPathRequestMatcher("/users/**"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


}
