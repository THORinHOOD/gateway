package fi.lipp.greatheart.gateway.service;

import java.util.Optional;

public interface UserService {

    UserDto saveUser(UserDto user);
    Optional<UserDto> findByLogin(String login);
    Optional<UserDto> signIn(String login, String password);
}
