package fi.lipp.greatheart.gateway.service;

import fi.lipp.greatheart.gateway.domain.User;
import fi.lipp.greatheart.gateway.repository.UserRepository;
import fi.lipp.greatheart.gateway.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository,
                       PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public User saveUser(User user, boolean leavePassword) {
        User savedUser = repository.save(user);
        if (!leavePassword) {
            savedUser.setPassword(null);
        }
        return savedUser;
    }

    public Response<User> findByLogin(String login, boolean withPassword) {
        return Response.EXECUTE(() -> {
            Optional<User> user = repository.findByLogin(login);
            if (user.isPresent()) {
                User result = user.get();
                if (!withPassword) {
                    result.setPassword(null);
                }
                return Response.OK(user.get());
            }
            return Response.BAD("Пользователь с логином %s не найден", login);
        });
    }

    public Response<User> signIn(String login, String password) {
        Response<User> user = this.findByLogin(login, true);
        if (user.isSuccess()) {
            if (!encoder.matches(password, user.getBody().getPassword())) {
                return Response.BAD("Неверный пароль или логин");
            }
        }
        return user;
    }
}
