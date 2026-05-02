package com.example.crm.config;

import com.example.crm.model.AppUser;
import com.example.crm.model.UserRole;
import com.example.crm.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUsersInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.user.username:user}")
    private String userUsername;

    @Value("${app.security.user.password:user123}")
    private String userPassword;

    @Value("${app.security.admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        createIfMissing(userUsername, userPassword, UserRole.USER);
        createIfMissing(adminUsername, adminPassword, UserRole.ADMIN);
    }

    private void createIfMissing(String username, String rawPassword, UserRole role) {
        if (appUserRepository.findByUsername(username).isPresent()) {
            return;
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        appUserRepository.save(user);
    }
}
