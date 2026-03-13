package com.example.backend.service;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.mapper.BeneficioMapper;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficioService {

    private final BeneficioRepository repository;
    private final BeneficioMapper mapper;

    public BeneficioService(BeneficioRepository repository, BeneficioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<BeneficioDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BeneficioDTO> findAllActive() {
        return repository.findByAtivoTrue().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BeneficioDTO findById(Long id) {
        Beneficio entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio not found with id: " + id));
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<BeneficioDTO> searchByNome(String nome) {
        return repository.searchByNome(nome).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BeneficioDTO create(BeneficioDTO dto) {
        validateBeneficioDTO(dto);
        Beneficio entity = mapper.toEntity(dto);
        entity.setId(null);
        entity.setVersion(null);
        Beneficio saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional
    public BeneficioDTO update(Long id, BeneficioDTO dto) {
        validateBeneficioDTO(dto);
        Beneficio entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio not found with id: " + id));
        
        mapper.updateEntityFromDTO(dto, entity);
        Beneficio updated = repository.save(entity);
        return mapper.toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Beneficio not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deactivate(Long id) {
        Beneficio entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio not found with id: " + id));
        entity.setAtivo(false);
        repository.save(entity);
    }

    @Transactional
    public void transfer(TransferRequestDTO request) {
        validateTransferRequest(request);

        Beneficio from = repository.findByIdWithLock(request.getFromId())
                .orElseThrow(() -> new EntityNotFoundException("Source beneficio not found: " + request.getFromId()));

        Beneficio to = repository.findByIdWithLock(request.getToId())
                .orElseThrow(() -> new EntityNotFoundException("Destination beneficio not found: " + request.getToId()));

        if (!from.getAtivo()) {
            throw new IllegalStateException("Source beneficio is not active");
        }

        if (!to.getAtivo()) {
            throw new IllegalStateException("Destination beneficio is not active");
        }

        if (from.getValor().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException(
                String.format("Insufficient balance. Available: %s, Required: %s", 
                    from.getValor(), request.getAmount())
            );
        }

        from.setValor(from.getValor().subtract(request.getAmount()));
        to.setValor(to.getValor().add(request.getAmount()));

        repository.save(from);
        repository.save(to);
    }

    private void validateBeneficioDTO(BeneficioDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome is required");
        }
        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor must be greater than or equal to zero");
        }
    }

    private void validateTransferRequest(TransferRequestDTO request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (request.getFromId() == null || request.getToId() == null) {
            throw new IllegalArgumentException("Beneficio IDs cannot be null");
        }

        if (request.getFromId().equals(request.getToId())) {
            throw new IllegalArgumentException("Cannot transfer to the same beneficio");
        }
    }
}
