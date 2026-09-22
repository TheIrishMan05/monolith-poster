package ifmo.poster.monolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ifmo.poster.monolith.entity.User;

public interface UserRepository  extends JpaRepository<User, Long> {
}
