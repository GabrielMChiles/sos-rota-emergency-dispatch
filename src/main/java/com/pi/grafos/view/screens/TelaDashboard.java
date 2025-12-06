package com.pi.grafos.view.screens;

import com.pi.grafos.dto.SugestaoDespachoDTO;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.enums.OcorrenciaGravidade;
import com.pi.grafos.repository.EquipeRepository;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.repository.TipoOcorrenciaRepository;
import com.pi.grafos.service.*;
import com.pi.grafos.view.components.Alerta;
import com.pi.grafos.view.components.ModalJustificativa;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent; // <--- Importante
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.pi.grafos.view.styles.AppStyles.*;

@Component
public class TelaDashboard {

    // --- DEPENDÊNCIAS ---
    private final FuncionarioService funcionarioService;
    private final AmbulanciaService ambulanciaService;
    private final EquipeService equipeService;
    private final DespachoService despachoService;
    private final OcorrenciaService ocorrenciaService;

    private final LocalizacaoRepository localizacaoRepo;
    private final TipoOcorrenciaRepository tipoOcorrenciaRepo;
    private final EquipeRepository equipeRepo;

    // --- VISUAIS ---
    private static final double LARGURA_SIDEBAR = 260;
    private static final double LARGURA_RESUMO = 340;

    private HBox rootLayout;
    private Region centerMap;
    private VBox rightPanelContent;
    private final List<Button> botoesMenu = new ArrayList<>();

    // Construtor com todas as injeções
    public TelaDashboard(FuncionarioService fs, AmbulanciaService as, EquipeService es,
                         DespachoService ds, OcorrenciaService os,
                         LocalizacaoRepository locRepo, TipoOcorrenciaRepository tipoRepo, EquipeRepository eqRepo) {
        this.funcionarioService = fs;
        this.ambulanciaService = as;
        this.equipeService = es;
        this.despachoService = ds;
        this.ocorrenciaService = os;
        this.localizacaoRepo = locRepo;
        this.tipoOcorrenciaRepo = tipoRepo;
        this.equipeRepo = eqRepo;
    }

    // --- AQUI A MUDANÇA CRÍTICA: Retorna Parent ---
    public Parent criarConteudo(Stage stage) {

        // 1. SIDEBAR
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
        } catch (Exception e) { }

        Label lblTituloPainel = new Label("PAINEL");
        lblTituloPainel.setFont(FONTE_TITULO);
        lblTituloPainel.setTextFill(COR_TEXTO_BRANCO);

        // Botões
        Button btnDashboard = criarBotaoMenu("Dashboard", "🏠");
        btnDashboard.setOnAction(e -> {
            atualizarEstiloBotao(btnDashboard);
            setConteudoCentral(centerMap);
            atualizarPainelDireito();
        });

        Button btnNovaOcorrencia = criarBotaoMenu("Nova Ocorrência", "➕");
        btnNovaOcorrencia.setOnAction(e -> {
            atualizarEstiloBotao(btnNovaOcorrencia);
            setConteudoCentral(new FormularioOcorrenciaView(despachoService, localizacaoRepo, tipoOcorrenciaRepo).criarView());
        });

        Button btnFrota = criarBotaoMenu("Ambulâncias", "🚑");
        btnFrota.setOnAction(e -> {
            atualizarEstiloBotao(btnFrota);
            setConteudoCentral(new GestaoAmbulanciasView(ambulanciaService, localizacaoRepo).criarView());
        });

        Button btnEquipe = criarBotaoMenu("Equipes", "👨‍⚕️");
        btnEquipe.setOnAction(e -> {
            atualizarEstiloBotao(btnEquipe);
            setConteudoCentral(new GestaoEquipesView(equipeService, ambulanciaService).criarView());
        });

        Button btnColaborador = criarBotaoMenu("Colaboradores", "⚕");
        btnColaborador.setOnAction(e -> {
            atualizarEstiloBotao(btnColaborador);
            setConteudoCentral(new GestaoFuncionariosView(funcionarioService).criarView());
        });

