package ua.nure.moleculis.repos;


import org.springframework.data.jpa.repository.JpaRepository;
import ua.nure.moleculis.models.entitys.TokenBlacklist;

public interface TokenRepo extends JpaRepository<TokenBlacklist, Integer> {
    boolean existsByToken(String token);
}
