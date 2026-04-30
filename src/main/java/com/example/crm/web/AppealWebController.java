package com.example.crm.web;

import com.example.crm.dto.AppealDto;
import com.example.crm.model.AppealStatus;
import com.example.crm.service.AppealService;
import com.example.crm.service.CustomerService;
import com.example.crm.service.PaginationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/appeals")
@RequiredArgsConstructor
public class AppealWebController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "subject", "status", "createdAt", "updatedAt");
    private final AppealService appealService;
    private final CustomerService customerService;

    @GetMapping
    public String listAppeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model
    ) {
        PaginationValidator.validate(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Page<AppealDto> appealPage = appealService.getAppealsPage(page, size, sortBy, sortDir);
        model.addAttribute("appealPage", appealPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");
        return "appeals/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("appeal", new AppealDto());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("statuses", AppealStatus.values());
        return "appeals/form";
    }

    @PostMapping
    public String createAppeal(@Valid @ModelAttribute("appeal") AppealDto appealDto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("statuses", AppealStatus.values());
            return "appeals/form";
        }
        appealService.createAppeal(appealDto);
        return "redirect:/appeals";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        AppealDto appeal = appealService.getAppealById(id);
        model.addAttribute("appeal", appeal);
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("statuses", AppealStatus.values());
        return "appeals/form";
    }

    @PostMapping("/update/{id}")
    public String updateAppeal(@PathVariable Long id,
                               @Valid @ModelAttribute("appeal") AppealDto appealDto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("statuses", AppealStatus.values());
            return "appeals/form";
        }
        appealService.updateAppeal(id, appealDto);
        return "redirect:/appeals";
    }

    @PostMapping("/delete/{id}")
    public String deleteAppeal(@PathVariable Long id) {
        appealService.deleteAppeal(id);
        return "redirect:/appeals";
    }
}