package ua.nure.moleculis.repos;

import org.springframework.data.repository.PagingAndSortingRepository;
import ua.nure.moleculis.models.entitys.Group;

public interface GroupRepo extends PagingAndSortingRepository<Group, Integer> {

}
