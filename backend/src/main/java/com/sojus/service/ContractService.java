package com.sojus.service;

import com.sojus.domain.entity.Contract;
import com.sojus.exception.ResourceNotFoundException;
import com.sojus.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;

    @Transactional(readOnly = true)
    public List<Contract> findAll() {
        return contractRepository.findAllByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Contract findById(Long id) {
        return contractRepository.findById(id)
                .filter(Contract::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", id));
    }

    @Transactional
    public Contract create(Contract contract) {
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract update(Long id, Contract updated) {
        Contract existing = findById(id);
        existing.setNombre(updated.getNombre());
        existing.setProveedor(updated.getProveedor());
        existing.setNumeroContrato(updated.getNumeroContrato());
        existing.setFechaInicio(updated.getFechaInicio());
        existing.setFechaFin(updated.getFechaFin());
        existing.setCoberturaHw(updated.getCoberturaHw());
        existing.setCoberturaSw(updated.getCoberturaSw());
        existing.setSlaDescripcion(updated.getSlaDescripcion());
        existing.setObservaciones(updated.getObservaciones());
        return contractRepository.save(existing);
    }

    @Transactional
    public void deactivate(Long id) {
        Contract contract = findById(id);
        contract.setActive(false);
        contractRepository.save(contract);
    }

    @Transactional(readOnly = true)
    public List<Contract> findExpiringSoon(int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        return contractRepository.findExpiringBefore(threshold);
    }
}
