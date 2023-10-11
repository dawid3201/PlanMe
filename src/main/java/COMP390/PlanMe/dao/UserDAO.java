package COMP390.PlanMe.dao;

//import COMP390.PlanMe.dashboard;
import COMP390.PlanMe.entity.User;

public interface UserDAO {
    //save user to database method
    void save(User user);

    //check if emial is already on database
    boolean checkEmailExists(String email);

    // get user details
    User getUserByEmail(String email);
}
