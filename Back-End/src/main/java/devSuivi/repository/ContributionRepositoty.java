package devSuivi.repository;

import devSuivi.entity.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributionRepositoty extends JpaRepository<Contribution, Long> {
}
