package com.pi.grafos.service;

import com.pi.grafos.dto.SugestaoDespachoDTO;
import com.pi.grafos.model.*;
import com.pi.grafos.model.enums.*; // Seus novos enums
import com.pi.grafos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DespachoService {

    @Autowired private AmbulanciaRepository ambulanciaRepository;
    @Autowired private LocalizacaoRepository localizacaoRepository;
    @Autowired private OcorrenciaRepository ocorrenciaRepository;
    @Autowired private AtendimentoRepository atendimentoRepository;
    @Autowired private GrafosService grafosService;

    // --- 1. ALGORITMO (Recebe o ENUM direto, não String) ---
    public List<SugestaoDespachoDTO> buscarAmbulanciasAptas(String nomeBairro, OcorrenciaGravidade gravidade) {

        Localizacao localOcorrencia = localizacaoRepository.findByNome(nomeBairro)
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado"));

        // Regras do PDF usando seus novos Enums
        int slaMax = (gravidade == OcorrenciaGravidade.ALTA) ? 8 : 15;
        double distMax = (gravidade == OcorrenciaGravidade.ALTA) ? 8.0 : 15.0;
        boolean exigeUTI = (gravidade == OcorrenciaGravidade.ALTA);

        List<Ambulancia> frota = ambulanciaRepository.findByStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);
        List<SugestaoDespachoDTO> sugestoes = new ArrayList<>();

        for (Ambulancia amb : frota) {
            // Filtro de Tipo
            if (exigeUTI && amb.getTipoAmbulancia() != TipoAmbulancia.UTI) continue;

            // Filtro de Equipe (Só despacha se tiver equipe)
            if (amb.getEquipes() == null || amb.getEquipes().isEmpty()) continue;

            double distancia = grafosService.calcularMenorDistancia(
                    amb.getUnidade().getIdLocal(),
                    localOcorrencia.getIdLocal()
            );

            int tempo = (int) Math.ceil(distancia * 1.0);

            boolean atende = (tempo <= slaMax) && (distancia <= distMax);

            sugestoes.add(new SugestaoDespachoDTO(
                    amb.getIdAmbulancia(),
                    amb.getPlaca(),
                    amb.getUnidade().getNome(),
                    amb.getTipoAmbulancia().toString(),
                    distancia,
                    tempo,
                    atende
            ));
        }

        sugestoes.sort(Comparator.comparing(SugestaoDespachoDTO::atendeSLA).reversed()
                .thenComparingInt(SugestaoDespachoDTO::tempoMinutos));

        return sugestoes;
    }

    // --- 2. REGISTRO (Recebe ENUM) ---
    @Transactional
    public Ocorrencia registrarOcorrencia(String bairro, String obs, OcorrenciaGravidade gravidade, TipoOcorrencia tipo) {
        Localizacao local = localizacaoRepository.findByNome(bairro).stream().findFirst().orElseThrow();

        Ocorrencia oc = new Ocorrencia();
        oc.setLocal(local);
        oc.setDescricao(obs);
        oc.setGravidade(gravidade); // Passa o objeto direto
        oc.setTipoOcorrencia(tipo);
        oc.setDataAbertura(LocalDateTime.now());
        oc.setStatus(OcorrenciaStatus.ABERTA); // Usa seu novo Enum (ABERTA é o inicial?) ou PENDENTE se tiver

        return ocorrenciaRepository.save(oc);
    }

    // --- 3. DESPACHO (Usa seu novo Enum Status) ---
    @Transactional
    public void realizarDespacho(Long idAmbulancia, Long idOcorrencia) {
        Ambulancia amb = ambulanciaRepository.findById(idAmbulancia)
                .orElseThrow(() -> new RuntimeException("Ambulância não encontrada"));
        Ocorrencia oc = ocorrenciaRepository.findById(idOcorrencia)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        // Validações
        if (amb.getStatusAmbulancia() != AmbulanciaStatus.DISPONIVEL) {
            throw new IllegalStateException("Esta ambulância não está mais disponível!");
        }
        // Valide se o status inicial é ABERTA ou PENDENTE conforme seu Enum
        if (oc.getStatus() != OcorrenciaStatus.ABERTA) {
            throw new IllegalStateException("Esta ocorrência já foi processada!");
        }

        // Atualização de Status
        amb.setStatusAmbulancia(AmbulanciaStatus.EM_ATENDIMENTO);
        oc.setStatus(OcorrenciaStatus.EM_ATENDIMENTO); // Seu Enum novo

        ambulanciaRepository.save(amb);
        ocorrenciaRepository.save(oc);

        Atendimento atendimento = new Atendimento();
        atendimento.setAmbulancia(amb);
        atendimento.setOcorrencia(oc);
        atendimento.setDataHoraDespacho(LocalDateTime.now());
        atendimentoRepository.save(atendimento);
    }
}