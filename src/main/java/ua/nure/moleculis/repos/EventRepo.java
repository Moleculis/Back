package ua.nure.moleculis.repos;

import org.springframework.data.repository.PagingAndSortingRepository;
import ua.nure.moleculis.models.entitys.Event;

import java.util.List;

public interface EventRepo extends PagingAndSortingRepository<Event, Integer> {
    List<Event> findAll();
}
