package ua.nure.moleculis.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ua.nure.moleculis.components.Translator;
import ua.nure.moleculis.exception.CustomException;
import ua.nure.moleculis.models.entitys.User;
import ua.nure.moleculis.models.enums.Role;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AdministrationService {
    final String backupFilesPath = "backups/";
    final String dbHost = "localhost";
    final String dbPort = "5432";
    final String dbUser = "eugene";
    final String dbName = "moleculisDB";

    private final UserService userService;

    public AdministrationService(UserService userService) {
        this.userService = userService;
    }

    public String createDatabaseBackup(HttpServletRequest request) {
        checkIfUserIsAdmin(request);
        final String date = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date());
        final String backupFileName = backupFilesPath + "backup_" + dbName.toLowerCase() + "_" + date + ".bak";
        try {
            final String[] createBackupCommand = new String[]{"pg_dump", dbName, "-h",
                    dbHost, "-p", dbPort, "-U", dbUser, "-w", "-c", "-f", backupFileName};
            final ProcessBuilder pb =
                    new ProcessBuilder(createBackupCommand);

            final Process process = pb.start();
            process.waitFor();
            return Translator.toLocale("backupCreated");
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(Translator.toLocale("backupCreateError"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String restoreDatabaseFromBackup(String backup, HttpServletRequest request) {
        checkIfUserIsAdmin(request);
        if(!getFileBackups().contains(backup)){
            throw new CustomException(Translator.toLocale("noBackup"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        final String backupName = backupFilesPath + backup;
        final String[] restoreCommand = new String[]{"psql", "-U", dbUser,
                "-f", backupName, "-h", dbHost, "-p", dbPort, dbName};
        try {
            final ProcessBuilder pb =
                    new ProcessBuilder(restoreCommand);

            final Process process = pb.start();
            process.waitFor();
            return Translator.toLocale("backupRestored");
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(Translator.toLocale("backupRestoreFail"), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public List<String> getBackupFileNames(HttpServletRequest request) {
        checkIfUserIsAdmin(request);
        return getFileBackups();
    }

    private List<String> getFileBackups(){
        final List<String> backupFileNames = new ArrayList<>();
        final File path = new File(backupFilesPath);

        final File[] files = path.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    backupFileNames.add(file.getName());
                }
            }
        }
        return backupFileNames;
    }

    private void checkIfUserIsAdmin(HttpServletRequest request) {
        final User currentUser = userService.currentUser(request);
        if (!currentUser.getRoles().contains(Role.ROLE_ADMIN)) {
            throw new CustomException(Translator.toLocale("notAdmin"), HttpStatus.UNAUTHORIZED);
        }
    }
}
