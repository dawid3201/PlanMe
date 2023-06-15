package COMP390.PlanMe.dao;

//import COMP390.PlanMe.dashboard;
import COMP390.PlanMe.entity.User;

public interface UserDAO {
    //save user to database method
    void save(User user);
    boolean checkEmailExists(String email);
    User getUserByEmail(String email); // get user details
}
