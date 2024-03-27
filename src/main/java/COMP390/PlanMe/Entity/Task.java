package COMP390.PlanMe.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tasks")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})//save and modify handlers
    @JoinColumn(name = "bar_id")
    private Bar bar;

    @Column(name = "state")
    private String state;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "priority")
    private int priority;

    @Column(name = "position")
    private Long position;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "assigned_user_email")
    private User assignedUser;
}
