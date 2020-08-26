package ua.nure.moleculis.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.nure.moleculis.components.Translator;
import ua.nure.moleculis.exception.CustomException;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.models.enums.Role;
import ua.nure.moleculis.repos.UserRepo;
import ua.nure.moleculis.security.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AdministrationService {

    private final UserService userService;

    public AdministrationService(UserService userService) {
        this.userService = userService;
    }

    public String createDatabaseBackup(HttpServletRequest request) {
        final User currentUser = userService.currentUser(request);
        if (!currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
            throw new CustomException(Translator.toLocale("notAdmin"), HttpStatus.UNAUTHORIZED);
        }

        final String backupFilesPath = "backups/";

        final String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
        final String backupFileName = backupFilesPath + "backup_moleculisdb_" + date + ".bak";
        try {
            final String createBackupCommand = "pg_dump moleculisDB -h localhost " +
                    "-p 5432 -U eugene -w -c -f " + backupFileName;
            final ProcessBuilder pb =
                    new ProcessBuilder(createBackupCommand.split(" "));

            final Process process = pb.start();
            process.waitFor();
            return Translator.toLocale("backupCreated");
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(Translator.toLocale("backupCreateError"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
