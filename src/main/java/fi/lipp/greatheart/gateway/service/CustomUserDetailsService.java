package fi.lipp.greatheart.gateway.service;

import fi.lipp.greatheart.gateway.domain.User;
import fi.lipp.greatheart.gateway.utils.Response;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Response<User> user = userService.findByLogin(username, true);
        if (!user.isSuccess()) {
            throw new UsernameNotFoundException(user.getMessage());
        }
        return CustomUserDetails.fromUserDtoToCustomUserDetails(user.getBody());
    }
}
