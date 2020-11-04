package fi.lipp.greatheart.gateway.resource;

import fi.lipp.greatheart.gateway.configuration.JwtProvider;
import fi.lipp.greatheart.gateway.domain.User;
import fi.lipp.greatheart.gateway.resource.models.SignInDto;
import fi.lipp.greatheart.gateway.service.CustomUserDetails;
import fi.lipp.greatheart.gateway.service.UserService;
import fi.lipp.greatheart.gateway.utils.Response;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

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

    @GetMapping("/validate_user")
    public ResponseEntity<Response<User>> validateUser(HttpServletRequest request) {
        return Response.EXECUTE(() -> {
            CustomUserDetails userDetails = (CustomUserDetails) request.getUserPrincipal();
            return userService.findById(userDetails.getId(), false);
        }).makeResponse();
    }

    @GetMapping("/validate_user_role")
    public ResponseEntity<Response<User>> validateToken(HttpServletRequest request) {
        return Response.EXECUTE(() -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean access = true;
            String trackerRole = request.getParameter("tracker");
            String catalogRole = request.getParameter("catalog");

            if (Strings.isNotBlank(trackerRole)) {
                access = Stream.of(trackerRole.split(","))
                    .allMatch(role -> checkRole(auth, CustomUserDetails.trackerRole(role)));
            }

            if (access && Strings.isNotBlank(catalogRole)) {
                access = Stream.of(catalogRole.split(","))
                    .allMatch(role -> checkRole(auth, CustomUserDetails.catalogRole(role)));
            }

            if (access) {
                return userService.findById(Long.valueOf(request.getUserPrincipal().getName()), false);
            } else {
                return Response.BAD("Не хватает прав доступа", "");
            }
        }).makeResponse();
    }

    private boolean checkRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }

}
