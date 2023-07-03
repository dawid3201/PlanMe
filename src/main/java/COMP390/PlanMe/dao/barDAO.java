package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Bar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface barDAO extends JpaRepository<Bar, Long> {

    Bar getBarByName(String newSwimlane);

    Bar getBarById(Long Id);
}
