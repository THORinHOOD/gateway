package fi.lipp.greatheart.gateway.service;

import fi.lipp.greatheart.gateway.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String password;

    private Collection<? extends GrantedAuthority> grantedAuthorities;

    public static CustomUserDetails fromUserDtoToCustomUserDetails(User user) {
        CustomUserDetails c = new CustomUserDetails();
        c.password = user.getPassword();
        c.id = user.getId();
        c.grantedAuthorities =Stream.concat(
            Arrays.stream(user.getCatalogRoleIds()).map(CustomUserDetails::catalog),
            Arrays.stream(user.getTrackerRoleIds()).map(CustomUserDetails::tracker)
        ).collect(Collectors.toList());
        return c;
    }

    private static SimpleGrantedAuthority catalog(Integer roleId) {
        return new SimpleGrantedAuthority("catalog_role_" + roleId);
    }

    private static SimpleGrantedAuthority tracker(Integer roleId) {
        return new SimpleGrantedAuthority("tracker_role_" + roleId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}