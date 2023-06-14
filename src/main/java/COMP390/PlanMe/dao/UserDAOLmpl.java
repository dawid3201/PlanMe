package COMP390.PlanMe.dao;

//import COMP390.PlanMe.dashboard;
import COMP390.PlanMe.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDAOLmpl implements UserDAO {
    private EntityManager entityManager;

    @Autowired
    public UserDAOLmpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public void save(User user) {
        entityManager.persist(user);
    }
    @Override
    public boolean checkEmailExists(String email) {
        Query query = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email");
        query.setParameter("email", email);
        Long count = (Long) query.getSingleResult();
        return count > 0;
    }
    @Override
    public User getUserByEmail(String email){
        Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email");
        query.setParameter("email", email);
        return (User) query.getSingleResult();
    }
}

