package com.example.backend.service;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @Mock
    private BeneficioMapper mapper;

    @InjectMocks
    private BeneficioService service;

    private Beneficio beneficio;
    private BeneficioDTO beneficioDTO;

    @BeforeEach
    void setUp() {
        beneficio = new Beneficio("Test", "Description", new BigDecimal("1000.00"), true);
        beneficio.setId(1L);
        beneficio.setVersion(0L);

        beneficioDTO = new BeneficioDTO(1L, "Test", "Description", new BigDecimal("1000.00"), true, 0L);
    }

    @Test
    void findAll_ShouldReturnAllBeneficios() {
        when(repository.findAll()).thenReturn(Arrays.asList(beneficio));
        when(mapper.toDTO(any(Beneficio.class))).thenReturn(beneficioDTO);

        List<BeneficioDTO> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnBeneficio() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficio));
        when(mapper.toDTO(beneficio)).thenReturn(beneficioDTO);

        BeneficioDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Test", result.getNome());
        verify(repository).findById(1L);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_WithValidData_ShouldCreateBeneficio() {
        when(mapper.toEntity(beneficioDTO)).thenReturn(beneficio);
        when(repository.save(any(Beneficio.class))).thenReturn(beneficio);
        when(mapper.toDTO(beneficio)).thenReturn(beneficioDTO);

        BeneficioDTO result = service.create(beneficioDTO);

        assertNotNull(result);
        verify(repository).save(any(Beneficio.class));
    }

    @Test
    void create_WithInvalidData_ShouldThrowException() {
        BeneficioDTO invalidDTO = new BeneficioDTO();
        invalidDTO.setNome("");
        invalidDTO.setValor(new BigDecimal("-100"));

        assertThrows(IllegalArgumentException.class, () -> service.create(invalidDTO));
    }

    @Test
    void update_WhenExists_ShouldUpdateBeneficio() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficio));
        when(repository.save(any(Beneficio.class))).thenReturn(beneficio);
        when(mapper.toDTO(beneficio)).thenReturn(beneficioDTO);

        BeneficioDTO result = service.update(1L, beneficioDTO);

        assertNotNull(result);
        verify(repository).save(any(Beneficio.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteBeneficio() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void transfer_WithValidData_ShouldTransferSuccessfully() {
        Beneficio from = new Beneficio("From", "Desc", new BigDecimal("1000.00"), true);
        from.setId(1L);
        Beneficio to = new Beneficio("To", "Desc", new BigDecimal("500.00"), true);
        to.setId(2L);

        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("300.00"));

        when(repository.findByIdWithLock(1L)).thenReturn(Optional.of(from));
        when(repository.findByIdWithLock(2L)).thenReturn(Optional.of(to));
        when(repository.save(any(Beneficio.class))).thenReturn(from);

        service.transfer(request);

        verify(repository, times(2)).save(any(Beneficio.class));
        assertEquals(new BigDecimal("700.00"), from.getValor());
        assertEquals(new BigDecimal("800.00"), to.getValor());
    }

    @Test
    void transfer_WithInsufficientBalance_ShouldThrowException() {
        Beneficio from = new Beneficio("From", "Desc", new BigDecimal("100.00"), true);
        from.setId(1L);
        Beneficio to = new Beneficio("To", "Desc", new BigDecimal("500.00"), true);
        to.setId(2L);

        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("300.00"));

        when(repository.findByIdWithLock(1L)).thenReturn(Optional.of(from));
        when(repository.findByIdWithLock(2L)).thenReturn(Optional.of(to));

        assertThrows(IllegalStateException.class, () -> service.transfer(request));
    }

    @Test
    void transfer_WithInactiveSource_ShouldThrowException() {
        Beneficio from = new Beneficio("From", "Desc", new BigDecimal("1000.00"), false);
        from.setId(1L);
        Beneficio to = new Beneficio("To", "Desc", new BigDecimal("500.00"), true);
        to.setId(2L);

        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("300.00"));

        when(repository.findByIdWithLock(1L)).thenReturn(Optional.of(from));
        when(repository.findByIdWithLock(2L)).thenReturn(Optional.of(to));

        assertThrows(IllegalStateException.class, () -> service.transfer(request));
    }

    @Test
    void transfer_WithSameIds_ShouldThrowException() {
        TransferRequestDTO request = new TransferRequestDTO(1L, 1L, new BigDecimal("300.00"));

        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }

    @Test
    void transfer_WithNegativeAmount_ShouldThrowException() {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("-100.00"));

        assertThrows(IllegalArgumentException.class, () -> service.transfer(request));
    }
}
