package com.LaMusic.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LaMusic.dto.AddressDTO;
import com.LaMusic.services.AddressService;
import com.LaMusic.util.AuthUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService service;

    @GetMapping("/me")
    public ResponseEntity<List<AddressDTO>> getMyAddresses() {
        UUID userId = AuthUtils.getLoggedUserId();
        return ResponseEntity.ok(service.getAllByUser(userId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressDTO>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getAllByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<AddressDTO> create(@RequestBody AddressDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> update(@PathVariable UUID id, @RequestBody AddressDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}