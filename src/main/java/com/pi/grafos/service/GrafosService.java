package com.pi.grafos.service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

// Classe que representa uma aresta do grafo, representa o vértice de destino e a distância
class Aresta {
    private int vizinho;
    private double distancia;

    public Aresta(int destino, double distancia) {
        this.vizinho = destino;
        this.distancia = distancia;
    }

    public int getVizinho() {
        return vizinho;
    }

    public double getDistancia() {
        return distancia;
    }
}

// Classe que representa o grafo
class ConstruirGrafo {
    private Map<Integer, List<Aresta>> caminho = new HashMap<>();

    public void addAresta(int origem, int destino, double distancia) {
    	
        if (!caminho.containsKey(origem)) {
            caminho.put(origem, new ArrayList<Aresta>());
        }
        caminho.get(origem).add(new Aresta(destino, distancia));

        if (!caminho.containsKey(destino)) {
            caminho.put(destino, new ArrayList<Aresta>());
        }
        caminho.get(destino).add(new Aresta(origem, distancia));
    }

    public Map<Integer, List<Aresta>> getCaminho() {
        return caminho;
    }
}

@Service
public class GrafosService {

    @Value("classpath:ruas_conexoes.csv")
    private Resource recurso;

    // Dijkstra para menor caminho
    public static List<Integer> menorCaminho(ConstruirGrafo grafo, int origem, int destino) {
        Map<Integer, Double> distancia = new HashMap<>();
        Map<Integer, Integer> anterior = new HashMap<>();

        // Usando PriorityQueue com nós e distância double
        PriorityQueue<double[]> fila = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));

        for (int vertice : grafo.getCaminho().keySet()) {
            distancia.put(vertice, Double.MAX_VALUE);
        }

        distancia.put(origem, 0.0);
        fila.add(new double[]{origem, 0.0});

        while (!fila.isEmpty()) {
            int atual = (int) fila.poll()[0];

            for (Aresta ar : grafo.getCaminho().get(atual)) {
                int vizinho = ar.getVizinho();
                double novaDist = distancia.get(atual) + ar.getDistancia();

                if (novaDist < distancia.getOrDefault(vizinho, Double.MAX_VALUE)) {
                    distancia.put(vizinho, novaDist);
                    anterior.put(vizinho, atual);
                    fila.add(new double[]{vizinho, novaDist});
                }
            }
        }

        // Reconstruindo caminho
        LinkedList<Integer> caminho = new LinkedList<>();
        Integer atual = destino;
        while (atual != null) {
            caminho.addFirst(atual);
            atual = anterior.get(atual);
        }

        // Verifica se o destino é alcançável
        if (caminho.isEmpty() || caminho.getFirst() != origem) {
            return new ArrayList<>(); // caminho inexistente
        }

        return caminho;
    }

    // Calcula a distância total de um caminho
    public static double calculaDistanciaTotal(ConstruirGrafo g, List<Integer> caminho) {
        double soma = 0;
        for (int i = 0; i < caminho.size() - 1; i++) {
            int atual = caminho.get(i);
            int prox = caminho.get(i + 1);

            for (Aresta a : g.getCaminho().get(atual)) {
                if (a.getVizinho() == prox) {
                    soma += a.getDistancia();
                    break;
                }
            }
        }
        return soma;
    }
}
