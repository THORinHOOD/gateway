package fi.lipp.greatheart.gateway.domain;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users", schema = "auth")
public class User {

    @Id
    private String login;
    private String password;
    @Type(type = "fi.lipp.greatheart.gateway.utils.StringArrayUserType")
    private String[] roles;

}
