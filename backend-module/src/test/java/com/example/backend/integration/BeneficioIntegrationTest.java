package com.example.backend.integration;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.repository.BeneficioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BeneficioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BeneficioRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void fullCrudCycle_ShouldWorkCorrectly() throws Exception {
        BeneficioDTO newBeneficio = new BeneficioDTO();
        newBeneficio.setNome("Integration Test");
        newBeneficio.setDescricao("Test Description");
        newBeneficio.setValor(new BigDecimal("1500.00"));
        newBeneficio.setAtivo(true);

        String createResponse = mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBeneficio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Integration Test"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BeneficioDTO created = objectMapper.readValue(createResponse, BeneficioDTO.class);
        Long id = created.getId();

        mockMvc.perform(get("/api/v1/beneficios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Integration Test"));

        created.setNome("Updated Name");
        mockMvc.perform(put("/api/v1/beneficios/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Updated Name"));

        mockMvc.perform(delete("/api/v1/beneficios/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/beneficios/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void transfer_ShouldWorkCorrectly() throws Exception {
        BeneficioDTO from = new BeneficioDTO();
        from.setNome("Source");
        from.setValor(new BigDecimal("1000.00"));
        from.setAtivo(true);

        BeneficioDTO to = new BeneficioDTO();
        to.setNome("Destination");
        to.setValor(new BigDecimal("500.00"));
        to.setAtivo(true);

        String fromResponse = mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(from)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String toResponse = mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(to)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BeneficioDTO fromCreated = objectMapper.readValue(fromResponse, BeneficioDTO.class);
        BeneficioDTO toCreated = objectMapper.readValue(toResponse, BeneficioDTO.class);

        TransferRequestDTO transferRequest = new TransferRequestDTO(
                fromCreated.getId(),
                toCreated.getId(),
                new BigDecimal("300.00")
        );

        mockMvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/beneficios/" + fromCreated.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(700.00));

        mockMvc.perform(get("/api/v1/beneficios/" + toCreated.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(800.00));
    }

    @Test
    void transfer_WithInsufficientBalance_ShouldFail() throws Exception {
        BeneficioDTO from = new BeneficioDTO();
        from.setNome("Source");
        from.setValor(new BigDecimal("100.00"));
        from.setAtivo(true);

        BeneficioDTO to = new BeneficioDTO();
        to.setNome("Destination");
        to.setValor(new BigDecimal("500.00"));
        to.setAtivo(true);

        String fromResponse = mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(from)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String toResponse = mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(to)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BeneficioDTO fromCreated = objectMapper.readValue(fromResponse, BeneficioDTO.class);
        BeneficioDTO toCreated = objectMapper.readValue(toResponse, BeneficioDTO.class);

        TransferRequestDTO transferRequest = new TransferRequestDTO(
                fromCreated.getId(),
                toCreated.getId(),
                new BigDecimal("300.00")
        );

        mockMvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
