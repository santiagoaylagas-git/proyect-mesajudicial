package com.sojus.repository;

import com.sojus.domain.entity.Juzgado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JuzgadoRepository extends JpaRepository<Juzgado, Long> {
    List<Juzgado> findAllByActiveTrue();
    List<Juzgado> findAllByEdificioId(Long edificioId);
}
