package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Homepage;
import COMP390.PlanMe.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class HomepageDAOLmpl implements HomepageDAO {
    private EntityManager entityManager;
    @Autowired
    public HomepageDAOLmpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public void save(Homepage homepage){
        entityManager.persist(homepage);
    }

    @Override
    public Homepage getHomepageByUser(User user) {
        return entityManager.createQuery("SELECT d FROM Homepage d WHERE d.user = :user", Homepage.class)
                .setParameter("user", user)
                .getSingleResult();
    }

}
