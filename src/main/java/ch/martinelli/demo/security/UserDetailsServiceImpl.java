package ch.martinelli.demo.security;

import ch.martinelli.demo.db.tables.UserRoles;
import ch.martinelli.demo.db.tables.records.ApplicationUserRecord;
import ch.martinelli.demo.db.tables.records.UserRolesRecord;
import ch.martinelli.demo.data.service.UserService;
import org.jooq.Result;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUserRecord user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHashedPassword(),
                    getAuthorities(user));
        }
    }

    private List<SimpleGrantedAuthority> getAuthorities(ApplicationUserRecord user) {
        Result<UserRolesRecord> roles = userService.findRolesByUserId(user.getId());
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_"+ role.getRole())).toList();
    }

}
