package com.example.crm.service;

import com.example.crm.dto.AdminUserForm;
import com.example.crm.exception.ConflictException;
import com.example.crm.exception.NotFoundException;
import com.example.crm.model.AppUser;
import com.example.crm.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AppUser getUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
    }

    @Transactional
    public void createUser(AdminUserForm form) {
        if (appUserRepository.existsByUsername(form.getUsername())) {
            throw new ConflictException("Логин уже занят");
        }
        if (form.getPassword() == null || form.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("Пароль должен быть минимум 6 символов");
        }

        AppUser user = new AppUser();
        user.setUsername(form.getUsername().trim());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setRole(form.getRole());
        user.setEnabled(form.isEnabled());
        appUserRepository.save(user);
    }

    @Transactional
    public void updateUser(Long id, AdminUserForm form, String currentUsername) {
        AppUser existingUser = getUserById(id);

        if (appUserRepository.existsByUsernameAndIdNot(form.getUsername(), id)) {
            throw new ConflictException("Логин уже занят");
        }

        if (existingUser.getUsername().equals(currentUsername) && !form.isEnabled()) {
            throw new IllegalArgumentException("Нельзя отключить текущего пользователя");
        }

        existingUser.setUsername(form.getUsername().trim());
        existingUser.setRole(form.getRole());
        existingUser.setEnabled(form.isEnabled());
        appUserRepository.save(existingUser);
    }

    @Transactional
    public void changePassword(Long id, String password) {
        if (password == null || password.trim().length() < 6) {
            throw new IllegalArgumentException("Пароль должен быть минимум 6 символов");
        }
        AppUser existingUser = getUserById(id);
        existingUser.setPasswordHash(passwordEncoder.encode(password));
        appUserRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id, String currentUsername) {
        AppUser existingUser = getUserById(id);
        if (existingUser.getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("Нельзя удалить текущего пользователя");
        }
        appUserRepository.delete(existingUser);
    }
}
