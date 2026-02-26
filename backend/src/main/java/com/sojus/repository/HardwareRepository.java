package com.sojus.repository;

import com.sojus.domain.entity.Hardware;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HardwareRepository extends JpaRepository<Hardware, Long> {
    List<Hardware> findAllByDeletedFalse();

    List<Hardware> findAllByJuzgadoIdAndDeletedFalse(Long juzgadoId);

    Optional<Hardware> findByInventarioPatrimonialAndDeletedFalse(String inventarioPatrimonial);

    boolean existsByInventarioPatrimonial(String inventarioPatrimonial);

    long countByDeletedFalse();
}
