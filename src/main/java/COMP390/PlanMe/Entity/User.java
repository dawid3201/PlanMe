package COMP390.PlanMe.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    @Column(name="password")
    private String password;

    @Id
    @Column(name="email_address")
    private String email;

//    @JsonIgnore
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonManagedReference//I added this recently, delete if there will be errors
    private List<Project> projects;

    @OneToMany(mappedBy = "assignedUser", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Task> tasksAssigned;

    public User() {}

    public User(String firstName, String lastName, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
    }

    //Use BCrypt to encode user password
    public void setPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }


}
