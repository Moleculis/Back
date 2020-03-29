package repos;


import models.entitys.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepo extends JpaRepository<TokenBlacklist, Integer> {
    boolean existsByToken(String token);
}