        Button btnRelatorio = criarBotaoMenu("Relatórios", "📊");
        btnRelatorio.setOnAction(e -> {
            atualizarEstiloBotao(btnRelatorio);
            setConteudoCentral(criarPlaceholder("Relatórios e Métricas"));
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnSair = criarBotaoMenu("Sair", "🚪");
        btnSair.setOnAction(e -> stage.close());

        sidebar.getChildren().addAll(logoView, lblTituloPainel, btnDashboard, btnNovaOcorrencia, btnFrota, btnEquipe, btnColaborador, btnRelatorio, spacer, btnSair);

        // 2. CENTRO (MAPA)
        centerMap = new Region();
        try {
            Image imgMap = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            BackgroundSize bgSize = new BackgroundSize(1.0, 1.0, true, true, false, true);
            centerMap.setBackground(new Background(new BackgroundImage(imgMap, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize)));
        } catch (Exception e) { centerMap.setStyle("-fx-background-color: #CBD5E1;"); }

        // 3. DIREITA (RESUMO)
        VBox rightPanel = criarPainelResumo();

        // MONTAGEM
        rootLayout = new HBox();
        rootLayout.getChildren().addAll(sidebar, centerMap, rightPanel);

        HBox.setHgrow(sidebar, Priority.NEVER);
        HBox.setHgrow(rightPanel, Priority.NEVER);
        HBox.setHgrow(centerMap, Priority.ALWAYS);

        atualizarEstiloBotao(btnDashboard);

        // MUDANÇA: Retorna o Layout Raiz (HBox), e não uma Scene
        return rootLayout;
    }

    // --- MÉTODOS AUXILIARES (MANTIDOS IDÊNTICOS) ---

    private VBox criarPainelResumo() {
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setPrefWidth(LARGURA_RESUMO);
        rightPanel.setMinWidth(LARGURA_RESUMO);
        rightPanel.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, -5, 0);");

        Label lblTitulo = new Label("Resumo Operacional");
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        rightPanelContent = new VBox(15);
        rightPanelContent.setPadding(new Insets(0, 20, 20, 20));
        atualizarListasOcorrencias();

        ScrollPane scroll = new ScrollPane(rightPanelContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: white; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Painel Frota
        VBox painelFrota = new VBox(5);
        painelFrota.setAlignment(Pos.CENTER);
        painelFrota.setPadding(new Insets(20));
        painelFrota.setStyle("-fx-background-color: " + HEX_CINZA_FUNDO + "; -fx-background-radius: 8;");

        Label lblFrota = new Label("Ambulâncias Disponíveis");
        lblFrota.setFont(FONTE_CORPO);
        lblFrota.setTextFill(COR_TEXTO_BRANCO);

        Label lblNum = new Label("5");
        lblNum.setFont(Font.font("Poppins", FontWeight.BOLD, 40));
        lblNum.setTextFill(COR_TEXTO_BRANCO);

        painelFrota.getChildren().addAll(lblFrota, lblNum);

        rightPanel.getChildren().addAll(lblTitulo, scroll, painelFrota);
        return rightPanel;
    }

    public void atualizarPainelDireito() {
        atualizarListasOcorrencias();
    }

    private void atualizarListasOcorrencias() {
        if (rightPanelContent == null) return;
        rightPanelContent.getChildren().clear();

        try {
            // --- GRUPO 1: PENDENTES ---
            List<Ocorrencia> pendentes = ocorrenciaService.listarPendentes();
            List<Node> cardsPendentes = new ArrayList<>();
            for (Ocorrencia oc : pendentes) {
                cardsPendentes.add(criarCardPendente(oc));
            }

            // Cria a seção colapsável (Vermelha) - Inicia ABERTA
            VBox secaoPendentes = criarSecaoColapsavel("Aguardando Despacho", HEX_VERMELHO, cardsPendentes, true);
            rightPanelContent.getChildren().add(secaoPendentes);

            // Espaço
            Region spacer = new Region();
            spacer.setMinHeight(15);
            rightPanelContent.getChildren().add(spacer);

            // --- GRUPO 2: EM ATENDIMENTO ---
            List<Ocorrencia> emAndamento = ocorrenciaService.listarEmAtendimento();
            List<Node> cardsAtendimento = new ArrayList<>();
            for (Ocorrencia oc : emAndamento) {
                cardsAtendimento.add(criarCardEmAtendimento(oc));
            }

            // Cria a seção colapsável (Azul) - Inicia ABERTA (ou FECHADA se preferir economizar espaço)
            VBox secaoAtendimento = criarSecaoColapsavel("Em Atendimento", "#3B82F6", cardsAtendimento, true);
            rightPanelContent.getChildren().add(secaoAtendimento);

        } catch (Exception e) {
            rightPanelContent.getChildren().add(new Label("Erro ao carregar dados."));
            e.printStackTrace();
        }
    }

    private HBox criarCardPendente(Ocorrencia oc) {
        HBox card = new HBox(10); card.setPadding(new Insets(10)); card.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 6; -fx-border-color: #FECACA; -fx-border-radius: 6; -fx-cursor: hand;");
        VBox info = new VBox(2);
        String tipo = oc.getTipoOcorrencia() != null ? oc.getTipoOcorrencia().getNomeTipoOcorrencia() : "Ocorrência";
        String bairro = oc.getLocal() != null ? oc.getLocal().getNome() : "?";
        Label lblTitulo = new Label(tipo); lblTitulo.setFont(FONTE_CORPO); lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #991B1B;");
        Label lblLocal = new Label(bairro + " • " + oc.getGravidade()); lblLocal.setFont(FONTE_PEQUENA); lblLocal.setTextFill(Color.web("#7F1D1D"));
        info.getChildren().addAll(lblTitulo, lblLocal);
        HBox.setHgrow(info, Priority.ALWAYS);
        card.setOnMouseClicked(e -> {
            Stage stage = (Stage) card.getScene().getWindow();
            try {
                // Correção: Passa Enum OcorrenciaGravidade
                List<SugestaoDespachoDTO> sugestoes = despachoService.buscarAmbulanciasAptas(bairro, oc.getGravidade());
                new ModalSelecaoAmbulancia().exibir(stage, bairro, oc.getGravidade().toString(), oc, sugestoes, despachoService);
            } catch (Exception ex) { new Alerta().mostrar("Erro", ex.getMessage(), Alerta.Tipo.ERRO); }
        });
        card.getChildren().add(info);
        return card;
    }

    private HBox criarCardEmAtendimento(Ocorrencia oc) {
        HBox card = new HBox(10); card.setPadding(new Insets(10)); card.setStyle("-fx-background-color: #EFF6FF; -fx-background-radius: 6; -fx-border-color: #BFDBFE; -fx-border-radius: 6;");
        VBox info = new VBox(2);
        String tipo = oc.getTipoOcorrencia() != null ? oc.getTipoOcorrencia().getNomeTipoOcorrencia() : "Ocorrência";
        Label lbl = new Label(tipo); lbl.setFont(FONTE_PEQUENA); lbl.setStyle("-fx-font-weight: bold;");
        info.getChildren().add(lbl);
        HBox.setHgrow(info, Priority.ALWAYS);
        Button btnConcluir = new Button("✔"); btnConcluir.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-cursor: hand;");
        btnConcluir.setOnAction(e -> {
            try { ocorrenciaService.concluirOcorrencia(oc.getIdOcorrencia()); atualizarPainelDireito(); } catch (Exception ex) { new Alerta().mostrar("Erro", ex.getMessage(), Alerta.Tipo.ERRO); }
        });
        Button btnCancelar = new Button("✖"); btnCancelar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> {
            String just = new ModalJustificativa().exibir((Stage) card.getScene().getWindow());
            if (just != null && !just.trim().isEmpty()) {
                try { ocorrenciaService.cancelarOcorrencia(oc.getIdOcorrencia(), just); atualizarPainelDireito(); } catch (Exception ex) { new Alerta().mostrar("Erro", ex.getMessage(), Alerta.Tipo.ERRO); }
            }
        });
        card.getChildren().addAll(info, btnConcluir, btnCancelar);
        return card;
    }

