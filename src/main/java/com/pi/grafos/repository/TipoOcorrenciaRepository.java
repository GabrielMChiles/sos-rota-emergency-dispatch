package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional; // Importante para o Seeder

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.TipoOcorrencia;

@Repository
public interface TipoOcorrenciaRepository extends JpaRepository<TipoOcorrencia, Long> {

    // 1. CORREÇÃO DE TIPO: Retorna List<TipoOcorrencia>, não do Repository
    // (Opcional, se você usar busca por nome parcial em algum lugar)
    List<TipoOcorrencia> findByNomeTipoOcorrenciaIgnoreCaseLike(String nome);

    // 2. METODO NOVO PARA O SEEDER
    // Precisamos buscar o nome exato para não cadastrar "Acidente" duas vezes
    Optional<TipoOcorrencia> findByNomeTipoOcorrencia(String nome);
}