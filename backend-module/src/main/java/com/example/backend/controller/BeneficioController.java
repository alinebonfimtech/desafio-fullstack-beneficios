package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
@CrossOrigin(origins = "*")
@Tag(name = "Benefícios", description = "API para gerenciamento de benefícios")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os benefícios", description = "Retorna uma lista de todos os benefícios cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<BeneficioDTO>> findAll(
            @Parameter(description = "Filtrar apenas benefícios ativos")
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        List<BeneficioDTO> beneficios = activeOnly ? service.findAllActive() : service.findAll();
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID", description = "Retorna um benefício específico pelo seu ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Benefício encontrado"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<BeneficioDTO> findById(
            @Parameter(description = "ID do benefício", required = true)
            @PathVariable Long id) {
        BeneficioDTO beneficio = service.findById(id);
        return ResponseEntity.ok(beneficio);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar benefícios por nome", description = "Retorna benefícios que contenham o nome especificado")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    public ResponseEntity<List<BeneficioDTO>> searchByNome(
            @Parameter(description = "Nome ou parte do nome para buscar", required = true)
            @RequestParam String nome) {
        List<BeneficioDTO> beneficios = service.searchByNome(nome);
        return ResponseEntity.ok(beneficios);
    }

    @PostMapping
    @Operation(summary = "Criar novo benefício", description = "Cria um novo benefício no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Benefício criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<BeneficioDTO> create(
            @Parameter(description = "Dados do benefício a ser criado", required = true)
            @Valid @RequestBody BeneficioDTO dto) {
        BeneficioDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício", description = "Atualiza os dados de um benefício existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Benefício atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<BeneficioDTO> update(
            @Parameter(description = "ID do benefício", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do benefício", required = true)
            @Valid @RequestBody BeneficioDTO dto) {
        BeneficioDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar benefício", description = "Remove um benefício do sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Benefício deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do benefício", required = true)
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desativar benefício", description = "Marca um benefício como inativo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Benefício desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado")
    })
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID do benefício", required = true)
            @PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transferir valor entre benefícios", 
               description = "Transfere um valor de um benefício para outro com validações e controle de concorrência")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflito de concorrência")
    })
    public ResponseEntity<Void> transfer(
            @Parameter(description = "Dados da transferência", required = true)
            @Valid @RequestBody TransferRequestDTO request) {
        service.transfer(request);
        return ResponseEntity.ok().build();
    }
}
