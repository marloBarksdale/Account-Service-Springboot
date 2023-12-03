package account.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserAdapter implements UserDetails {


    private final AppUser user;

    public UserAdapter(AppUser user) {

        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        var userGroups = user.getUserGroupsSet();
        Collection<GrantedAuthority> authorities = new ArrayList<>(userGroups.size());

        for (Group userGroup : userGroups) {


            authorities.add(new SimpleGrantedAuthority(userGroup.getCode().toUpperCase()));

        }


        return authorities;
    }


    public List<String> getAuthoritiesList() {


        var a = getAuthorities();


        return a.stream().map(x -> x.getAuthority().toUpperCase()).toList();
    }




    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public AppUser getUser() {
        return this.user;
    }

    public void setPassword(String password) {

        user.setPassword(password);
    }

    public String getLastname() {

        return user.getLastname();
    }

    @Override
    public String getUsername() {
        return user.getName();
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

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "UserAdapter{" +
                "user=" + user +
                '}';
    }
}
