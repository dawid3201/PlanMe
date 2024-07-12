package COMP390.PlanMe.Project;

import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.User.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "deadline")
    private String deadline;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonManagedReference
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "project_members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> members = new ArrayList<>();

    //List of tasks related to project
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "project")
    @OrderBy("position ASC")
    @JsonManagedReference
    private List<Task> tasks;

    //list of bars related to project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @OrderBy("position")
    @JsonBackReference
    private List<Bar> bars = new ArrayList<>();
}
