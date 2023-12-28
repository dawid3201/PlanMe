package COMP390.PlanMe.Dao;

import COMP390.PlanMe.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserDAO extends JpaRepository<User, Long> {
    //check if email is already on database
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean checkEmailExists(@Param("email") String email);
    // get user details
    User findByEmail(String email);
}
