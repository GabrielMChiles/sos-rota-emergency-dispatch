package com.pi.grafos.service;

import com.pi.grafos.model.Rua;
import com.pi.grafos.repository.CidadeRepository;
import com.pi.grafos.repository.RuaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GrafosService {

    @Autowired
    private CidadeRepository cidadeRepository;

    @Autowired
    private RuaRepository ruaRepository;

    // --- CACHE DO GRAFO (Memória RAM) ---
    private ConstruirGrafo grafoCache;

    /**
     * Inicializa o grafo automaticamente quando o sistema sobe.
     */
    @PostConstruct
    public void atualizarGrafo() {
        System.out.println("--- [GRAFO] Iniciando construção do grafo em memória... ---");

        List<Rua> todasRuas = ruaRepository.findAll();

        if (todasRuas.isEmpty()) {
            System.err.println("--- [GRAFO] ERRO CRÍTICO: Nenhuma rua encontrada no banco! O Seeder rodou? ---");
            return;
        }

        ConstruirGrafo novoGrafo = new ConstruirGrafo();
        for (Rua rua : todasRuas) {
            if (rua.getOrigem() != null && rua.getDestino() != null && rua.getDistancia() != null) {
                novoGrafo.addAresta(
                        rua.getOrigem().getIdLocal(),
                        rua.getDestino().getIdLocal(),
                        rua.getDistancia()
                );
            }
        }

        this.grafoCache = novoGrafo;
        System.out.println("--- [GRAFO] Grafo montado com sucesso! Nós carregados. ---");
    }

    /**
     * NOVO MÉTODO (O que faltava):
     * Retorna o objeto do grafo para quem pedir.
     * Se estiver nulo (por algum erro de inicialização), tenta recriar.
     */
    public ConstruirGrafo getGrafoAtualizado() {
        if (this.grafoCache == null) {
            atualizarGrafo();
        }
        return this.grafoCache;
    }

    /**
     * Calcula a menor distância entre dois pontos usando Dijkstra.
     */
    public double calcularMenorDistancia(Long idOrigem, Long idDestino) {
        // Garante que o grafo existe antes de calcular
        if (this.grafoCache == null) {
            atualizarGrafo();
            if (this.grafoCache == null) return Double.MAX_VALUE;
        }

        // Dijkstra Clássico
        Map<Long, Double> distancias = new HashMap<>();
        PriorityQueue<NoDijkstra> filaPrioridade = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distanciaAtual));

        for (Long vertice : grafoCache.getVertices()) {
            distancias.put(vertice, Double.MAX_VALUE);
        }

        distancias.put(idOrigem, 0.0);
        filaPrioridade.add(new NoDijkstra(idOrigem, 0.0));

        while (!filaPrioridade.isEmpty()) {
            NoDijkstra atual = filaPrioridade.poll();

            if (atual.idVertice.equals(idDestino)) {
                return distancias.get(idDestino);
            }

            if (atual.distanciaAtual > distancias.get(atual.idVertice)) {
                continue;
            }

            List<Aresta> vizinhos = grafoCache.getVizinhos(atual.idVertice);
            if (vizinhos != null) {
                for (Aresta aresta : vizinhos) {
                    double novaDistancia = atual.distanciaAtual + aresta.peso;

                    if (novaDistancia < distancias.getOrDefault(aresta.destino, Double.MAX_VALUE)) {
                        distancias.put(aresta.destino, novaDistancia);
                        filaPrioridade.add(new NoDijkstra(aresta.destino, novaDistancia));
                    }
                }
            }
        }

        return distancias.getOrDefault(idDestino, Double.MAX_VALUE);
    }

    // =========================================================================
    // CLASSES INTERNAS
    // =========================================================================

    private record NoDijkstra(Long idVertice, double distanciaAtual) {}

    public static class Aresta {
        Long destino;
        double peso;

        public Aresta(Long destino, double peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    public static class ConstruirGrafo {
        private final Map<Long, List<Aresta>> adjacencia = new HashMap<>();

        public void addAresta(Long origem, Long destino, double peso) {
            adjacencia.computeIfAbsent(origem, k -> new ArrayList<>()).add(new Aresta(destino, peso));
            adjacencia.computeIfAbsent(destino, k -> new ArrayList<>()).add(new Aresta(origem, peso));
        }

        public List<Aresta> getVizinhos(Long origem) {
            return adjacencia.get(origem);
        }

        public Set<Long> getVertices() {
            return adjacencia.keySet();
        }
    }
}