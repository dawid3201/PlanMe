package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


public interface UserDAO extends JpaRepository<User, Long> {
    //check if email is already on database
    @Query("SELECT u FROM User u WHERE u.email = :email")
    boolean checkEmailExists(String email);


    // get user details
    User findByEmail(String email);
}
