package ua.nure.moleculis.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.nure.moleculis.models.dto.ListDTO;
import ua.nure.moleculis.models.dto.MessageDTO;
import ua.nure.moleculis.services.AdministrationService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/administration")
public class AdministrationController {
    private final String backupsEndpoint = "/backups";

    private final AdministrationService administrationService;

    public AdministrationController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @PostMapping(backupsEndpoint)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> createDbBackup(HttpServletRequest request) {
        final String message = administrationService.createDatabaseBackup(request);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }

    @GetMapping(backupsEndpoint)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ListDTO<String>> getBackups(HttpServletRequest request) {
        final List<String> backups = administrationService.getBackupFileNames(request);
        return new ResponseEntity<>(new ListDTO<>(backups), HttpStatus.OK);
    }

    @PutMapping(backupsEndpoint)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> restoreBackup(@RequestParam String backup, HttpServletRequest request) {
        final String message = administrationService.restoreDatabaseFromBackup(backup, request);
        return new ResponseEntity<>(new MessageDTO(message), HttpStatus.OK);
    }
}
