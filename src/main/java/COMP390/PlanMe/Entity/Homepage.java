package COMP390.PlanMe.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Homepages")
public class Homepage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "data")
    private String data;

    //getters and setters

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}