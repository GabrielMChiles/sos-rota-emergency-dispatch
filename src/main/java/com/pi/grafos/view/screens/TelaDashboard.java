package com.pi.grafos.view.screens;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_BRANCO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_CLARO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO2;
import static com.pi.grafos.view.styles.AppStyles.FONTE_CORPO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_PEQUENA;
import static com.pi.grafos.view.styles.AppStyles.FONTE_SUBTITULO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_CINZA_FUNDO;
import static com.pi.grafos.view.styles.AppStyles.HEX_SIDEBAR_BG;
import static com.pi.grafos.view.styles.AppStyles.HEX_SIDEBAR_HOVER;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

@Component
public class TelaDashboard {




    // --- CONFIGURAÇÕES VISUAIS ---
    private static final double LARGURA_SIDEBAR = 240;
    private static final double LARGURA_RESUMO = 320;

    // --- ESTADO DA TELA (Variáveis globais da classe) ---
    private HBox rootLayout;      // O layout principal que segura tudo
    private Region centerMap;     // O mapa original
    private List<Button> botoesMenu = new ArrayList<>(); // Lista para controlar qual botão está ativo

    public Scene criarCena(Stage stage) {

        // =============================================================================================
        // 1. COLUNA ESQUERDA: MENU LATERAL
        // =============================================================================================
        VBox sidebar = new VBox(10); // Espaçamento reduzido para 10
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(LARGURA_SIDEBAR);
        sidebar.setMinWidth(LARGURA_SIDEBAR);
        sidebar.setStyle("-fx-background-color: " + HEX_SIDEBAR_BG + ";");
        sidebar.setAlignment(Pos.TOP_CENTER);

        // --- LOGO ---
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo2.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(120);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { System.err.println("Erro logo dashboard"); }

        // --- TÍTULO DO PAINEL ---
        Label lblTituloPainel = new Label("PAINEL");
        lblTituloPainel.setFont(FONTE_TITULO);
        lblTituloPainel.setTextFill(COR_TEXTO_BRANCO);

        // --- BOTÕES DE NAVEGAÇÃO ---
        Button btnDashboard = criarBotaoMenu("Dashboard", "🏠");
        // Ação: Voltar para o Mapa e pintar de vermelho
        btnDashboard.setOnAction(e -> {
            atualizarEstiloBotao(btnDashboard);
            setConteudoCentral(centerMap);
        });

        Button btnNovaOcorrencia = criarBotaoMenu("Nova Ocorrência", "➕");
        // Ação: Mostrar formulário de cadastro
        btnNovaOcorrencia.setOnAction(e -> {
            atualizarEstiloBotao(btnNovaOcorrencia);
            // Chama o formulário de nova ocorrência
            setConteudoCentral(new FormularioOcorrenciaView().criarView());
        });

        Button btnFrota = criarBotaoMenu("Ambulâncias", "🚑");
        btnFrota.setOnAction(e -> {
            atualizarEstiloBotao(btnFrota);
            setConteudoCentral(criarPlaceholderFormulario("Gestão de Frota"));
        });

        Button btnEquipe = criarBotaoMenu("Equipe", "👨‍⚕️");
        btnEquipe.setOnAction(e -> {
            atualizarEstiloBotao(btnEquipe);
            setConteudoCentral(new FormularioEquipeView().criarView());
        });

        Button btnColaborador = criarBotaoMenu("Colaboradores", "⚕");
        btnColaborador.setOnAction(e -> {
            atualizarEstiloBotao(btnColaborador);
            setConteudoCentral(new GestaoFuncionariosView().criarView());
        });

        Button btnRelatorio = criarBotaoMenu("Relatórios", "");
        btnRelatorio.setOnAction(e -> {
            atualizarEstiloBotao(btnRelatorio);
            setConteudoCentral(criarPlaceholderFormulario("Relatório"));
        });



        // Espacador
        Region spacerMenu = new Region();
        VBox.setVgrow(spacerMenu, Priority.ALWAYS);

        Button btnSair = criarBotaoMenu("Sair do Sistema", "🚪");
        btnSair.setOnAction(e -> {
            System.out.println("Saindo...");
            stage.close();
        });

        sidebar.getChildren().addAll(logoView, lblTituloPainel, btnDashboard, btnNovaOcorrencia, btnFrota, btnEquipe, btnColaborador, btnRelatorio, spacerMenu, btnSair);


        // =============================================================================================
        // 2. COLUNA CENTRAL: MAPA DA CIDADE (Salvo na variável global)
        // =============================================================================================
        centerMap = new Region(); // Inicializamos a variável global

        try {
            Image imgMap = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            BackgroundSize bgSize = new BackgroundSize(1.0, 1.0, true, true, false, true); // COVER
            BackgroundImage bgImage = new BackgroundImage(
                    imgMap,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    bgSize
            );
            centerMap.setBackground(new Background(bgImage));
        } catch (Exception e) {
            centerMap.setStyle("-fx-background-color: #CBD5E1;");
        }

        // =============================================================================================
        // 3. COLUNA DIREITA: RESUMO
        // =============================================================================================
        VBox rightPanel = new VBox(25);
        rightPanel.setPadding(new Insets(30));
        rightPanel.setPrefWidth(LARGURA_RESUMO);
        rightPanel.setMinWidth(LARGURA_RESUMO);
        rightPanel.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, -5, 0);");

