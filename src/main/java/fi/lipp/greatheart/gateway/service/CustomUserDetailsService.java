package fi.lipp.greatheart.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private UserService userService;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user = userService.findByLogin(username).get();
        return CustomUserDetails.fromUserDtoToCustomUserDetails(user);
    }
}