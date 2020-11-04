package fi.lipp.greatheart.gateway.service;

import fi.lipp.greatheart.gateway.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String password;
    private Integer[] catalogRoles;
    private Integer[] trackerRoles;
    private Collection<? extends GrantedAuthority> grantedAuthorities;

    public static CustomUserDetails fromUserDtoToCustomUserDetails(User user) {
        CustomUserDetails c = new CustomUserDetails();
        c.password = user.getPassword();
        c.id = user.getId();
        c.grantedAuthorities = Stream.concat(
            Arrays.stream(user.getCatalogRoleIds() != null ? user.getCatalogRoleIds() : new Integer[]{})
                    .map(CustomUserDetails::catalog),
            Arrays.stream(user.getTrackerRoleIds() != null ? user.getTrackerRoleIds() : new Integer[]{})
                    .map(CustomUserDetails::tracker)
        ).collect(Collectors.toList());
        c.catalogRoles = user.getCatalogRoleIds();
        c.trackerRoles = user.getTrackerRoleIds();
        return c;
    }

    public Long getId() {
        return id;
    }

    public static String trackerRole(String roleId) {
        return "tracker_role_" + roleId;
    }

    public static String catalogRole(String roleId) {
        return "catalog_role_" + roleId;
    }

    private static SimpleGrantedAuthority catalog(Integer roleId) {
        return new SimpleGrantedAuthority(catalogRole(String.valueOf(roleId)));
    }

    private static SimpleGrantedAuthority tracker(Integer roleId) {
        return new SimpleGrantedAuthority(trackerRole(String.valueOf(roleId)));
    }

    public boolean hasCatalogRole(Integer roleId) {
        return Arrays.asList(catalogRoles).contains(roleId);
    }

    public boolean hasTrackerRole(Integer roleId) {
        return Arrays.asList(trackerRoles).contains(roleId);
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