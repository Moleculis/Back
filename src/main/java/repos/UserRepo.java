package repos;


import models.entitys.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;

public interface UserRepo extends PagingAndSortingRepository<User, Integer> {
    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    @Transactional
    void deleteByUsername(String username);
}
