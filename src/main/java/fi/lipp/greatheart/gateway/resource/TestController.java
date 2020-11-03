package fi.lipp.greatheart.gateway.resource;

import fi.lipp.greatheart.gateway.configuration.JwtProvider;
import fi.lipp.greatheart.gateway.domain.User;
import fi.lipp.greatheart.gateway.resource.models.SignInDto;
import fi.lipp.greatheart.gateway.service.UserService;
import fi.lipp.greatheart.gateway.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;

    public TestController(UserService userService,
                          JwtProvider jwtProvider,
                          PasswordEncoder encoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.encoder = encoder;
    }

    @PostMapping("/sign_up")
    public ResponseEntity<Response<User>> registerUser(@RequestBody User user) {
        return Response.EXECUTE_RAW(() -> {
            user.setPassword(encoder.encode(user.getPassword()));
            return userService.saveUser(user, false);
        }).makeResponse();
    }

    @PostMapping("/sign_in")
    public ResponseEntity<Response<String>> auth(@RequestBody SignInDto signInDto) {
        return Response.EXECUTE(() -> {
            Response<User> userFound = userService.signIn(signInDto.getLogin(), signInDto.getPassword());
            if (!userFound.isSuccess()) {
                return Response.BAD(userFound.getMessage());
            }
            String token = jwtProvider.generateToken(userFound.getBody().getLogin());
            return Response.OK(token);
        }).makeResponse();
    }

    @PostMapping("/validate_token")
    public ResponseEntity<Response<User>> validateToken(@RequestBody String token) {
        return Response.EXECUTE(() -> {
            String login = jwtProvider.getLoginFromToken(token);
            return userService.findByLogin(login, false);
        }).makeResponse();
    }
}
