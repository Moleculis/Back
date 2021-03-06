package ua.nure.moleculis.models.entitys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "group_entity")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<User> users;

    @ManyToMany
    @JoinTable(
            name = "group_admin",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<User> admins;

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public void removeAdmin(User admin) {
        admins.remove(admin);
    }

    public void addAdmin(User admin) {
        admins.add(admin);
    }
}