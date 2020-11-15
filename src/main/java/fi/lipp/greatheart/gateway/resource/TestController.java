package fi.lipp.greatheart.gateway.resource;

import fi.lipp.greatheart.gateway.configuration.JwtProvider;
import fi.lipp.greatheart.gateway.service.UserDto;
import fi.lipp.greatheart.gateway.service.UserService;
import fi.lipp.greatheart.gateway.utils.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<Response<UserDto>> registerUser(@RequestBody UserDto user) {
        return Response.EXECUTE(() -> {
            user.setPassword(encoder.encode(user.getPassword()));
            Response<UserDto> saved = userService.saveUser(user);
            if (!saved.isSuccess()) {
                return saved;
            }
            UserDto result = saved.getBody();
            result.setPassword("");
            return Response.OK(result);
        }).makeResponse();

    }

    @PostMapping("/sign_in")
    public ResponseEntity<Response<String>> auth(@RequestBody UserDto user) {
        return Response.EXECUTE(() -> {
            Response<UserDto> userDto = userService.signIn(user.getLogin(), user.getPassword());
            if (!userDto.isSuccess()) {
                return Response.BAD(userDto.getMessage());
            }
            String token = jwtProvider.generateToken(userDto.getBody().getLogin());
            return Response.OK(token);
        }).makeResponse();
    }

    @PostMapping("/validate_token")
    public ResponseEntity<Response<UserDto>> validateToken(@RequestBody String token) {
        return Response.EXECUTE(() -> {
            String login = jwtProvider.getLoginFromToken(token);
            Response<UserDto> user = userService.findByLogin(login);
            if (user.isSuccess()) {
                UserDto userDto = user.getBody();
                userDto.setPassword("");
                return Response.OK(userDto);
            }
            return user;
        }).makeResponse();
    }

}
