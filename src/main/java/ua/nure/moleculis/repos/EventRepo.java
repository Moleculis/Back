package ua.nure.moleculis.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ua.nure.moleculis.models.entitys.Event;
import ua.nure.moleculis.models.entitys.User;

import javax.transaction.Transactional;
import java.util.List;

public interface EventRepo extends PagingAndSortingRepository<Event, Integer> {
    List<Event> findAll();

    Page<Event> findAllByUsersContains(User user, Pageable pageable);

    Page<Event> findDistinctByUsersNotContains(User user, Pageable pageable);

    Event findById(Long id);

    @Transactional
    void deleteById(Long id);
}
