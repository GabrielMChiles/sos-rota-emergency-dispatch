package com.pi.grafos.config;

import com.pi.grafos.model.Cidade;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Rua;
import com.pi.grafos.model.TipoOcorrencia;
import com.pi.grafos.model.enums.TipoLocalizacao;
import com.pi.grafos.repository.CidadeRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.repository.RuaRepository;
import com.pi.grafos.repository.TipoOcorrenciaRepository; // <--- Novo Import

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SistemaSeeder implements CommandLineRunner {

    private final LocalizacaoRepository localizacaoRepository;
    private final CidadeRepository cidadeRepository;
    private final RuaRepository ruaRepository;
    private final TipoOcorrenciaRepository tipoOcorrenciaRepository; // <--- Nova Dependência

    // LISTA DE IDs QUE SÃO BASES_AMBULANCIA
    // 2, 9, 11, 13, 14, 15, 17, 20
    private final List<Long> idsBases = Arrays.asList(2L, 9L, 11L, 13L, 14L, 15L, 17L, 20L);

    // Construtor atualizado com o novo repositório
    public SistemaSeeder(LocalizacaoRepository locRepo,
                         CidadeRepository cidRepo,
                         RuaRepository ruaRepo,
                         TipoOcorrenciaRepository tipoRepo) {
        this.localizacaoRepository = locRepo;
        this.cidadeRepository = cidRepo;
        this.ruaRepository = ruaRepo;
        this.tipoOcorrenciaRepository = tipoRepo;
    }

    @Override
    public void run(String... args) {
        // 1. Carrega os tipos de ocorrência (Essencial para o formulário abrir)
        carregarTiposOcorrencia();

        // 2. Carrega o Grafo (Bairros e Ruas)
        if (localizacaoRepository.count() == 0) {
            carregarGrafo();
        }
    }

    // --- NOVO METODO IMPLEMENTADO ---
    private void carregarTiposOcorrencia() {
        if (tipoOcorrenciaRepository.count() == 0) {
            System.out.println("⚡ Cadastrando tipos de ocorrência padrão...");

            criarTipoSeNaoExistir("Acidente de Trânsito");
            criarTipoSeNaoExistir("Mal Súbito");
            criarTipoSeNaoExistir("Trauma / Queda");
            criarTipoSeNaoExistir("Parada Cardiorrespiratória");
            criarTipoSeNaoExistir("Incêndio");
            criarTipoSeNaoExistir("Outros");
        }
    }

    private void criarTipoSeNaoExistir(String nome) {
        // Verifica se já existe para não duplicar
        if (tipoOcorrenciaRepository.findByNomeTipoOcorrencia(nome).isEmpty()) {
            TipoOcorrencia tipo = new TipoOcorrencia();
            tipo.setNomeTipoOcorrencia(nome); // Usa o seu setter correto
            tipoOcorrenciaRepository.save(tipo);
            System.out.println("   -> Tipo cadastrado: " + nome);
        }
    }
    // --------------------------------

    private void carregarGrafo() {
        System.out.println("Carregando e Classificando mapa de Cidália...");

        Cidade cidade = cidadeRepository.findByNomeCidade("Cidália")
                .orElseGet(() -> cidadeRepository.save(new Cidade("Cidália")));

        try {
            InputStream is = getClass().getResourceAsStream("/bairros.csv");
            if (is == null) {
                System.err.println("❌ ARQUIVO bairros.csv NÃO ENCONTRADO!");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String linha;

            // reader.readLine(); // Descomente se tiver cabeçalho no CSV

            while ((linha = reader.readLine()) != null) {
                try {
                    String[] partes = linha.split(",");
                    if (partes.length >= 2) {
                        Long id = Long.parseLong(partes[0].trim());
                        String nome = partes[1].trim();

                        Localizacao local = new Localizacao();
                        local.setIdLocal(id);
                        local.setNome(nome);
                        local.setCidade(cidade);

                        // --- LÓGICA DE CLASSIFICAÇÃO AUTOMÁTICA ---
                        if (idsBases.contains(id)) {
                            local.setTipo(TipoLocalizacao.BASE_AMBULANCIA); // É Base (Cinza)
                        } else {
                            local.setTipo(TipoLocalizacao.BAIRRO); // É Bairro Comum (Azul)
                        }

                        localizacaoRepository.save(local);
                        // System.out.println("Salvo: " + nome + " como " + local.getTipo());
                    }
                } catch (Exception ex) {
                    System.err.println("Erro linha bairros: " + linha);
                }
            }
            System.out.println("✅ Mapa carregado e classificado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- CARGA DAS RUAS ---
        try {
            InputStream is = getClass().getResourceAsStream("/ruas_conexoes.csv");
            if (is == null) {
                System.err.println("❌ ARQUIVO ruas_conexoes.csv NÃO ENCONTRADO!");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String linha;

            // reader.readLine(); // Descomente se tiver cabeçalho

            while ((linha = reader.readLine()) != null) {
                try {
                    String[] partes = linha.split(",");

                    if (partes.length >= 4) {
                        // Supondo formato: ID, OrigemID, DestinoID, Distancia
                        Long origemId = Long.parseLong(partes[1].trim());
                        Long destinoId = Long.parseLong(partes[2].trim());
                        Double distancia = Double.parseDouble(partes[3].trim());

                        Optional<Localizacao> origemOpt = localizacaoRepository.findById(origemId);
                        Optional<Localizacao> destinoOpt = localizacaoRepository.findById(destinoId);

                        if (origemOpt.isPresent() && destinoOpt.isPresent()) {
                            Localizacao origem = origemOpt.get();
                            Localizacao destino = destinoOpt.get();

                            Rua rua = new Rua();
                            rua.setDistancia(distancia);
                            rua.setNomeRua("Conexão " + origem.getNome() + " -> " + destino.getNome());
                            rua.setOrigem(origem);
                            rua.setDestino(destino);

                            if (origem.getCidade() != null) {
                                rua.setCidade(origem.getCidade());
                            } else {
                                System.err.println("Erro: Bairro de origem " + origemId + " não tem cidade vinculada.");
                                continue;
                            }

                            ruaRepository.save(rua);
                            // System.out.println("Rua salva: " + rua.getNomeRua() + " (" + distancia + "km)");
                        } else {
                            System.err.println("Ignorado: Origem (" + origemId + ") ou Destino (" + destinoId + ") não encontrados no banco.");
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Erro ao processar linha rua: " + linha + " | Erro: " + ex.getMessage());
                }
            }
            System.out.println("✅ Importação de ruas finalizada!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}