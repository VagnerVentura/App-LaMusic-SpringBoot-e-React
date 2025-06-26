package com.LaMusic.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.LaMusic.Mappers.PaymentMapper;
import com.LaMusic.dto.PaymentDTO;
import com.LaMusic.entity.Payment;
import com.LaMusic.repositories.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;

    public List<PaymentDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public PaymentDTO findById(UUID id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
        return mapper.toDTO(payment);
    }

    public PaymentDTO create(PaymentDTO dto) {
        Payment payment = mapper.toEntity(dto);
        payment.setCreatedAt(OffsetDateTime.now());
        return mapper.toDTO(repository.save(payment));
    }

    public PaymentDTO update(UUID id, PaymentDTO dto) {
        Payment existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        existing.setAmount(dto.getAmount());
        existing.setMethod(dto.getMethod());
        existing.setStatus(dto.getStatus());
        existing.setTransactionId(dto.getTransactionId());
        existing.setErrorCode(dto.getErrorCode());
        existing.setErrorMessage(dto.getErrorMessage());
        existing.setAttemptNumber(dto.getAttemptNumber());
        existing.setProcessedAt(dto.getProcessedAt());

        return mapper.toDTO(repository.save(existing));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

	public void create(Payment payment) {
		repository.save(payment);		
	}
}