        // --- Título Resumo ---
        Label lblResumo = new Label("Resumo Operacional");
        lblResumo.setFont(FONTE_SUBTITULO);
        lblResumo.setTextFill(COR_AZUL_NOTURNO);

        // --- SEÇÃO 1: LISTA DE OCORRÊNCIAS ---
        Label lblPendentes = new Label("Ocorrências Pendentes");
        lblPendentes.setFont(FONTE_CORPO);
        lblPendentes.setTextFill(COR_TEXTO_CLARO);

        VBox listaContainer = new VBox(10);

        // Adicionando ocorrências na força bruta para visualizacao
        listaContainer.getChildren().add(criarCardOcorrencia("Acidente Centro", "Alta Prioridade", HEX_VERMELHO));
        listaContainer.getChildren().add(criarCardOcorrencia("Mal Súbito - Jd. América", "Média Prioridade", "#F59E0B"));
        listaContainer.getChildren().add(criarCardOcorrencia("Transporte Eletivo", "Baixa Prioridade", "#10B981"));
        listaContainer.getChildren().add(criarCardOcorrencia("Colisão Leve", "Baixa Prioridade", "#10B981"));
        listaContainer.getChildren().add(criarCardOcorrencia("Mal Súbito - Jd. América", "Média Prioridade", "#F59E0B"));
        listaContainer.getChildren().add(criarCardOcorrencia("Transporte Eletivo", "Baixa Prioridade", "#10B981"));
        listaContainer.getChildren().add(criarCardOcorrencia("Colisão Leve", "Baixa Prioridade", "#10B981"));


        ScrollPane scrollPane = new ScrollPane(listaContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(300);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // --- SEÇÃO 2: CONTADOR DE FROTA ---
        VBox painelFrota = new VBox(5);
        painelFrota.setAlignment(Pos.CENTER);
        painelFrota.setPadding(new Insets(20));
        painelFrota.setStyle("-fx-background-color: " + HEX_CINZA_FUNDO + "; -fx-background-radius: 8;");

        Label lblFrotaTitulo = new Label("Ambulâncias Disponíveis");
        lblFrotaTitulo.setFont(FONTE_CORPO);
        lblFrotaTitulo.setTextFill(COR_TEXTO_BRANCO);

        Label lblFrotaNumero = new Label("5");
        lblFrotaNumero.setFont(FONTE_TITULO);
        lblFrotaNumero.setTextFill(COR_TEXTO_BRANCO);

        Label lblFrotaTotal = new Label("de 12 viaturas totais");
        lblFrotaTotal.setFont(FONTE_PEQUENA);
        lblFrotaTotal.setTextFill(COR_TEXTO_BRANCO);

        painelFrota.getChildren().addAll(lblFrotaTitulo, lblFrotaNumero, lblFrotaTotal);

        rightPanel.getChildren().addAll(lblResumo, lblPendentes, scrollPane, painelFrota);


        // =============================================================================================
        // MONTAGEM FINAL
        // =============================================================================================
        rootLayout = new HBox(); // Inicializamos a variável global
        rootLayout.getChildren().addAll(sidebar, centerMap, rightPanel);

        HBox.setHgrow(sidebar, Priority.NEVER);
        HBox.setHgrow(rightPanel, Priority.NEVER);
        HBox.setHgrow(centerMap, Priority.ALWAYS);

        // Marca o botão Dashboard como ativo inicialmente
        atualizarEstiloBotao(btnDashboard);

        return new Scene(rootLayout, 1200, 700);
    }

    // =============================================================================================
    // MÉTODOS de Troca de Tela e Estilo)
    // =============================================================================================

    /**
     * Remove o que está no centro e coloca o novo conteúdo
     */
    private void setConteudoCentral(Node novoConteudo) {
        // O índice 1 é sempre o centro (0=Esquerda, 1=Centro, 2=Direita)
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(1, novoConteudo);

        // Garante que o novo conteúdo cresça
        HBox.setHgrow(novoConteudo, Priority.ALWAYS);

        // Se for um painel, remove restrições de tamanho para preencher tudo
        if (novoConteudo instanceof Region) {
            ((Region) novoConteudo).setMaxWidth(Double.MAX_VALUE);
            ((Region) novoConteudo).setMaxHeight(Double.MAX_VALUE);
        }
    }

