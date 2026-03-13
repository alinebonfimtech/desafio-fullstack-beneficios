package com.example.backend.mapper;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.entity.Beneficio;
import org.springframework.stereotype.Component;

@Component
public class BeneficioMapper {

    public BeneficioDTO toDTO(Beneficio entity) {
        if (entity == null) {
            return null;
        }
        return new BeneficioDTO(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getValor(),
            entity.getAtivo(),
            entity.getVersion()
        );
    }

    public Beneficio toEntity(BeneficioDTO dto) {
        if (dto == null) {
            return null;
        }
        Beneficio entity = new Beneficio();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setValor(dto.getValor());
        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        entity.setVersion(dto.getVersion());
        return entity;
    }

    public void updateEntityFromDTO(BeneficioDTO dto, Beneficio entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setValor(dto.getValor());
        if (dto.getAtivo() != null) {
            entity.setAtivo(dto.getAtivo());
        }
    }
}
