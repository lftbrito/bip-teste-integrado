package com.example.backend.repository;

import com.example.backend.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {

    @Query("SELECT b FROM Beneficio b WHERE b.ativo = true ORDER BY b.nome")
    List<Beneficio> findAllAtivos();

    @Query("SELECT b FROM Beneficio b WHERE b.id = :id AND b.ativo = true")
    Optional<Beneficio> findByIdAndAtivo(Long id);

    boolean existsByNome(String nome);

    @Query("SELECT COUNT(b) > 0 FROM Beneficio b WHERE b.nome = :nome AND b.id <> :id")
    boolean existsByNomeAndIdNot(String nome, Long id);

}