    /**
     * Gerencia visualmente qual botão está selecionado (Vermelho)
     */
    private void atualizarEstiloBotao(Button btnAtivo) {
        // Estilos padrão
        String estiloNormal = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String estiloAtivo  = "-fx-background-color: " + HEX_SIDEBAR_HOVER + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String estiloHover  = "-fx-background-color: #334155; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";

        // 1. Reseta TODOS os botões da lista
        for (Button b : botoesMenu) {
            b.setStyle(estiloNormal);
            // Volta a cor do texto para cinza claro
            alterarCorTextoBotao(b, Color.web("#E2E8F0"));

            // Recria o comportamento de hover (porque ao setar style, as vezes perde o listener)
            b.setOnMouseEntered(e -> {
                // Só aplica hover se NÃO for o botão ativo atual
                if (b != btnAtivo) {
                    b.setStyle(estiloHover);
                    alterarCorTextoBotao(b, Color.WHITE);
                }
            });
            b.setOnMouseExited(e -> {
                if (b != btnAtivo) {
                    b.setStyle(estiloNormal);
                    alterarCorTextoBotao(b, Color.web("#E2E8F0"));
                }
            });
        }

        // 2. Aplica estilo ATIVO no botão clicado
        btnAtivo.setStyle(estiloAtivo);
        alterarCorTextoBotao(btnAtivo, Color.WHITE); // Texto fica branco puro

        // Remove listeners do ativo para ele não piscar
        btnAtivo.setOnMouseEntered(null);
        btnAtivo.setOnMouseExited(null);
    }

    /**
     * Helper para mudar a cor do texto DENTRO do TextFlow do botão
     */
    private void alterarCorTextoBotao(Button btn, Color cor) {
        if (btn.getGraphic() instanceof TextFlow) {
            TextFlow flow = (TextFlow) btn.getGraphic();
            for (Node n : flow.getChildren()) {
                if (n instanceof Text) {
                    ((Text) n).setFill(cor);
                }
            }
        }
    }

    /**
     * Cria um VBox simples apenas para ilustrar a troca de telas (Placeholder)
     */
    private VBox criarPlaceholderFormulario(String titulo) {
        VBox form = new VBox(20);
        form.setPadding(new Insets(40));
        form.setAlignment(Pos.TOP_LEFT);
        form.setStyle("-fx-background-color: #F1F5F9;"); // Fundo cinza claro

        Label lbl = new Label(titulo);
        lbl.setFont(FONTE_TITULO);
        lbl.setTextFill(COR_AZUL_NOTURNO);

        Label lblDesc = new Label("O formulário de " + titulo + " será implementado aqui.");
        lblDesc.setFont(FONTE_CORPO);

        form.getChildren().addAll(lbl, lblDesc);
        return form;
    }

    // =============================================================================================
    // MÉTODOS AUXILIARES (Botao com emoji de uma fonte, e escrita de outra)
    // =============================================================================================

    /**
     * Cria um botão estilizado
     */
    private Button criarBotaoMenu(String texto, String iconeEmoji) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);

        // Sequência: v cima, v1 direita, v2 baixo, v3 esquerda
        btn.setPadding(new Insets(12, 15, 12, 15));

        // Altura controlada (Evita que fique gigante)
        btn.setMinHeight(45);
        btn.setMaxHeight(45);

        // --- 1. EMOJI ---
        javafx.scene.text.Text txtEmoji = new javafx.scene.text.Text(iconeEmoji);
        // Reduz o emoji para 16px
        txtEmoji.setFont(Font.font("Segoe UI Emoji", 16));
        txtEmoji.setFill(Color.web("#E2E8F0"));

        // --- 2. TEXTO POPPINS ---
        javafx.scene.text.Text txtLabel = new javafx.scene.text.Text("  " + texto); // Espaço aqui no texto
        txtLabel.setFont(FONTE_BOTAO2);
        txtLabel.setFill(Color.web("#E2E8F0"));

        // --- 3. MISTA DOS DOIS ---
        javafx.scene.text.TextFlow flow = new javafx.scene.text.TextFlow(txtEmoji, txtLabel);
        // Alinha o testo dos botoes
        flow.setTextAlignment(TextAlignment.LEFT);

        btn.setGraphic(flow);

        // --- REGISTRO DO BOTÃO NA LISTA (IMPORTANTE) ---
        // Adicionamos o botão na lista para podermos controlar a cor depois
        botoesMenu.add(btn);

        return btn;
    }

    /**
     * Cria um quadrado visual para representar uma ocorrência na lista
     */
    private HBox criarCardOcorrencia(String titulo, String subtitulo, String corStatus) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 6; -fx-background-radius: 6;");
        card.setAlignment(Pos.CENTER_LEFT);

        Circle statusDot = new Circle(5, Color.web(corStatus));

        VBox textos = new VBox(2);
        Label lblTit = new Label(titulo);
        // Usando fonte do corpo para o card ficar mais limpo
        lblTit.setFont(FONTE_CORPO);
        lblTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label lblSub = new Label(subtitulo);
        lblSub.setFont(FONTE_PEQUENA);
        lblSub.setTextFill(Color.web(corStatus));

        textos.getChildren().addAll(lblTit, lblSub);

        card.getChildren().addAll(statusDot, textos);
        return card;
    }
}