package com.example.crm.service;

import com.example.crm.model.Customer;
import com.example.crm.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getCustomersPage_returnsMappedDtosAndPagingMetadata() {
        Customer customer = new Customer();
        customer.setId(7L);
        customer.setFirstName("Ivan");
        customer.setLastName("Petrov");
        customer.setEmail("ivan@example.com");

        Page<Customer> page = new PageImpl<>(List.of(customer), PageRequest.of(0, 5), 1);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<com.example.crm.dto.CustomerDto> result = customerService.getCustomersPage(0, 5, "id", "asc");

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(7L);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("ivan@example.com");
    }
}
