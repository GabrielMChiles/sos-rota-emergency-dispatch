package com.pi.grafos.view.screens;

import java.util.ArrayList;
import java.util.List;

import com.pi.grafos.dto.SugestaoDespachoDTO; // Importe o DTO
import com.pi.grafos.service.AmbulanciaService;
import com.pi.grafos.service.DespachoService; // Importe o Cérebro
import javafx.scene.control.Alert;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import com.pi.grafos.repository.AmbulanciaRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.service.FuncionarioService;

// Imports Estáticos de Estilo (Mantidos)
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
import javafx.scene.Parent;
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

    // --- DEPENDÊNCIAS (INJEÇÃO VIA CONSTRUTOR - BEST PRACTICE) ---
    private final FuncionarioService funcionarioService;
    private final AmbulanciaService ambulanciaService;
    private final LocalizacaoRepository localizacaoRepository;
    private final DespachoService despachoService;
    private final ObjectProvider<GestaoEquipesView> gestaoEquipesProvider;

    // Construtor único recebendo TUDO do Spring
    public TelaDashboard(FuncionarioService funcionarioService,
                         AmbulanciaService ambulanciaService,
                         LocalizacaoRepository localizacaoRepository,
                         DespachoService despachoService,
                         ObjectProvider<GestaoEquipesView> gestaoEquipesProvider) {
        this.funcionarioService = funcionarioService;
        this.ambulanciaService = ambulanciaService;
        this.localizacaoRepository = localizacaoRepository;
        this.despachoService = despachoService;
        this.gestaoEquipesProvider = gestaoEquipesProvider;
    }

    // --- CONFIGURAÇÕES VISUAIS ---
    private static final double LARGURA_SIDEBAR = 240;
    private static final double LARGURA_RESUMO = 320;

    // --- ESTADO DA TELA ---
    private HBox rootLayout;
    private Region centerMap;
    private final List<Button> botoesMenu = new ArrayList<>();

    public Parent criarConteudo(Stage stage) {

        // 1. MENU LATERAL
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setPrefWidth(LARGURA_SIDEBAR);
        sidebar.setMinWidth(LARGURA_SIDEBAR);
        sidebar.setStyle("-fx-background-color: " + HEX_SIDEBAR_BG + ";");
        sidebar.setAlignment(Pos.TOP_CENTER);

        // Logo
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo2.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(120);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { /* Ignora */ }

        Label lblTituloPainel = new Label("PAINEL");
        lblTituloPainel.setFont(FONTE_TITULO);
        lblTituloPainel.setTextFill(COR_TEXTO_BRANCO);

        // --- BOTÕES ---
        Button btnDashboard = criarBotaoMenu("Dashboard", "🏠");
        btnDashboard.setOnAction(e -> {
            atualizarEstiloBotao(btnDashboard);
            setConteudoCentral(centerMap);
        });

        Button btnNovaOcorrencia = criarBotaoMenu("Nova Ocorrência", "➕");
        btnNovaOcorrencia.setOnAction(e -> {
            atualizarEstiloBotao(btnNovaOcorrencia);
            // CORREÇÃO CRÍTICA: Passando o serviço para a View funcionar
            setConteudoCentral(new FormularioOcorrenciaView(despachoService, localizacaoRepository).criarView());
        });

        Button btnFrota = criarBotaoMenu("Ambulâncias", "🚑");
        btnFrota.setOnAction(e -> {
            atualizarEstiloBotao(btnFrota);
            setConteudoCentral(new GestaoAmbulanciasView(ambulanciaService, localizacaoRepository).criarView());
        });

        Button btnEquipe = criarBotaoMenu("Equipe", "👨‍⚕️");
        btnEquipe.setOnAction(e -> {
            atualizarEstiloBotao(btnEquipe);
            setConteudoCentral(gestaoEquipesProvider.getObject().criarView());
        });

        Button btnColaborador = criarBotaoMenu("Colaboradores", "⚕");
        btnColaborador.setOnAction(e -> {
            atualizarEstiloBotao(btnColaborador);
            setConteudoCentral(new GestaoFuncionariosView(funcionarioService).criarView());
        });

        Button btnRelatorio = criarBotaoMenu("Relatórios", "📊");
        btnRelatorio.setOnAction(e -> {
            atualizarEstiloBotao(btnRelatorio);
            setConteudoCentral(criarPlaceholderFormulario("Relatório"));
        });

        Region spacerMenu = new Region();
        VBox.setVgrow(spacerMenu, Priority.ALWAYS);

        Button btnSair = criarBotaoMenu("Sair do Sistema", "🚪");
        btnSair.setOnAction(e -> stage.close());

        sidebar.getChildren().addAll(logoView, lblTituloPainel, btnDashboard, btnNovaOcorrencia, btnFrota, btnEquipe, btnColaborador, btnRelatorio, spacerMenu, btnSair);

        // 2. MAPA CENTRAL
        centerMap = new Region();
        try {
            Image imgMap = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            BackgroundSize bgSize = new BackgroundSize(1.0, 1.0, true, true, false, true);
            centerMap.setBackground(new Background(new BackgroundImage(imgMap, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize)));
        } catch (Exception e) {
            centerMap.setStyle("-fx-background-color: #CBD5E1;");
        }

        // 3. RESUMO DIREITO
        VBox rightPanel = new VBox(25);
        rightPanel.setPadding(new Insets(30));
        rightPanel.setPrefWidth(LARGURA_RESUMO);
        rightPanel.setMinWidth(LARGURA_RESUMO);
        rightPanel.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, -5, 0);");

        Label lblResumo = new Label("Resumo Operacional");
        lblResumo.setFont(FONTE_SUBTITULO);
        lblResumo.setTextFill(COR_AZUL_NOTURNO);

        Label lblPendentes = new Label("Ocorrências Pendentes");
        lblPendentes.setFont(FONTE_CORPO);
        lblPendentes.setTextFill(COR_TEXTO_CLARO);

        VBox listaContainer = new VBox(10);
        // Cards estáticos para demo, mas com Lógica Real de clique
        listaContainer.getChildren().add(criarCardOcorrencia("Acidente Centro", "Alta Prioridade - Requer UTI", HEX_VERMELHO, "Centro", "ALTA"));
        listaContainer.getChildren().add(criarCardOcorrencia("Mal Súbito", "Média Prioridade - Jd. América", "#F59E0B", "Jardim América", "MÉDIA"));
        listaContainer.getChildren().add(criarCardOcorrencia("Transporte Eletivo", "Baixa Prioridade - Vila Nova", "#10B981", "Vila Nova", "BAIXA"));

        ScrollPane scrollPane = new ScrollPane(listaContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPrefHeight(300);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Painel Frota (Estático por enquanto)
        VBox painelFrota = new VBox(5);
        painelFrota.setAlignment(Pos.CENTER);
        painelFrota.setPadding(new Insets(20));
        painelFrota.setStyle("-fx-background-color: " + HEX_CINZA_FUNDO + "; -fx-background-radius: 8;");
        painelFrota.getChildren().addAll(
                criarLabelEstilizado("Ambulâncias Disponíveis", FONTE_CORPO, COR_TEXTO_BRANCO),
                criarLabelEstilizado("5", FONTE_TITULO, COR_TEXTO_BRANCO),
                criarLabelEstilizado("de 12 viaturas totais", FONTE_PEQUENA, COR_TEXTO_BRANCO)
        );

        rightPanel.getChildren().addAll(lblResumo, lblPendentes, scrollPane, painelFrota);

        // Montagem
        rootLayout = new HBox();
        rootLayout.getChildren().addAll(sidebar, centerMap, rightPanel);
        HBox.setHgrow(centerMap, Priority.ALWAYS);

        atualizarEstiloBotao(btnDashboard);
        return rootLayout;
    }

    // --- MÉTODOS AUXILIARES ---

    private Label criarLabelEstilizado(String texto, Font fonte, Color cor) {
        Label l = new Label(texto);
        l.setFont(fonte);
        l.setTextFill(cor);
        return l;
    }

    private void setConteudoCentral(Node novoConteudo) {
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(1, novoConteudo);
        HBox.setHgrow(novoConteudo, Priority.ALWAYS);
        if (novoConteudo instanceof Region) {
            ((Region) novoConteudo).setMaxWidth(Double.MAX_VALUE);
            ((Region) novoConteudo).setMaxHeight(Double.MAX_VALUE);
        }
    }

    private void atualizarEstiloBotao(Button btnAtivo) {
        String estiloNormal = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String estiloAtivo  = "-fx-background-color: " + HEX_SIDEBAR_HOVER + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";
        String estiloHover  = "-fx-background-color: #334155; -fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER_LEFT;";

        for (Button b : botoesMenu) {
            b.setStyle(estiloNormal);
            alterarCorTextoBotao(b, Color.web("#E2E8F0"));
            b.setOnMouseEntered(e -> { if (b != btnAtivo) { b.setStyle(estiloHover); alterarCorTextoBotao(b, Color.WHITE); }});
            b.setOnMouseExited(e -> { if (b != btnAtivo) { b.setStyle(estiloNormal); alterarCorTextoBotao(b, Color.web("#E2E8F0")); }});
        }
        btnAtivo.setStyle(estiloAtivo);
        alterarCorTextoBotao(btnAtivo, Color.WHITE);
        btnAtivo.setOnMouseEntered(null);
        btnAtivo.setOnMouseExited(null);
    }

    private void alterarCorTextoBotao(Button btn, Color cor) {
        if (btn.getGraphic() instanceof TextFlow) {
            ((TextFlow) btn.getGraphic()).getChildren().stream()
                    .filter(n -> n instanceof Text)
                    .forEach(n -> ((Text) n).setFill(cor));
        }
    }

    private VBox criarPlaceholderFormulario(String titulo) {
        VBox form = new VBox(20);
        form.setPadding(new Insets(40));
        form.setAlignment(Pos.TOP_LEFT);
        form.setStyle("-fx-background-color: #F1F5F9;");
        Label lbl = new Label(titulo);
        lbl.setFont(FONTE_TITULO);
        lbl.setTextFill(COR_AZUL_NOTURNO);
        form.getChildren().addAll(lbl, new Label("Módulo de " + titulo + " em construção."));
        return form;
    }

    private Button criarBotaoMenu(String texto, String iconeEmoji) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setMinHeight(45);
        btn.setMaxHeight(45);

        Text txtEmoji = new Text(iconeEmoji);
        txtEmoji.setFont(Font.font("Segoe UI Emoji", 16));
        txtEmoji.setFill(Color.web("#E2E8F0"));

        Text txtLabel = new Text("  " + texto);
        txtLabel.setFont(FONTE_BOTAO2);
        txtLabel.setFill(Color.web("#E2E8F0"));

        TextFlow flow = new TextFlow(txtEmoji, txtLabel);
        flow.setTextAlignment(TextAlignment.LEFT);
        btn.setGraphic(flow);
        botoesMenu.add(btn);
        return btn;
    }

    /**
     * CORREÇÃO CRÍTICA: Agora o Card calcula a rota ANTES de abrir o Modal
     */
    private HBox criarCardOcorrencia(String titulo, String subtitulo, String corStatus, String bairro, String gravidade) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        String estiloNormal = "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";
        String estiloHover = "-fx-background-color: #F1F5F9; -fx-border-color: " + corStatus + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";
        card.setStyle(estiloNormal);

        card.setOnMouseEntered(e -> card.setStyle(estiloHover));
        card.setOnMouseExited(e -> card.setStyle(estiloNormal));

        // --- LÓGICA DE CLIQUE COM CÁLCULO REAL ---
        card.setOnMouseClicked(e -> {
            Stage stageAtual = (Stage) card.getScene().getWindow();
            try {
                System.out.println("Calculando rota rápida para: " + bairro);

                // 1. CHAMA O CÉREBRO (DIJKSTRA)
                List<SugestaoDespachoDTO> sugestoes = despachoService.buscarAmbulanciasAptas(bairro, gravidade);

                // 2. ABRE O MODAL COM OS DADOS REAIS
                new ModalSelecaoAmbulancia().exibir(stageAtual, bairro, gravidade, sugestoes);

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Não foi possível calcular a rota");
                erro.setContentText(ex.getMessage());
                erro.showAndWait();
            }
        });

        Circle statusDot = new Circle(5, Color.web(corStatus));
        VBox textos = new VBox(4);
        Label lblTit = new Label(titulo);
        lblTit.setFont(FONTE_CORPO);
        lblTit.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label lblSub = new Label(subtitulo);
        lblSub.setFont(FONTE_PEQUENA);
        lblSub.setTextFill(Color.web(corStatus));
        textos.getChildren().addAll(lblTit, lblSub);
        card.getChildren().addAll(statusDot, textos);
        HBox.setHgrow(textos, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }
}