package com.pi.grafos.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

@Service
public class GrafosService {

	class Aresta {
		private int verticeDestino;
		private double pesoDistancia;

		public Aresta(int verticeDestino, double pesoDistancia) {
			this.verticeDestino = verticeDestino;
			this.pesoDistancia = pesoDistancia;
		}

		public int getVerticeDestino() {
			return verticeDestino;
		}

		public double getPesoDistancia() {
			return pesoDistancia;
		}
	}

	public class Grafo {
		private Map<Integer, List<Aresta>> listaAdjacencia = new HashMap<>();

		public void adicionarAresta(int origem, int destino, double distancia) {

			listaAdjacencia.computeIfAbsent(origem, x -> new ArrayList<>()).add(new Aresta(destino, distancia));

			listaAdjacencia.computeIfAbsent(destino, x -> new ArrayList<>()).add(new Aresta(origem, distancia));
		}

		public Map<Integer, List<Aresta>> getListaAdjacencia() {
			return listaAdjacencia;
		}
	}

	class NoDijkstra {
		public int vertice;
		public double distanciaAcumulada;

		public NoDijkstra(int vertice, double distanciaAcumulada) {
			this.vertice = vertice;
			this.distanciaAcumulada = distanciaAcumulada;
		}
	}

	public class DijkstraResultado {
		public Map<Integer, Double> menorDistancia;
		public Map<Integer, Integer> verticeAnterior;

		public DijkstraResultado(Map<Integer, Double> menorDistancia, Map<Integer, Integer> verticeAnterior) {
			this.menorDistancia = menorDistancia;
			this.verticeAnterior = verticeAnterior;
		}
	}

	public class RotaCalculada {
		public int verticeOrigem;
		public double distanciaTotal;
		public List<Integer> caminhoCompleto;

		public RotaCalculada(int verticeOrigem, double distanciaTotal, List<Integer> caminhoCompleto) {
			this.verticeOrigem = verticeOrigem;
			this.distanciaTotal = distanciaTotal;
			this.caminhoCompleto = caminhoCompleto;
		}
	}

	public DijkstraResultado executarDijkstra(Grafo grafo, int destino) {

		Map<Integer, Double> menorDistancia = new HashMap<>();
		Map<Integer, Integer> verticeAnterior = new HashMap<>();

		for (int vertice : grafo.getListaAdjacencia().keySet()) {
			menorDistancia.put(vertice, Double.MAX_VALUE);
		}

		menorDistancia.put(destino, 0.0);

		PriorityQueue<NoDijkstra> filaPrioridade = new PriorityQueue<>(
				Comparator.comparingDouble(no -> no.distanciaAcumulada));

		filaPrioridade.add(new NoDijkstra(destino, 0));

		while (!filaPrioridade.isEmpty()) {
			NoDijkstra noAtual = filaPrioridade.poll();
			int verticeAtual = noAtual.vertice;

			for (Aresta aresta : grafo.getListaAdjacencia().get(verticeAtual)) {

				double novaDistancia = menorDistancia.get(verticeAtual) + aresta.getPesoDistancia();

				if (novaDistancia < menorDistancia.get(aresta.getVerticeDestino())) {
					menorDistancia.put(aresta.getVerticeDestino(), novaDistancia);
					verticeAnterior.put(aresta.getVerticeDestino(), verticeAtual);
					filaPrioridade.add(new NoDijkstra(aresta.getVerticeDestino(), novaDistancia));
				}
			}
		}

		return new DijkstraResultado(menorDistancia, verticeAnterior);
	}

	public List<Integer> reconstruirCaminho(DijkstraResultado resultado, int origem, int destino) {

		if (origem == destino)
			return List.of(destino);

		if (!resultado.verticeAnterior.containsKey(origem))
			return List.of();

		LinkedList<Integer> caminho = new LinkedList<>();
		Integer atual = origem;

		while (atual != null && atual != destino) {
			caminho.add(atual);
			atual = resultado.verticeAnterior.get(atual);
		}

		caminho.add(destino);
		return caminho;
	}

	public List<RotaCalculada> calcularRotasOrdenadas(Grafo grafo, int destino, List<Integer> bases) {

		DijkstraResultado resultadoDijkstra = executarDijkstra(grafo, destino);

		List<RotaCalculada> rotas = new ArrayList<>();

		for (int base : bases) {

			double distanciaBaseParaDestino = resultadoDijkstra.menorDistancia.getOrDefault(base, Double.MAX_VALUE);

			List<Integer> caminhoCompleto = distanciaBaseParaDestino == Double.MAX_VALUE ? List.of()
					: reconstruirCaminho(resultadoDijkstra, base, destino);

			rotas.add(new RotaCalculada(base, distanciaBaseParaDestino, caminhoCompleto));
		}

		rotas.sort(Comparator.comparingDouble(r -> r.distanciaTotal));

		return rotas;
	}
}
