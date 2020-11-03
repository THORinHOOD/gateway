package fi.lipp.greatheart.gateway.resource.models;

import lombok.Data;

@Data
public class SignInDto {
    private String login;
    private String password;
}
