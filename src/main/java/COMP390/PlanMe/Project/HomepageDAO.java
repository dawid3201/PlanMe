package COMP390.PlanMe.Project;

import COMP390.PlanMe.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HomepageDAO extends JpaRepository<Homepage, Long> {
    @Query("SELECT h FROM Homepage h WHERE h.user = :user")
    Homepage getHomepageByUser(User user);
}
