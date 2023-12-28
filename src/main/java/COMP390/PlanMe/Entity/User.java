package COMP390.PlanMe.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @JsonIgnore
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
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

    public String getLetters(){
        String fullName = firstName + " " + lastName;
        String[] nameParts = fullName.split("\\s");
        StringBuilder initials  = new StringBuilder();
        for(String namePart : nameParts){
            initials.append(namePart.charAt(0));
        }
        return initials.toString().toUpperCase();
    }
}
