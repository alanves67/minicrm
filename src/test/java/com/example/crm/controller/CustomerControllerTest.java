package com.example.crm.controller;

import com.example.crm.dto.CustomerDto;
import com.example.crm.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Test
    @WithMockUser(roles = {"USER"})
    void getAllCustomers_usesPaginationParams() throws Exception {
        CustomerDto dto = new CustomerDto();
        dto.setId(1L);
        dto.setFirstName("Anna");
        dto.setLastName("Ivanova");
        dto.setEmail("anna@example.com");

        when(customerService.getCustomersPage(eq(1), eq(5), eq("firstName"), eq("desc")))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/customers")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortBy", "firstName")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Anna"));

        verify(customerService).getCustomersPage(1, 5, "firstName", "desc");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createCustomer_whenBodyInvalid_returnsBadRequest() throws Exception {
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("");
        dto.setLastName("Ivanova");
        dto.setEmail("wrong-email");

        mockMvc.perform(post("/api/customers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.firstName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getAllCustomers_whenSortFieldInvalid_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("sortBy", "dropTable")
                        .param("sortDir", "asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").exists());

        verify(customerService, never()).getCustomersPage(0, 20, "dropTable", "asc");
    }
}
