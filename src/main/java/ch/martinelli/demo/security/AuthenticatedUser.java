package ch.martinelli.demo.security;

import ch.martinelli.demo.db.tables.records.ApplicationUserRecord;
import ch.martinelli.demo.data.service.UserService;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final UserService userService;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(UserService userService, AuthenticationContext authenticationContext) {
        this.userService = userService;
        this.authenticationContext = authenticationContext;
    }


    @Transactional
    public Optional<ApplicationUserRecord> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userService.findByUsername(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
