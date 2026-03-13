package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("Beneficio IDs cannot be null");
        }

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same beneficio");
        }

        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC);
        Beneficio to = em.find(Beneficio.class, toId, LockModeType.OPTIMISTIC);

        if (from == null) {
            throw new IllegalArgumentException("Source beneficio not found: " + fromId);
        }

        if (to == null) {
            throw new IllegalArgumentException("Destination beneficio not found: " + toId);
        }

        if (!from.getAtivo()) {
            throw new IllegalStateException("Source beneficio is not active");
        }

        if (!to.getAtivo()) {
            throw new IllegalStateException("Destination beneficio is not active");
        }

        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance. Available: " + from.getValor() + ", Required: " + amount);
        }

        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        em.merge(from);
        em.merge(to);
        em.flush();
    }
}
