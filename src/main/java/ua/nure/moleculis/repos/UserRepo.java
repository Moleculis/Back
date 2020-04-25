package ua.nure.moleculis.repos;


import org.springframework.data.repository.PagingAndSortingRepository;
import ua.nure.moleculis.models.entitys.User;

import javax.transaction.Transactional;

public interface UserRepo extends PagingAndSortingRepository<User, Integer> {
    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    @Transactional
    void deleteByUsername(String username);
}
