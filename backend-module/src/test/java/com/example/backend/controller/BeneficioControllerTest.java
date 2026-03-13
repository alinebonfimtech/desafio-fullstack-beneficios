package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeneficioController.class)
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeneficioService service;

    private BeneficioDTO beneficioDTO;

    @BeforeEach
    void setUp() {
        beneficioDTO = new BeneficioDTO(1L, "Test", "Description", new BigDecimal("1000.00"), true, 0L);
    }

    @Test
    void findAll_ShouldReturnBeneficiosList() throws Exception {
        when(service.findAll()).thenReturn(Arrays.asList(beneficioDTO));

        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Test"))
                .andExpect(jsonPath("$[0].valor").value(1000.00));

        verify(service).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnBeneficio() throws Exception {
        when(service.findById(1L)).thenReturn(beneficioDTO);

        mockMvc.perform(get("/api/v1/beneficios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Test"));

        verify(service).findById(1L);
    }

    @Test
    void create_WithValidData_ShouldCreateBeneficio() throws Exception {
        when(service.create(any(BeneficioDTO.class))).thenReturn(beneficioDTO);

        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Test"));

        verify(service).create(any(BeneficioDTO.class));
    }

    @Test
    void update_WithValidData_ShouldUpdateBeneficio() throws Exception {
        when(service.update(eq(1L), any(BeneficioDTO.class))).thenReturn(beneficioDTO);

        mockMvc.perform(put("/api/v1/beneficios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Test"));

        verify(service).update(eq(1L), any(BeneficioDTO.class));
    }

    @Test
    void delete_ShouldDeleteBeneficio() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/beneficios/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void transfer_WithValidData_ShouldTransferSuccessfully() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("300.00"));
        doNothing().when(service).transfer(any(TransferRequestDTO.class));

        mockMvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).transfer(any(TransferRequestDTO.class));
    }
}
