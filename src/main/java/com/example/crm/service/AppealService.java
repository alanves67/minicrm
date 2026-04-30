package com.example.crm.service;

import com.example.crm.dto.AppealDto;
import com.example.crm.exception.NotFoundException;
import com.example.crm.model.Appeal;
import com.example.crm.model.AppealStatus;
import com.example.crm.model.Customer;
import com.example.crm.repository.AppealRepository;
import com.example.crm.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppealService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "subject", "status", "createdAt", "updatedAt");
    private final AppealRepository appealRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<AppealDto> getAllAppeals() {
        return appealRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<AppealDto> getAppealsPage(int page, int size, String sortBy, String sortDir) {
        PaginationValidator.validate(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return appealRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public AppealDto getAppealById(Long id) {
        Appeal appeal = appealRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appeal not found with id: " + id));
        return toDto(appeal);
    }

    @Transactional(readOnly = true)
    public List<AppealDto> getAppealsByCustomerId(Long customerId) {
        // убедимся, что клиент существует
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Customer not found with id: " + customerId);
        }
        return appealRepository.findByCustomerId(customerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppealDto createAppeal(AppealDto appealDto) {
        Customer customer = customerRepository.findById(appealDto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + appealDto.getCustomerId()));

        Appeal appeal = new Appeal();
        appeal.setSubject(appealDto.getSubject());
        appeal.setDescription(appealDto.getDescription());
        appeal.setStatus(appealDto.getStatus() != null ? appealDto.getStatus() : AppealStatus.NEW);
        appeal.setCustomer(customer);

        Appeal saved = appealRepository.save(appeal);
        return toDto(saved);
    }

    @Transactional
    public AppealDto updateAppeal(Long id, AppealDto appealDto) {
        Appeal appeal = appealRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appeal not found with id: " + id));

        // Если customerId изменился, проверим нового клиента
        if (appealDto.getCustomerId() != null && !appeal.getCustomer().getId().equals(appealDto.getCustomerId())) {
            Customer customer = customerRepository.findById(appealDto.getCustomerId())
                    .orElseThrow(() -> new NotFoundException("Customer not found with id: " + appealDto.getCustomerId()));
            appeal.setCustomer(customer);
        }

        appeal.setSubject(appealDto.getSubject());
        appeal.setDescription(appealDto.getDescription());
        if (appealDto.getStatus() != null) {
            appeal.setStatus(appealDto.getStatus());
        }

        Appeal updated = appealRepository.save(appeal);
        return toDto(updated);
    }

    @Transactional
    public void deleteAppeal(Long id) {
        if (!appealRepository.existsById(id)) {
            throw new NotFoundException("Appeal not found with id: " + id);
        }
        appealRepository.deleteById(id);
    }

    private AppealDto toDto(Appeal appeal) {
        AppealDto dto = new AppealDto();
        dto.setId(appeal.getId());
        dto.setSubject(appeal.getSubject());
        dto.setDescription(appeal.getDescription());
        dto.setStatus(appeal.getStatus());
        dto.setCustomerId(appeal.getCustomer().getId());
        dto.setCreatedAt(appeal.getCreatedAt());
        dto.setUpdatedAt(appeal.getUpdatedAt());
        return dto;
    }
}