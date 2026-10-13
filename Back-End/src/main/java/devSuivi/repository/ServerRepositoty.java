package devSuivi.repository;

import devSuivi.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepositoty extends JpaRepository<Server, Long> {
}
