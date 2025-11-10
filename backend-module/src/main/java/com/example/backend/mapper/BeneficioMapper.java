package com.example.backend.mapper;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.BeneficioRequestDTO;
import com.example.backend.entity.Beneficio;
import org.springframework.stereotype.Component;

@Component
public class BeneficioMapper {

    public BeneficioDTO toDTO(Beneficio entity) {
        if (entity == null) {
            return null;
        }

        BeneficioDTO dto = new BeneficioDTO(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getAtivo(),
                entity.getVersion()
        );
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Beneficio toEntity(BeneficioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Beneficio entity = new Beneficio();
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setValor(dto.getValor());
        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        return entity;
    }

    public void updateEntity(Beneficio entity, BeneficioRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setValor(dto.getValor());
        entity.setAtivo(dto.getAtivo());
    }
}
