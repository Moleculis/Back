package ua.nure.moleculis.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.nure.moleculis.models.dto.MessageDTO;
import ua.nure.moleculis.services.AdministrationService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/administration")
public class AdministrationController {
    private final AdministrationService administrationService;

    public AdministrationController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @PostMapping("/backup")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> createDbBackup(HttpServletRequest request) {
        final String message = administrationService.createDatabaseBackup(request);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }
}
