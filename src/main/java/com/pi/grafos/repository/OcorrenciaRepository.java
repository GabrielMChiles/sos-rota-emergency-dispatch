package com.pi.grafos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.TipoOcorrencia;
import com.pi.grafos.model.enums.OcorrenciaGravidade;
import com.pi.grafos.model.enums.OcorrenciaStatus;

@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    // Busca específica por ID (Opcional, pois findById já existe, mas mantive para compatibilidade)
    Optional<Ocorrencia> findByIdOcorrencia(Long id);

    // Buscas auxiliares
    List<Ocorrencia> findByLocal(Localizacao local);
    List<Ocorrencia> findByTipoOcorrencia(TipoOcorrencia tipo);

    // Busca por Gravidade (Alta, Média, Baixa)
    List<Ocorrencia> findByGravidade(OcorrenciaGravidade gravidade);

    // --- O QUE FALTAVA (Essencial para o Dashboard) ---
    // Permite filtrar: "Me dê todas as ocorrências ABERTAS" ou "EM_ATENDIMENTO"
    List<Ocorrencia> findByStatus(OcorrenciaStatus status);
}