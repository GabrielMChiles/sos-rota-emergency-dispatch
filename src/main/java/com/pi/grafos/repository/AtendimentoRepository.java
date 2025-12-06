package com.pi.grafos.repository;

import com.pi.grafos.model.Atendimento;
import com.pi.grafos.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    /**
     * Busca o ÚLTIMO atendimento registrado para uma ocorrência específica.
     * Útil para saber qual ambulância está vinculada atualmente antes de concluir ou cancelar.
     * * @param ocorrencia A ocorrência que queremos consultar
     * @return O objeto Atendimento mais recente (ou null se não houver)
     */
    Atendimento findTopByOcorrenciaOrderByDataHoraDespachoDesc(Ocorrencia ocorrencia);
}
