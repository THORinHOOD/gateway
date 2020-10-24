package fi.lipp.greatheart.gateway.service;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private String login;
    private String password;
    private List<String> roles;
}
