package fi.lipp.greatheart.gateway.service;

import fi.lipp.greatheart.gateway.repository.UserRepository;
import fi.lipp.greatheart.gateway.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private UserMapper mapper;
    @Autowired private UserRepository repository;
    @Autowired private PasswordEncoder encoder;

    @Override
    public UserDto saveUser(UserDto user) {
        return mapper.convert(repository.save(mapper.convert(user)));
    }

    @Override
    public Optional<UserDto> findByLogin(String login) {
        return repository.findByLogin(login).map(mapper::convert);
    }

    @Override
    public Optional<UserDto> signIn(String login, String password) {
        Optional<UserDto> user = this.findByLogin(login);
        return user.filter(e -> encoder.matches(password, e.getPassword()));
    }
}
