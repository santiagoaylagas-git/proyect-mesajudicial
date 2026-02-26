package com.sojus.repository;

import com.sojus.domain.entity.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SoftwareRepository extends JpaRepository<Software, Long> {
    List<Software> findAllByDeletedFalse();

    long countByDeletedFalse();
}
