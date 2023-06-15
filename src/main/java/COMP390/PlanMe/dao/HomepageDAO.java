package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Homepage;
import COMP390.PlanMe.entity.User;

public interface HomepageDAO {
    void save(Homepage dashboard);
    Homepage getHomepageByUser(User user);
}
