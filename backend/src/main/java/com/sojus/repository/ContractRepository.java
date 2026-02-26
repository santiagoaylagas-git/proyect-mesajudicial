package com.sojus.repository;

import com.sojus.domain.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findAllByActiveTrue();

    @Query("SELECT c FROM Contract c WHERE c.fechaFin <= :date AND c.active = true")
    List<Contract> findExpiringBefore(LocalDate date);

    long countByActiveTrue();

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.fechaFin <= :date AND c.active = true")
    long countByFechaFinBeforeAndActiveTrue(LocalDate date);
}
