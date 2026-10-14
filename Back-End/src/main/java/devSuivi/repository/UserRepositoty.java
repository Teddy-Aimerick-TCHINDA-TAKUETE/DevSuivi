package devSuivi.repository;

import devSuivi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoty extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String username);

    boolean existsByEmailIgnoreCase(String email);
}
