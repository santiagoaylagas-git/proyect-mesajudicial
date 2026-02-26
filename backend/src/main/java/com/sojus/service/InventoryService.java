package com.sojus.service;

import com.sojus.domain.entity.Hardware;
import com.sojus.domain.entity.Software;
import com.sojus.exception.BusinessRuleException;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.HardwareRepository;
import com.sojus.repository.SoftwareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final HardwareRepository hardwareRepository;
    private final SoftwareRepository softwareRepository;

    // ---- Hardware ----

    @Transactional(readOnly = true)
    public List<Hardware> findAllHardware() {
        return hardwareRepository.findAllByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Hardware findHardwareById(Long id) {
        return hardwareRepository.findById(id)
                .filter(h -> !h.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Hardware", id));
    }

    @Transactional
    public Hardware createHardware(Hardware hardware) {
        if (hardwareRepository.existsByInventarioPatrimonial(hardware.getInventarioPatrimonial())) {
            throw new BusinessRuleException("Ya existe un equipo con ese N° Inventario Patrimonial");
        }
        return hardwareRepository.save(hardware);
    }

    @Transactional
    public Hardware updateHardware(Long id, Hardware updated) {
        Hardware existing = findHardwareById(id);
        existing.setClase(updated.getClase());
        existing.setTipo(updated.getTipo());
        existing.setMarca(updated.getMarca());
        existing.setModelo(updated.getModelo());
        existing.setNumeroSerie(updated.getNumeroSerie());
        existing.setEstado(updated.getEstado());
        existing.setJuzgado(updated.getJuzgado());
        existing.setUbicacionFisica(updated.getUbicacionFisica());
        existing.setObservaciones(updated.getObservaciones());
        return hardwareRepository.save(existing);
    }

    @Transactional
    public void softDeleteHardware(Long id) {
        Hardware hw = findHardwareById(id);
        hw.setDeleted(true);
        hardwareRepository.save(hw);
    }

    // ---- Software ----

    @Transactional(readOnly = true)
    public List<Software> findAllSoftware() {
        return softwareRepository.findAllByDeletedFalse();
    }

    @Transactional(readOnly = true)
    public Software findSoftwareById(Long id) {
        return softwareRepository.findById(id)
                .filter(s -> !s.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Software", id));
    }

    @Transactional
    public Software createSoftware(Software software) {
        return softwareRepository.save(software);
    }

    @Transactional
    public Software updateSoftware(Long id, Software updated) {
        Software existing = findSoftwareById(id);
        existing.setNombre(updated.getNombre());
        existing.setVersion(updated.getVersion());
        existing.setFabricante(updated.getFabricante());
        existing.setTipoLicencia(updated.getTipoLicencia());
        existing.setNumeroLicencia(updated.getNumeroLicencia());
        existing.setCantidadLicencias(updated.getCantidadLicencias());
        existing.setFechaVencimiento(updated.getFechaVencimiento());
        existing.setEstado(updated.getEstado());
        existing.setObservaciones(updated.getObservaciones());
        return softwareRepository.save(existing);
    }

    @Transactional
    public void softDeleteSoftware(Long id) {
        Software sw = findSoftwareById(id);
        sw.setDeleted(true);
        softwareRepository.save(sw);
    }
}
