package account.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.util.*;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)

public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotEmpty
    private String name;

    @NotBlank
    @NotEmpty
    private String lastname;


    @NotBlank
    @NotEmpty
    @Email(regexp = ".*@acme.com")
    private String email;


    @NotBlank
    @NotEmpty

    @Length(min = 12, message = "Password length must be 12 chars minimum!")
    private String password;


    public AppUser(String name, String lastName, String email, String password) {
        this.name = name;
        this.lastname = lastName;
        this.email = email;
        this.password = password;
    }


    public AppUser() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,

    })
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"
            ))
    private Set<Group> userGroups = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Set<Group> getUserGroupsSet() {
        return userGroups;
    }


    @JsonProperty("roles")
    public List<String> getUserGroups() {

        List<String> grouping = new ArrayList<>();

        for (var usergroup : userGroups) {

            grouping.add(usergroup.getName());
        }


        Collections.sort(grouping);
        return grouping;

    }


    public void setUserGroups(Set<Group> userGroups) {
        this.userGroups = userGroups;
    }


    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userGroups=" + userGroups +
                '}';
    }

    public void addUserGroups(Group group) {

        userGroups.add(group);
    }
}
