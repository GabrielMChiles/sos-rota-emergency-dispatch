package com.pi.grafos.service;

import com.pi.grafos.model.Ambulancia;
import com.pi.grafos.model.Atendimento;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.OcorrenciaStatus; // <--- Nome corrigido!
import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.AtendimentoRepository;
import com.pi.grafos.repository.OcorrenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OcorrenciaService {

    @Autowired private OcorrenciaRepository ocorrenciaRepository;
    @Autowired private AtendimentoRepository atendimentoRepository;
    @Autowired private AmbulanciaRepository ambulanciaRepository;

    // --- LISTAS PARA O DASHBOARD ---

    /**
     * Lista ocorrências que estão aguardando despacho (Status: ABERTA)
     */
    public List<Ocorrencia> listarPendentes() {
        // Usando seu Enum correto: ABERTA
        return ocorrenciaRepository.findByStatus(OcorrenciaStatus.ABERTA);
    }

    /**
     * Lista ocorrências que já têm ambulância a caminho (Status: EM_ATENDIMENTO)
     */
    public List<Ocorrencia> listarEmAtendimento() {
        // Usando seu Enum correto: EM_ATENDIMENTO
        return ocorrenciaRepository.findByStatus(OcorrenciaStatus.EM_ATENDIMENTO);
    }

    // --- AÇÃO: CONCLUIR OCORRÊNCIA ---
    @Transactional
    public void concluirOcorrencia(Long idOcorrencia) {
        Ocorrencia oc = ocorrenciaRepository.findById(idOcorrencia)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        // Validação de segurança
        if (oc.getStatus() != OcorrenciaStatus.EM_ATENDIMENTO) {
            throw new IllegalStateException("Apenas ocorrências em atendimento podem ser concluídas.");
        }

        // 1. Finaliza a Ocorrência
        oc.setStatus(OcorrenciaStatus.CONCLUIDA);
        ocorrenciaRepository.save(oc);

        // 2. Libera a Ambulância e fecha o histórico
        // Busca o último atendimento vinculado a esta ocorrência
        Atendimento atendimento = atendimentoRepository.findTopByOcorrenciaOrderByDataHoraDespachoDesc(oc);

        if (atendimento != null) {
            // Fecha a hora do atendimento
            atendimento.setDataHoraConclusao(LocalDateTime.now());
            atendimentoRepository.save(atendimento);

            // Libera a ambulância para a próxima missão
            Ambulancia amb = atendimento.getAmbulancia();
            amb.setStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);
            ambulanciaRepository.save(amb);
        }
    }

    // --- AÇÃO: CANCELAR COM JUSTIFICATIVA ---
    @Transactional
    public void cancelarOcorrencia(Long idOcorrencia, String justificativa) {
        if (justificativa == null || justificativa.trim().length() < 5) {
            throw new IllegalArgumentException("É obrigatório fornecer uma justificativa válida para o cancelamento.");
        }

        Ocorrencia oc = ocorrenciaRepository.findById(idOcorrencia)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        // Se a ocorrência já estiver com ambulância na rua, precisamos liberar a ambulância primeiro
        if (oc.getStatus() == OcorrenciaStatus.EM_ATENDIMENTO) {
            Atendimento atendimento = atendimentoRepository.findTopByOcorrenciaOrderByDataHoraDespachoDesc(oc);
            if (atendimento != null) {
                atendimento.setDataHoraConclusao(LocalDateTime.now());
                atendimentoRepository.save(atendimento);

                Ambulancia amb = atendimento.getAmbulancia();
                amb.setStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);
                ambulanciaRepository.save(amb);
            }
        }

        // Atualiza o status da ocorrência para CANCELADA
        oc.setStatus(OcorrenciaStatus.CANCELADA);

        // Registra a justificativa na descrição (Log de auditoria simples)
        String novaDescricao = (oc.getDescricao() != null ? oc.getDescricao() : "") +
                " [CANCELADO EM " + LocalDateTime.now() + ": " + justificativa + "]";

        // Garante que não estoure o limite do banco se a descrição for pequena
        if (novaDescricao.length() > 255) {
            novaDescricao = novaDescricao.substring(0, 255);
        }

        oc.setDescricao(novaDescricao);
        ocorrenciaRepository.save(oc);
    }
}