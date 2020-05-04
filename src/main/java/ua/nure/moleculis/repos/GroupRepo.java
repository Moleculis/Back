package ua.nure.moleculis.repos;

import org.springframework.data.repository.PagingAndSortingRepository;
import ua.nure.moleculis.models.entitys.Group;

import java.util.List;

public interface GroupRepo extends PagingAndSortingRepository<Group, Integer> {
    List<Group> findAll();

    Group findGroupById(Long id);
}
