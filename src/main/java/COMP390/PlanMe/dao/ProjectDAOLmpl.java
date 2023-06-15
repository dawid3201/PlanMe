package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ProjectDAOLmpl implements ProjectDAO{
    private EntityManager entityManager;

    public ProjectDAOLmpl(EntityManager theEntityManager){
        entityManager = theEntityManager;
    }
    @Transactional
    @Override
    public void save(Project project) {
        entityManager.persist(project);
    }

    @Override
    public Project getProjectById(Long id) {
        return entityManager.find(Project.class, id);
    }

    @Override
    public List<Project> getProjectsByUser(User user) {
        return entityManager.createQuery("SELECT p FROM Project p WHERE :user MEMBER OF p.members", Project.class)
                .setParameter("user", user)
                .getResultList();
    }
}
