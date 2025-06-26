package com.LaMusic.util;

import com.LaMusic.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) {
            return null;
        }
        // Padroniza a escrita no banco para sempre ser minúscula (ex: "customer", "admin")
        return role.name().toLowerCase();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        // Converte o valor do banco (seja ele qual for) para o Enum correto
        switch (dbData.toLowerCase()) {
            case "customer":
            case "user": // CORREÇÃO: Adicionado para tratar o legado "USER" como "CUSTOMER"
                return Role.CUSTOMER;
            case "admin":
                return Role.ADMIN;
            default:
                // Lança uma exceção se encontrar um valor realmente desconhecido
                throw new IllegalArgumentException("Valor desconhecido para Role no banco de dados: " + dbData);
        }
    }
}