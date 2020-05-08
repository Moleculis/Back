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

    @Column(nullable = false)
    private String displayname;

    @Column(nullable = false)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Type(type = "pgsql_enum")
    private Gender gender;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TokenBlacklist> blacklistTokens = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private Set<Event> events = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private Set<Contact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<Contact> contactRequests = new HashSet<>();

    @Size(min = 4, max = 30)
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 8)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    private Set<Group> groups = new HashSet<>();
    ;

    @ManyToMany(mappedBy = "admins")
    private Set<Group> admin_groups = new HashSet<>();
    ;

    private boolean enabled = false;

    public void addTokenToBlacklist(String token, LocalDateTime localDateTime) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(token);
        tokenBlacklist.setDate(localDateTime);
        tokenBlacklist.setUser(this);
        blacklistTokens.add(tokenBlacklist);
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void addContactRequest(Contact contact) {
        contactRequests.add(contact);
    }

    public void addEvent(Event event) {
        event.addUser(this);
        events.add(event);
    }

    public void removeEvent(Event event) {
        event.removeUser(this);
        events.remove(event);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void removeGroup(Group group) {
        group.removeUser(this);
        groups.remove(group);
    }

    public void addAdminGroup(Group group) {
        admin_groups.add(group);
    }

    public void removeAdminGroup(Group group) {
        group.removeAdmin(this);
        admin_groups.remove(group);
    }
}