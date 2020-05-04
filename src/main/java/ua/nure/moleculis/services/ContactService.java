package ua.nure.moleculis.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.nure.moleculis.components.Translator;
import ua.nure.moleculis.exception.CustomException;
import ua.nure.moleculis.models.entitys.Contact;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.repos.ContactRepo;
import ua.nure.moleculis.repos.UserRepo;

import javax.servlet.http.HttpServletRequest;

@Service
public class ContactService {

    private final ContactRepo contactRepo;
    private final UserService userService;
    private final UserRepo userRepo;

    public ContactService(ContactRepo contactRepo, UserService userService, UserRepo userRepo) {
        this.contactRepo = contactRepo;
        this.userService = userService;
        this.userRepo = userRepo;
    }

    public String sendContactRequest(HttpServletRequest request, String username) {
        final User currentUser = userService.currentUser(request);
        final User contact = userService.getUser(username);
        if (contact == null) {
            throw new CustomException(Translator.toLocale("noUser"), HttpStatus.BAD_REQUEST);
        }
        final Contact newContact = new Contact();
        newContact.setAccepted(false);
        newContact.setSender(currentUser);
        newContact.setReceiver(contact);

        contactRepo.save(newContact);

        currentUser.addContact(newContact);
        userRepo.save(currentUser);

        contact.addContactRequest(newContact);
        userRepo.save(contact);

        return Translator.toLocale("contactReqSent");
    }

    public String deleteContact(Long id) {
        final Contact contact = contactRepo.findContactById(id);
        if (contact == null) {
            throw new CustomException(Translator.toLocale("noContact"), HttpStatus.BAD_REQUEST);
        }
        contactRepo.delete(contact);
        return Translator.toLocale("contactRequestCanceled");
    }

    public String acceptContactRequest(Long id, HttpServletRequest req) {
        final Contact contact = contactRepo.findContactById(id);
        if (contact == null) {
            throw new CustomException(Translator.toLocale("noContact"), HttpStatus.BAD_REQUEST);
        }
        final User currentUser = userService.currentUser(req);
        if (contact.getSender().getUsername().equals(currentUser.getUsername())) {
            throw new CustomException(Translator.toLocale("ownContactAccept"), HttpStatus.BAD_REQUEST);
        }

        if (!contact.getReceiver().getUsername().equals(currentUser.getUsername())) {
            throw new CustomException(Translator.toLocale("foreignContact"), HttpStatus.BAD_REQUEST);
        }

        if (contact.isAccepted()) {
            throw new CustomException(Translator.toLocale("contactAlreadyAccepted"), HttpStatus.BAD_REQUEST);
        }
        contact.setAccepted(true);

        contactRepo.save(contact);

        return Translator.toLocale("contactAccepted");
    }
}
