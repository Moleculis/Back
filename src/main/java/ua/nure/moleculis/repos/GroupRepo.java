package ua.nure.moleculis.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ua.nure.moleculis.models.entitys.Group;
import ua.nure.moleculis.models.entitys.User;

import javax.transaction.Transactional;
import java.util.List;

public interface GroupRepo extends PagingAndSortingRepository<Group, Integer> {
    List<Group> findAll();

    Page<Group> findAllByUsersContainsOrAdminsContains(User user, User admin, Pageable pageable);

    Group findGroupById(Long id);

    @Transactional
    void deleteById(Long groupId);
}
