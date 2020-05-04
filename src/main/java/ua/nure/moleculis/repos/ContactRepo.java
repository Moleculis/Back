package ua.nure.moleculis.repos;


import org.springframework.data.repository.PagingAndSortingRepository;
import ua.nure.moleculis.models.entitys.Contact;

public interface ContactRepo extends PagingAndSortingRepository<Contact, Integer> {
    Contact findContactById(Long id);
}
