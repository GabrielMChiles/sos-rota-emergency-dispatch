package com.pi.grafos.service;

import com.pi.grafos.dto.SugestaoDespachoDTO;
import com.pi.grafos.model.*;
import com.pi.grafos.model.enums.AmbulanciaStatus;
import com.pi.grafos.model.enums.TipoAmbulancia;
import com.pi.grafos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DespachoService {

    @Autowired
    private AmbulanciaRepository ambulanciaRepository;

    @Autowired
    private LocalizacaoRepository localizacaoRepository;

    @Autowired
    private GrafosService grafosService; // Seu serviço com Dijkstra

    /**
     * Algoritmo Principal de Despacho
     */
    public List<SugestaoDespachoDTO> buscarAmbulanciasAptas(String nomeBairroOcorrencia, String gravidade) {

        // 1. Identificar o nó de destino (Ocorrência)
        Localizacao localOcorrencia = localizacaoRepository.findByNome(nomeBairroOcorrencia)
                .stream()   // 1. Transforma a Lista em Stream
                .findFirst() // 2. Pega o primeiro item (vira Optional)
                .orElseThrow(() -> new RuntimeException("Bairro não encontrado: " + nomeBairroOcorrencia));

        // 2. Definir SLA e Filtros baseados na Gravidade
        int slaMaximoMinutos = gravidade.equalsIgnoreCase("ALTA") ? 8 : 15;
        boolean exigeUTI = gravidade.equalsIgnoreCase("ALTA");

        // 3. Buscar TODAS ambulâncias DISPONÍVEIS no banco
        // (Aqui assumimos que DISPONIVEL já valida se tem equipe. Se não validar, adicione a lógica aqui)
        List<Ambulancia> frotaDisponivel = ambulanciaRepository.findByStatusAmbulancia(AmbulanciaStatus.DISPONIVEL);

        List<SugestaoDespachoDTO> sugestoes = new ArrayList<>();

        // 4. Carregar o Grafo na memória (Garante que está atualizado)
        var grafo = grafosService.getGrafoAtualizado(); // Você precisará criar esse método no GrafosService

        // 5. Iterar sobre cada ambulância para calcular rota
        for (Ambulancia amb : frotaDisponivel) {

            // Filtro de Tipo (Regra: Alta exige UTI. Média/Baixa aceita qualquer uma)
            if (exigeUTI && amb.getTipoAmbulancia() != TipoAmbulancia.UTI) {
                continue; // Pula essa ambulância
            }

            Localizacao baseOrigem = amb.getUnidade(); // Onde a ambulância está parada

            // --- O CÁLCULO DE ROTA (DIJKSTRA) ---

            double distanciaKm = grafosService.calcularMenorDistancia(
                    baseOrigem.getIdLocal(),
                    localOcorrencia.getIdLocal()
            );

            // Regra do PDF: 1 km = 1 minuto
            int tempoEstimado = (int) Math.ceil(distanciaKm * 1.0);

            boolean atendeSla = tempoEstimado <= slaMaximoMinutos;

            // Cria o DTO
            sugestoes.add(new SugestaoDespachoDTO(
                    amb.getIdAmbulancia(),
                    amb.getPlaca(),
                    baseOrigem.getNome(),
                    amb.getTipoAmbulancia().toString(),
                    distanciaKm,
                    tempoEstimado,
                    atendeSla
            ));
        }

        // 6. Ordenar: Primeiro as que atendem o SLA, depois por menor tempo
        sugestoes.sort(Comparator.comparing(SugestaoDespachoDTO::atendeSLA).reversed()
                .thenComparingInt(SugestaoDespachoDTO::tempoMinutos));

        return sugestoes;
    }
}