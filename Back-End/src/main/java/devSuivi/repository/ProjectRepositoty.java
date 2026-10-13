package devSuivi.repository;

import devSuivi.entity.Project;
import devSuivi.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepositoty extends JpaRepository<Project, Long> {

    List<Project> findAllByServer(Server server);
}
