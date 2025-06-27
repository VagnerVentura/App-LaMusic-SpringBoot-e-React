package com.LaMusic.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.LaMusic.Mappers.AddressMapper;
import com.LaMusic.dto.AddressDTO;
import com.LaMusic.entity.Address;
import com.LaMusic.entity.User;
import com.LaMusic.repositories.AddressRepository;
import com.LaMusic.repositories.UserRepository;
import com.LaMusic.util.AuthUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper mapper;

    public AddressDTO create(AddressDTO dto) {
        UUID userId = AuthUtils.getLoggedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));

        Address address = mapper.toEntity(dto, user);
        return mapper.toDTO(addressRepository.save(address));
    }
    
    public List<AddressDTO> getAllByUser(UUID userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(mapper::toDTO)
                .toList();
    }
    
    public Address findById(UUID id) {
        return addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado"));
    }


    public AddressDTO getById(UUID id) {
        return mapper.toDTO(addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found")));
    }

    public AddressDTO update(UUID id, AddressDTO dto) {
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        existing.setType(dto.getType());
        existing.setLabel(dto.getLabel());
        existing.setRecipientName(dto.getRecipientName());
        existing.setStreet(dto.getStreet());
        existing.setNumber(dto.getNumber());
        existing.setComplement(dto.getComplement());
        existing.setNeighborhood(dto.getNeighborhood());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setZipCode(dto.getZipCode());
        existing.setCountry(dto.getCountry());
        existing.setDefault(dto.isDefault());

        return mapper.toDTO(addressRepository.save(existing));
    }

    public void delete(UUID id) {
        addressRepository.deleteById(id);
    }
}