    private void setConteudoCentral(Node novoConteudo) {
        rootLayout.getChildren().remove(1);
        rootLayout.getChildren().add(1, novoConteudo);
        HBox.setHgrow(novoConteudo, Priority.ALWAYS);
        if (novoConteudo instanceof Region) { ((Region) novoConteudo).setMaxWidth(Double.MAX_VALUE); ((Region) novoConteudo).setMaxHeight(Double.MAX_VALUE); }
        atualizarPainelDireito();
    }



    private Button criarBotaoMenu(String texto, String emoji) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setMinHeight(45);
        btn.setMaxHeight(45);

        // Emoji
        Text txtEmoji = new Text(emoji);
        txtEmoji.setFont(Font.font("Segoe UI Emoji", 16));
        txtEmoji.setFill(Color.web("#E2E8F0"));

        // Texto do Label
        Text txtLabel = new Text("  " + texto);

        // AQUI: Já aplicamos o estilo CSS logo na criação para garantir consistência desde o frame 0
        txtLabel.setStyle(converterFontParaCSS(FONTE_BOTAO2));
        txtLabel.setFill(Color.web("#E2E8F0"));

        // Tag para identificar depois
        txtLabel.setUserData("LABEL_TEXTO");

        TextFlow flow = new TextFlow(txtEmoji, txtLabel);
        flow.setTextAlignment(TextAlignment.LEFT);
        btn.setGraphic(flow);
        botoesMenu.add(btn);
        return btn;
    }

    private void atualizarEstiloBotao(Button btnAtivo) {
        // Strings de estilo do container (Botão)
        String normal = "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;";
        String ativo = "-fx-background-color: " + HEX_SIDEBAR_HOVER + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);";
        String hover = "-fx-background-color: #334155; -fx-background-radius: 8; -fx-cursor: hand;";

        for (Button b : botoesMenu) {
            // Limpa listeners antigos para não acumular lixo na memória
            b.setOnMouseEntered(null);
            b.setOnMouseExited(null);

            if (b == btnAtivo) {
                b.setStyle(ativo);
                atualizarTextoInterno(b, Color.WHITE);
            } else {
                b.setStyle(normal);
                atualizarTextoInterno(b, Color.web("#E2E8F0"));

                b.setOnMouseEntered(e -> {
                    b.setStyle(hover);
                    atualizarTextoInterno(b, Color.WHITE);
                });
                b.setOnMouseExited(e -> {
                    b.setStyle(normal);
                    atualizarTextoInterno(b, Color.web("#E2E8F0"));
                });
            }
        }
    }

    private void atualizarTextoInterno(Button btn, Color cor) {
        if (btn.getGraphic() instanceof TextFlow) {
            TextFlow flow = (TextFlow) btn.getGraphic();
            for (Node n : flow.getChildren()) {
                if (n instanceof Text) {
                    Text t = (Text) n;

                    // 1. Aplica a cor via Java (isso funciona bem para cores)
                    t.setFill(cor);

                    // 2. BLINDAGEM DA FONTE:
                    // Se for o texto do label, forçamos o CSS direto no nó.
                    // O CSS do pai (Botão) não consegue tocar nisso.
                    if ("LABEL_TEXTO".equals(t.getUserData())) {
                        // Pega a string CSS da sua fonte definida no AppStyles
                        String cssFont = converterFontParaCSS(FONTE_BOTAO2);

                        // Aplica no setStyle.
                        // Nota: setStyle substitui estilos anteriores, então a fonte "gruda".
                        t.setStyle(cssFont);
                    }
                }
            }
        }
    }

    private void alterarCorTextoBotao(Button btn, Color cor) {
        if (btn.getGraphic() instanceof TextFlow) { ((TextFlow) btn.getGraphic()).getChildren().stream().filter(n -> n instanceof Text).forEach(n -> ((Text) n).setFill(cor)); }
    }

    private VBox criarPlaceholder(String titulo) {
        VBox v = new VBox(20); v.setPadding(new Insets(40)); v.setStyle("-fx-background-color: #F1F5F9;");
        Label l = new Label(titulo); l.setFont(FONTE_TITULO); l.setTextFill(COR_AZUL_NOTURNO);
        v.getChildren().add(l); return v;
    }

    /**
     * Cria uma seção bonita que abre e fecha (Accordion Customizado)
     */
    private VBox criarSecaoColapsavel(String titulo, String corHex, List<Node> cards, boolean iniciarAberto) {
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-border-width: 1;");

        // --- 1. CABEÇALHO (Clicável) ---
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 15, 12, 15));
        header.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        // Ícone de Seta (Usando texto para ser leve)
        Text seta = new Text(iniciarAberto ? "▼" : "▶");
        seta.setFont(Font.font("System", 12));
        seta.setFill(Color.web("#94A3B8")); // Cinza suave

        // Título + Contador
        Label lblTitulo = new Label(titulo + " (" + cards.size() + ")");
        lblTitulo.setFont(FONTE_PEQUENA);
        lblTitulo.setStyle("-fx-font-weight: bold;");
        lblTitulo.setTextFill(Color.web(corHex));

        header.getChildren().addAll(seta, lblTitulo);

        // --- 2. CONTEÚDO (A Lista) ---
        VBox content = new VBox(8);
        content.setPadding(new Insets(0, 10, 10, 10)); // Padding só nas laterais e embaixo
        content.getChildren().addAll(cards);

        // Estado Inicial
        content.setVisible(iniciarAberto);
        content.setManaged(iniciarAberto); // Se false, não ocupa espaço na tela

        // --- 3. AÇÃO DE CLIQUE ---
        header.setOnMouseClicked(e -> {
            boolean estaAberto = content.isVisible();

            // Inverte o estado
            content.setVisible(!estaAberto);
            content.setManaged(!estaAberto); // Faz a mágica de "sumir" o espaço

            // Atualiza a seta
            seta.setText(!estaAberto ? "▼" : "▶");
        });

        container.getChildren().addAll(header, content);
        return container;
    }

    // Método "Nuclear" para converter Font Java em CSS String
    private String converterFontParaCSS(Font fonte) {
        return String.format("-fx-font-family: '%s'; -fx-font-size: %s px; -fx-font-weight: %s;",
                fonte.getFamily(),
                fonte.getSize(),
                (fonte.getStyle().toLowerCase().contains("bold") ? "bold" : "normal")
        );
    }
}