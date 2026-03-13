package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class BeneficioDTO {

    private Long id;

    @NotBlank(message = "Nome is required")
    @Size(max = 100, message = "Nome must not exceed 100 characters")
    private String nome;

    @Size(max = 255, message = "Descricao must not exceed 255 characters")
    private String descricao;

    @NotNull(message = "Valor is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor must be greater than zero")
    private BigDecimal valor;

    private Boolean ativo;

    private Long version;

    public BeneficioDTO() {
    }

    public BeneficioDTO(Long id, String nome, String descricao, BigDecimal valor, Boolean ativo, Long version) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = ativo;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
