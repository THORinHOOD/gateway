package fi.lipp.greatheart.gateway.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "USERS")
public class UserEntity {

    @Id
    private String login;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;
}
