package fi.lipp.greatheart.gateway.service;

import fi.lipp.greatheart.gateway.repository.UserRepository;
import fi.lipp.greatheart.gateway.service.mapper.UserMapper;
import fi.lipp.greatheart.gateway.utils.Response;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    public UserService(UserRepository repository,
                       PasswordEncoder encoder,
                       UserMapper userMapper) {
        this.repository = repository;
        this.encoder = encoder;
        this.mapper = userMapper;
    }

    public Response<UserDto> saveUser(UserDto user) {
        return Response.EXECUTE_RAW(() -> {
            return mapper.convert(repository.save(mapper.convert(user)));
        });
    }

    public Response<UserDto> findByLogin(String login) {
        return Response.EXECUTE(() -> {
            Optional<UserDto> userDtoOptional = repository.findByLogin(login).map(mapper::convert);
            if (userDtoOptional.isEmpty()) {
                return Response.BAD("Нет пользователя с логином = %s", login);
            }
            return Response.OK(userDtoOptional.get());
        });
    }

    public Response<UserDto> signIn(String login, String password) {
        return Response.EXECUTE(() -> {
            Response<UserDto> user = this.findByLogin(login);
            if (!user.isSuccess()) {
                return user;
            }
            if (encoder.matches(password, user.getBody().getPassword())) {
                return user;
            }
            return Response.BAD("Неверный пароль или логин");
        });
    }
}
