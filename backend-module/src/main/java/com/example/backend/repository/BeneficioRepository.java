package com.example.backend.repository;

import com.example.backend.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    List<Beneficio> findByAtivoTrue();

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b FROM Beneficio b WHERE b.id = :id")
    Optional<Beneficio> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT b FROM Beneficio b WHERE LOWER(b.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Beneficio> searchByNome(@Param("nome") String nome);
}
