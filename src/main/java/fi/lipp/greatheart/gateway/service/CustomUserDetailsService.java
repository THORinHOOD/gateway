package fi.lipp.greatheart.gateway.service;

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
        Response<UserDto> user = userService.findByLogin(username);
        if (!user.isSuccess()) {
            return CustomUserDetails.empty();
        }
        return CustomUserDetails.fromUserDtoToCustomUserDetails(user.getBody());
    }
}
