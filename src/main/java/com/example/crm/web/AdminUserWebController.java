package com.example.crm.web;

import com.example.crm.dto.AdminUserForm;
import com.example.crm.exception.ConflictException;
import com.example.crm.exception.NotFoundException;
import com.example.crm.model.AppUser;
import com.example.crm.model.UserRole;
import com.example.crm.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserWebController {

    private final AdminUserService adminUserService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", adminUserService.getAllUsers());
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        AdminUserForm form = new AdminUserForm();
        form.setRole(UserRole.USER);
        model.addAttribute("userForm", form);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("editMode", false);
        return "admin/users/form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("userForm") AdminUserForm form,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("editMode", false);
            return "admin/users/form";
        }

        try {
            adminUserService.createUser(form);
            return "redirect:/admin/users?created";
        } catch (ConflictException ex) {
            result.rejectValue("username", "duplicate", ex.getMessage());
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("editMode", false);
            return "admin/users/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            AppUser user = adminUserService.getUserById(id);
            AdminUserForm form = new AdminUserForm();
            form.setId(user.getId());
            form.setUsername(user.getUsername());
            form.setRole(user.getRole());
            form.setEnabled(user.isEnabled());
            form.setPassword("");

            model.addAttribute("userForm", form);
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("editMode", true);
            return "admin/users/form";
        } catch (NotFoundException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userForm") AdminUserForm form,
                             BindingResult result,
                             Model model,
                             Authentication authentication) {
        if (result.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("editMode", true);
            return "admin/users/form";
        }

        try {
            adminUserService.updateUser(id, form, authentication.getName());
            return "redirect:/admin/users?updated";
        } catch (ConflictException ex) {
            result.rejectValue("username", "duplicate", ex.getMessage());
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("editMode", true);
            return "admin/users/form";
        } catch (IllegalArgumentException ex) {
            result.reject("selfModifyDenied", ex.getMessage());
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("editMode", true);
            return "admin/users/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            adminUserService.deleteUser(id, authentication.getName());
            return "redirect:/admin/users?deleted";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addAttribute("error", ex.getMessage());
            return "redirect:/admin/users";
        }
    }
}
