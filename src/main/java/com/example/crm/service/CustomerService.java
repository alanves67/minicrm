package com.example.crm.service;

import com.example.crm.dto.CustomerDto;
import com.example.crm.exception.ConflictException;
import com.example.crm.exception.NotFoundException;
import com.example.crm.model.Customer;
import com.example.crm.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
public class CustomerService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "firstName", "lastName", "email", "company");
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CustomerDto> getCustomersPage(int page, int size, String sortBy, String sortDir) {
        PaginationValidator.validate(page, size, sortBy, sortDir, ALLOWED_SORT_FIELDS);
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return customerRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        return toDto(customer);
    }

    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer customer = new Customer();
        updateEntityFromDto(customer, customerDto);
        try {
            Customer saved = customerRepository.save(customer);
            return toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Customer with this email already exists");
        }
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        updateEntityFromDto(customer, customerDto);
        try {
            Customer updated = customerRepository.save(customer);
            return toDto(updated);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Customer with this email already exists");
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerDto toDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setCompany(customer.getCompany());
        return dto;
    }

    private void updateEntityFromDto(Customer customer, CustomerDto dto) {
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setCompany(dto.getCompany());
    }
}
