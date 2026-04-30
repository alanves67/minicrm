package com.example.crm.controller;

import com.example.crm.dto.AppealDto;
import com.example.crm.model.AppealStatus;
import com.example.crm.service.AppealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppealController.class)
class AppealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppealService appealService;

    @Test
    void getAllAppeals_withoutParams_usesDefaults() throws Exception {
        AppealDto dto = new AppealDto();
        dto.setId(10L);
        dto.setSubject("Help");
        dto.setDescription("Need support");
        dto.setStatus(AppealStatus.NEW);
        dto.setCustomerId(3L);

        when(appealService.getAppealsPage(eq(0), eq(20), eq("id"), eq("asc")))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/appeals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].status").value("NEW"));

        verify(appealService).getAppealsPage(0, 20, "id", "asc");
    }

    @Test
    void getAllAppeals_whenSizeTooLarge_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/appeals").param("size", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        verify(appealService, never()).getAppealsPage(0, 500, "id", "asc");
    }
}
