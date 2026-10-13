package devSuivi.repository;

import devSuivi.entity.Assign;
import devSuivi.entity.AssignId;
import devSuivi.entity.Project;
import devSuivi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignRepositoty extends JpaRepository<Assign, AssignId> {

    List<Assign> findAllByUser(User user);

    List<Assign> findAllByProject(Project project);
}
