package fi.lipp.greatheart.gateway.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@Table(name = "user", schema = "catalog")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String password;
    @Type(type = "fi.lipp.greatheart.gateway.utils.IntArrayUserType")
    @Column(name = "catalog_role_ids")
    private Integer[] catalogRoleIds;
    @Type(type = "fi.lipp.greatheart.gateway.utils.IntArrayUserType")
    @Column(name = "tracker_role_ids")
    private Integer[] trackerRoleIds;
    @Column(name = "additional_info")
    @Type(type = "jsonb")
    private Map<String, Object> additionalInfo;

}
