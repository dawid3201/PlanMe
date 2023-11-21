package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Homepage;
import COMP390.PlanMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HomepageDAO extends JpaRepository<Homepage, Long> {
    @Query("SELECT h FROM Homepage h WHERE h.user = :user")
    Homepage getHomepageByUser(User user);
}
