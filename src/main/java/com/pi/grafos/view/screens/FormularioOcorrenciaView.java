package com.pi.grafos.view.screens;

import com.pi.grafos.dto.SugestaoDespachoDTO;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.enums.TipoLocalizacao;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.service.DespachoService;
import com.pi.grafos.view.components.Alerta; // Seu alerta bonito

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.pi.grafos.view.styles.AppStyles.*;

public class FormularioOcorrenciaView {

    // Dependências
    private final DespachoService despachoService;
    private final LocalizacaoRepository localizacaoRepo; // Nova dependência para buscar bairros

    // Componentes de Tela
    private ComboBox<Localizacao> comboBairro; // Agora usa Objeto real
    private ComboBox<String> comboTipo;
    private ComboBox<String> comboGravidade;
    private TextArea txtObservacao;
    private Label lblSlaInfo;

    public FormularioOcorrenciaView(DespachoService despachoService, LocalizacaoRepository localizacaoRepo) {
        this.despachoService = despachoService;
        this.localizacaoRepo = localizacaoRepo;
    }

    public VBox criarView() {
        // Layout Base
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // Card Branco
        VBox formCard = new VBox(25);
        formCard.setMaxWidth(900);
        formCard.setPadding(new Insets(40));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        // Cabeçalho
        Label lblTitulo = new Label("Nova Ocorrência");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblDesc = new Label("Preencha os dados para cálculo automático de rota e triagem.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);

        // --- CAMPOS ---

        // 1. Bairro (Do Banco) e Endereço
        HBox row1 = new HBox(20);

        comboBairro = new ComboBox<>();
        comboBairro.setMaxWidth(Double.MAX_VALUE);

        // Busca apenas BAIRROS (locais de acidente) e ignora BASES
        try {
            List<Localizacao> bairros = localizacaoRepo.findByTipo(TipoLocalizacao.BAIRRO);
            comboBairro.setItems(FXCollections.observableArrayList(bairros));
        } catch (Exception e) {
            System.err.println("Erro ao buscar bairros: " + e.getMessage());
        }

        // Conversor para mostrar apenas o nome
        comboBairro.setConverter(new StringConverter<Localizacao>() {
            @Override public String toString(Localizacao l) { return l == null ? null : l.getNome(); }
            @Override public Localizacao fromString(String s) { return null; }
        });

        VBox boxBairro = criarCampoInput("Bairro (Nó do Grafo)", comboBairro);

        TextField txtEndereco = new TextField();
        txtEndereco.setPromptText("Ex: Rua 10, Qd 5...");
        VBox boxEndereco = criarCampoInput("Endereço / Referência", txtEndereco);

        TextField txtData = new TextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        txtData.setEditable(false);
        txtData.setDisable(true); // Visualmente desabilitado (cinza claro)
        txtData.setStyle("-fx-opacity: 1; -fx-background-color: #E2E8F0; -fx-text-fill: #475569; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");
        VBox boxData = criarCampoInput("Data Abertura", txtData);

        HBox.setHgrow(boxBairro, Priority.ALWAYS);
        HBox.setHgrow(boxEndereco, Priority.ALWAYS);
        HBox.setHgrow(boxData, Priority.ALWAYS);
        row1.getChildren().addAll(boxBairro, boxEndereco, boxData);

        // 2. Tipo e Gravidade
        HBox row2 = new HBox(20);
        comboTipo = new ComboBox<>();
        comboTipo.setItems(FXCollections.observableArrayList("Acidente Trânsito", "Mal Súbito", "Trauma", "PCR", "Outros"));
        VBox boxTipo = criarCampoInput("Tipo da Ocorrência", comboTipo);

        comboGravidade = new ComboBox<>();
        comboGravidade.setItems(FXCollections.observableArrayList("ALTA", "MÉDIA", "BAIXA"));
        VBox boxGravidade = criarCampoInput("Gravidade (SLA)", comboGravidade);

        lblSlaInfo = new Label("");
        lblSlaInfo.setFont(FONTE_PEQUENA);
        lblSlaInfo.setPadding(new Insets(5, 0, 0, 0)); // Espaçamento
        boxGravidade.getChildren().add(lblSlaInfo);

        comboGravidade.setOnAction(e -> atualizarSlaInfo());

        HBox.setHgrow(boxTipo, Priority.ALWAYS);
        HBox.setHgrow(boxGravidade, Priority.ALWAYS);
        row2.getChildren().addAll(boxTipo, boxGravidade);

        // 3. Observação
        txtObservacao = new TextArea();
        txtObservacao.setPromptText("Detalhes da vítima, pontos de referência...");
        txtObservacao.setPrefHeight(100);
        txtObservacao.setWrapText(true);
        // Estilo manual para TextArea (ele é chato)
        txtObservacao.setStyle("-fx-background-color: white; -fx-control-inner-background: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Poppins';");

        VBox boxObs = new VBox(8);
        Label lblObs = new Label("Observações");
        lblObs.setFont(FONTE_CORPO);
        lblObs.setTextFill(Color.web("#64748B"));
        boxObs.getChildren().addAll(lblObs, txtObservacao);

        // --- BOTÃO DE AÇÃO ---
        Button btnSalvar = new Button("LOCALIZAR AMBULÂNCIA");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50);
        btnSalvar.setPrefWidth(250);

        // Estilo Hover do Botão
        String styleBase = "-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";
        String styleHover = "-fx-background-color: #B91C1C; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-family: 'Poppins'; -fx-font-size: 18px;";

        btnSalvar.setStyle(styleBase);
        btnSalvar.setOnMouseEntered(e -> btnSalvar.setStyle(styleHover));
        btnSalvar.setOnMouseExited(e -> btnSalvar.setStyle(styleBase));

        btnSalvar.setOnAction(e -> {
            Stage stageAtual = (Stage) btnSalvar.getScene().getWindow();
            Localizacao bairro = comboBairro.getValue(); // Agora é objeto
            String gravidade = comboGravidade.getValue();

            if (bairro != null && gravidade != null) {
                try {
                    System.out.println("Iniciando cálculo de rotas para: " + bairro.getNome());

                    // Passa o NOME do bairro para o serviço (se o serviço esperar String)
                    List<SugestaoDespachoDTO> sugestoes = despachoService.buscarAmbulanciasAptas(bairro.getNome(), gravidade);

                    new ModalSelecaoAmbulancia().exibir(stageAtual, bairro.getNome(), gravidade, sugestoes);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alerta().mostrar("Erro no Algoritmo", "Falha ao calcular rotas: " + ex.getMessage(), Alerta.Tipo.ERRO);
                }
            } else {
                new Alerta().mostrar("Atenção", "Selecione o Bairro e a Gravidade.", Alerta.Tipo.AVISO);
            }
        });

        HBox boxBtn = new HBox(btnSalvar);
        boxBtn.setAlignment(Pos.CENTER_RIGHT);
        boxBtn.setPadding(new Insets(10, 0, 0, 0));

        formCard.getChildren().addAll(lblTitulo, lblDesc, row1, row2, boxObs, boxBtn);
        root.getChildren().add(formCard);

        return root;
    }

