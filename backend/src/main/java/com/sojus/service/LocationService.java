package com.sojus.service;

import com.sojus.domain.entity.Circunscripcion;
import com.sojus.domain.entity.Juzgado;
import com.sojus.repository.CircunscripcionRepository;
import com.sojus.repository.JuzgadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final CircunscripcionRepository circunscripcionRepository;
    private final JuzgadoRepository juzgadoRepository;

    @Transactional(readOnly = true)
    public List<Circunscripcion> findAllCircunscripciones() {
        return circunscripcionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Juzgado> findAllJuzgados() {
        return juzgadoRepository.findAllByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Juzgado> findJuzgadosByEdificio(Long edificioId) {
        return juzgadoRepository.findAllByEdificioId(edificioId);
    }
}
