package fi.lipp.greatheart.gateway.resource;

import fi.lipp.greatheart.gateway.configuration.JwtProvider;
import fi.lipp.greatheart.gateway.service.UserDto;
import fi.lipp.greatheart.gateway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TestController {

    @Autowired private UserService userService;
    @Autowired private JwtProvider jwtProvider;
    @Autowired private PasswordEncoder encoder;

    @PostMapping("/sign_up")
    public ResponseEntity<String> registerUser(@RequestBody UserDto user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sign_in")
    public ResponseEntity<String> auth(@RequestBody UserDto user) {
        Optional<UserDto> userDto = userService.signIn(user.getLogin(), user.getPassword());
        if (userDto.isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        String token = jwtProvider.generateToken(userDto.get().getLogin());
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/validate_token")
    public ResponseEntity<UserDto> validateToken(@RequestBody String token) {
        String login = jwtProvider.getLoginFromToken(token);
        Optional<UserDto> user = userService.findByLogin(login);
        if (user.isPresent()) {
            user.get().setPassword("");
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