    // --- ESTILIZAÇÃO DOS INPUTS (O SEGREDO DO VISUAL LIMPO) ---
    private VBox criarCampoInput(String label, Control input) {
        VBox v = new VBox(8);
        Label l = new Label(label);
        l.setFont(FONTE_CORPO);
        l.setTextFill(Color.web("#64748B")); // Cinza azulado profissional

        input.setPrefHeight(45);
        input.setMaxWidth(Double.MAX_VALUE);

        // CSS IMPORTANTE: Remove o fundo cinza padrão e coloca borda suave
        if (!(input instanceof TextArea) && !input.isDisabled()) {
            input.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
        }

        v.getChildren().addAll(l, input);
        return v;
    }

    private void atualizarSlaInfo() {
        String val = comboGravidade.getValue();
        if (val == null) return;
        if (val.equals("ALTA")) {
            lblSlaInfo.setText("⚡ SLA: 8 min (Requer UTI)");
            lblSlaInfo.setTextFill(COR_VERMELHO_RESGATE);
            lblSlaInfo.setStyle("-fx-font-weight: bold;");
        } else if (val.equals("MÉDIA")) {
            lblSlaInfo.setText("⚠️ SLA: 15 min (Requer Básica)");
            lblSlaInfo.setTextFill(Color.web("#F59E0B"));
        } else {
            lblSlaInfo.setText("ℹ️ SLA: 30 min");
            lblSlaInfo.setTextFill(Color.web("#10B981"));
        }
    }
}