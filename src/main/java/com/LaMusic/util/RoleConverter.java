package com.LaMusic.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        // Garante que o valor salvo no banco seja sempre minúsculo (ex: "customer", "admin")
        return role.name().toLowerCase();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        // Converte o valor do banco (minúsculo) para o Enum (maiúsculo)
        return Stream.of(Role.values())
                .filter(r -> r.name().equalsIgnoreCase(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Valor desconhecido para Role: " + dbData));
    }
}