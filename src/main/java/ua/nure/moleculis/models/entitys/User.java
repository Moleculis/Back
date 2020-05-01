package ua.nure.moleculis.models.entitys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ua.nure.moleculis.models.enums.Gender;
import ua.nure.moleculis.models.enums.PostgreSQLEnumType;
import ua.nure.moleculis.models.enums.Role;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_entity")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "displayname")
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Type(type = "pgsql_enum")
    private Gender gender;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TokenBlacklist> blacklistTokens = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private Set<Event> events = new HashSet<>();

    @ManyToMany
    private Set<User> contacts;

    @Size(min = 4, max = 30)
    @Column(unique = true, nullable = false)
    private String username;

    @Size(min = 8)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    @ManyToMany(mappedBy = "users")
    private Set<Group> groups;

    @ManyToMany(mappedBy = "admins")
    private Set<Group> admin_groups;

    public void addTokenToBlacklist(String token, LocalDateTime localDateTime) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(token);
        tokenBlacklist.setDate(localDateTime);
        tokenBlacklist.setUser(this);
        blacklistTokens.add(tokenBlacklist);
    }
}