package com.pi.grafos.view.screens;

import com.pi.grafos.dto.SugestaoDespachoDTO;
import com.pi.grafos.model.Localizacao;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.model.TipoOcorrencia;
import com.pi.grafos.model.enums.OcorrenciaGravidade;
import com.pi.grafos.model.enums.TipoLocalizacao;
import com.pi.grafos.repository.LocalizacaoRepository;
import com.pi.grafos.repository.TipoOcorrenciaRepository;
import com.pi.grafos.service.DespachoService;
import com.pi.grafos.view.components.Alerta;

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
    private final LocalizacaoRepository localizacaoRepo;
    private final TipoOcorrenciaRepository tipoOcorrenciaRepo;

    // Componentes Tipados (Correção para não usar String solta)
    private ComboBox<Localizacao> comboBairro;
    private ComboBox<TipoOcorrencia> comboTipo;
    private ComboBox<OcorrenciaGravidade> comboGravidade;

    private TextArea txtObservacao;
    private Label lblSlaInfo;

    public FormularioOcorrenciaView(DespachoService despachoService,
                                    LocalizacaoRepository localizacaoRepo,
                                    TipoOcorrenciaRepository tipoOcorrenciaRepo) {
        this.despachoService = despachoService;
        this.localizacaoRepo = localizacaoRepo;
        this.tipoOcorrenciaRepo = tipoOcorrenciaRepo;
    }

    public VBox criarView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        VBox formCard = new VBox(25);
        formCard.setMaxWidth(900);
        formCard.setPadding(new Insets(40));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        Label lblTitulo = new Label("Nova Ocorrência");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);
        Label lblDesc = new Label("Preencha os dados para cálculo automático de rota e triagem.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);

        // --- 1. LOCALIZAÇÃO (BAIRRO) ---
        HBox row1 = new HBox(20);
        comboBairro = new ComboBox<>();
        comboBairro.setMaxWidth(Double.MAX_VALUE);

        // Busca do banco apenas Bairros (ignorando bases)
        try {
            comboBairro.setItems(FXCollections.observableArrayList(localizacaoRepo.findByTipo(TipoLocalizacao.BAIRRO)));
        } catch (Exception e) { /* Ignora se vazio */ }

        comboBairro.setConverter(new StringConverter<Localizacao>() {
            @Override public String toString(Localizacao l) { return l == null ? null : l.getNome(); }
            @Override public Localizacao fromString(String s) { return null; }
        });
        VBox boxBairro = criarCampoInput("Bairro (Local)", comboBairro);

        TextField txtData = new TextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        txtData.setEditable(false);
        txtData.setStyle("-fx-opacity: 1; -fx-background-color: #E2E8F0; -fx-text-fill: #475569; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");
        VBox boxData = criarCampoInput("Data Abertura", txtData);

        HBox.setHgrow(boxBairro, Priority.ALWAYS);
        HBox.setHgrow(boxData, Priority.ALWAYS);
        row1.getChildren().addAll(boxBairro, boxData);

        // --- 2. TIPO E GRAVIDADE ---
        HBox row2 = new HBox(20);

        comboTipo = new ComboBox<>();
        try {
            comboTipo.setItems(FXCollections.observableArrayList(tipoOcorrenciaRepo.findAll()));
        } catch (Exception e) {}

        comboTipo.setConverter(new StringConverter<TipoOcorrencia>() {
            @Override public String toString(TipoOcorrencia t) { return t == null ? null : t.getNomeTipoOcorrencia(); }
            @Override public TipoOcorrencia fromString(String s) { return null; }
        });
        VBox boxTipo = criarCampoInput("Tipo da Ocorrência", comboTipo);

        comboGravidade = new ComboBox<>();
        comboGravidade.setItems(FXCollections.observableArrayList(OcorrenciaGravidade.values()));
        VBox boxGravidade = criarCampoInput("Gravidade (SLA)", comboGravidade);

        lblSlaInfo = new Label("");
        lblSlaInfo.setFont(FONTE_PEQUENA);
        boxGravidade.getChildren().add(lblSlaInfo);
        comboGravidade.setOnAction(e -> atualizarSlaInfo());

        HBox.setHgrow(boxTipo, Priority.ALWAYS);
        HBox.setHgrow(boxGravidade, Priority.ALWAYS);
        row2.getChildren().addAll(boxTipo, boxGravidade);

        // --- 3. OBSERVAÇÕES ---
        txtObservacao = new TextArea();
        txtObservacao.setPrefHeight(80);
        txtObservacao.setWrapText(true);
        txtObservacao.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-font-family: 'Poppins';");
        VBox boxObs = new VBox(8, new Label("Observações"), txtObservacao);

        // --- BOTÃO DE AÇÃO (A CORREÇÃO ESTÁ AQUI) ---
        Button btnSalvar = new Button("LOCALIZAR AMBULÂNCIA");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50);
        btnSalvar.setPrefWidth(250);
        btnSalvar.setStyle("-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-cursor: hand; -fx-border-radius: 6; -fx-background-radius: 6;");

        btnSalvar.setOnAction(e -> {
            Stage stageAtual = (Stage) btnSalvar.getScene().getWindow();

            Localizacao bairro = comboBairro.getValue();
            TipoOcorrencia tipo = comboTipo.getValue();
            OcorrenciaGravidade gravidade = comboGravidade.getValue();
            String obs = txtObservacao.getText();

            if (bairro != null && gravidade != null && tipo != null) {
                try {
                    System.out.println("1. Registrando Ocorrência...");

                    // AQUI CHAMAMOS O SERVICE QUE RETORNA O OBJETO 'OCORRENCIA'
                    Ocorrencia ocorrenciaSalva = despachoService.registrarOcorrencia(
                            bairro.getNome(),
                            obs,
                            gravidade,
                            tipo
                    );

                    System.out.println("2. Calculando Rotas...");
                    List<SugestaoDespachoDTO> sugestoes = despachoService.buscarAmbulanciasAptas(bairro.getNome(), gravidade);

                    // 3. AGORA SIM: Passamos os 6 parâmetros corretos para o Modal
                    new ModalSelecaoAmbulancia().exibir(
                            stageAtual,
                            bairro.getNome(),      // String
                            gravidade.toString(),  // String
                            ocorrenciaSalva,       // Objeto Ocorrencia (NECESSÁRIO PARA O ERRO SUMIR)
                            sugestoes,             // Lista
                            despachoService        // Service
                    );

                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alerta().mostrar("Erro", "Falha no processo: " + ex.getMessage(), Alerta.Tipo.ERRO);
                }
            } else {
                new Alerta().mostrar("Atenção", "Preencha Bairro, Tipo e Gravidade.", Alerta.Tipo.AVISO);
            }
        });

        HBox boxBtn = new HBox(btnSalvar);
        boxBtn.setAlignment(Pos.CENTER_RIGHT);
        boxBtn.setPadding(new Insets(20, 0, 0, 0));

        formCard.getChildren().addAll(lblTitulo, lblDesc, row1, row2, boxObs, boxBtn);
        root.getChildren().add(formCard);
        return root;
    }

    private VBox criarCampoInput(String label, Control input) {
        VBox v = new VBox(8);
        Label l = new Label(label);
        l.setFont(FONTE_CORPO);
        l.setTextFill(Color.web("#64748B"));
        input.setPrefHeight(45);
        input.setMaxWidth(Double.MAX_VALUE);
        if (!(input instanceof TextArea) && !input.isDisabled()) {
            input.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
        }
        v.getChildren().addAll(l, input);
        return v;
    }

    private void atualizarSlaInfo() {
        OcorrenciaGravidade val = comboGravidade.getValue();
        if (val == null) return;

        if (val == OcorrenciaGravidade.ALTA) {
            lblSlaInfo.setText("SLA: 8 min (Requer UTI)");
            lblSlaInfo.setTextFill(COR_VERMELHO_RESGATE);
            lblSlaInfo.setStyle("-fx-font-weight: bold;");
        } else if (val == OcorrenciaGravidade.MEDIA) {
            lblSlaInfo.setText("SLA: 15 min (Requer Básica)");
            lblSlaInfo.setTextFill(Color.web("#F59E0B"));
        } else {
            lblSlaInfo.setText("SLA: 30 min");
            lblSlaInfo.setTextFill(Color.web("#10B981"));
        }
    }
}