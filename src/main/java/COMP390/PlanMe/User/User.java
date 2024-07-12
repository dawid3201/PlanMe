package COMP390.PlanMe.User;
import COMP390.PlanMe.Annotation.StrongPassword;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Task.Task;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Table(name="users")
@Entity
public class User {
    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @StrongPassword //Private annotation
    @Column(name="password")
    private String password;

    @Id
    @Column(name="email_address")
    private String email;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "assignedUser", cascade = CascadeType.ALL)
    private List<Task> tasksAssigned = new ArrayList<>();

    public User() {}

    public User(String firstName, String lastName, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
    }
}
