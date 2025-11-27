package com.pi.grafos.view.screens;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.pi.grafos.view.styles.AppStyles.*;

public class FormularioOcorrenciaView {

    // Variáveis de acesso aos dados
    private ComboBox<String> comboBairro;
    private ComboBox<String> comboTipo;
    private ComboBox<String> comboGravidade;
    private TextArea txtObservacao;
    private Label lblSlaInfo;

    public VBox criarView() {
        // Layout Base
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // Card Branco
        VBox formCard = new VBox(25);
        formCard.setMaxWidth(850);
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

        // 1. Bairro e Data
        HBox row1 = new HBox(20);
        comboBairro = new ComboBox<>();
        comboBairro.setItems(FXCollections.observableArrayList(
                "Alto da Serra",
                "Bela Vista",
                "Centro",
                "Colina Azul",
                "Distrito Industrial",
                "Ecoparque Sul",
                "Jardim América",
                "Lago Azul",
                "Morada do Sol",
                "Nova Alvorada",
                "Recanto Verde",
                "Residencial Esperança",
                "Residencial Florença",
                "Setor Central II",
                "Setor das Palmeiras",
                "Setor Industrial Norte",
                "Setor Leste",
                "Setor Oeste",
                "Vale do Cerrado",
                "Vila Nova"
        ));
        VBox boxBairro = criarCampoInput("Bairro", comboBairro);

        // 2. O Endereço Real (Informação para o Motorista)
        TextField txtEndereco = new TextField();
        txtEndereco.setPromptText("Ex: Rua 10, Qd 5, Lt 2 - Frente ao Mercado");
        VBox boxEndereco = criarCampoInput("Endereço / Ponto de Ref.", txtEndereco);

        // Configuração de crescimento
        HBox.setHgrow(boxBairro, Priority.ALWAYS);
        HBox.setHgrow(boxEndereco, Priority.ALWAYS);
        boxEndereco.setMinWidth(300);

        // 3. Data (campo travado, pega a data e hora atual)
        TextField txtData = new TextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        txtData.setEditable(false);
        VBox boxData = criarCampoInput("Data de Abertura", txtData);

        HBox.setHgrow(boxBairro, Priority.ALWAYS);
        HBox.setHgrow(boxData, Priority.ALWAYS);
        row1.getChildren().addAll(boxBairro, boxEndereco, boxData);

        // 4. Tipo e Gravidade
        HBox row2 = new HBox(20);
        comboTipo = new ComboBox<>();
        comboTipo.setItems(FXCollections.observableArrayList("Acidente Trânsito", "Mal Súbito", "Trauma", "PCR", "Outros"));
        VBox boxTipo = criarCampoInput("Tipo de Ocorrência", comboTipo);

        comboGravidade = new ComboBox<>();
        comboGravidade.setItems(FXCollections.observableArrayList("ALTA", "MÉDIA", "BAIXA"));
        VBox boxGravidade = criarCampoInput("Gravidade (SLA)", comboGravidade);

        // Label de Info SLA
        lblSlaInfo = new Label("");
        lblSlaInfo.setFont(FONTE_PEQUENA);
        lblSlaInfo.setPadding(new Insets(5, 0, 0, 0));
        boxGravidade.getChildren().add(lblSlaInfo); // Adiciona embaixo do combo

        // Lógica SLA
        comboGravidade.setOnAction(e -> atualizarSlaInfo());

        HBox.setHgrow(boxTipo, Priority.ALWAYS);
        HBox.setHgrow(boxGravidade, Priority.ALWAYS);
        row2.getChildren().addAll(boxTipo, boxGravidade);

        // 5. Observação
        txtObservacao = new TextArea();
        txtObservacao.setPromptText("");
        txtObservacao.setPrefHeight(100);
        txtObservacao.setWrapText(true);
        txtObservacao.setStyle("-fx-border-color: #CBD5E1; -fx-background-radius: 5; -fx-border-radius: 5; -fx-font-family: 'Poppins';");

        VBox boxObs = new VBox(8);
        Label lblObs = new Label("Observações");
        lblObs.setFont(FONTE_CORPO);
        lblObs.setTextFill(Color.web("#64748B"));
        boxObs.getChildren().addAll(lblObs, txtObservacao);

        // --- BOTÕES ---
        HBox boxBtn = new HBox(15);
        boxBtn.setAlignment(Pos.CENTER_RIGHT);
        boxBtn.setPadding(new Insets(20, 0, 0, 0));

        Button btnCancel = new Button("Cancelar");
        btnCancel.setFont(FONTE_BOTAO2);
        btnCancel.setStyle("-fx-background-color: white; -fx-text-fill: #64748B; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-cursor: hand;");
        btnCancel.setPrefHeight(50);
        btnCancel.setPrefWidth(120);

        Button btnSalvar = new Button("LOCALIZAR AMBULÂNCIA");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50);

        // Define o estilo BASE (Normal)
        String estiloNormal = "-fx-background-color: " + HEX_VERMELHO + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-font-family: 'Poppins'; " + // Força a fonte no CSS também por segurança
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px;"; // Tamanho da FONTE_BOTAO2

        // Define o estilo HOVER (Mouse em cima) - Apenas muda a cor de fundo
        String estiloHover = "-fx-background-color: #B91C1C; " + // Um vermelho mais escuro
                "-fx-text-fill: white; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-font-family: 'Poppins'; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 18px;";

        // Aplica o normal inicialmente
        btnSalvar.setStyle(estiloNormal);

        // Adiciona os Listeners para trocar o estilo SEM perder a fonte
        btnSalvar.setOnMouseEntered(e -> btnSalvar.setStyle(estiloHover));
        btnSalvar.setOnMouseExited(e -> btnSalvar.setStyle(estiloNormal));


        btnSalvar.setOnAction(e -> {
            // Pega a janela atual para ser dona do modal
            Stage stageAtual = (Stage) btnSalvar.getScene().getWindow();

            String bairro = comboBairro.getValue();
            String gravidade = comboGravidade.getValue();

            if (bairro != null && gravidade != null) {
                // Abre o Modal
                new ModalSelecaoAmbulancia().exibir(stageAtual, bairro, gravidade);
            } else {
                System.out.println("Preencha os campos!");
            }
        });

        boxBtn.getChildren().addAll(btnCancel, btnSalvar);

        formCard.getChildren().addAll(lblTitulo, lblDesc, row1, row2, boxObs, boxBtn);
        root.getChildren().add(formCard);

        return root;
    }




    // Método auxiliar para montar Label + Input
    private VBox criarCampoInput(String label, Control input) {
        VBox v = new VBox(8);
        Label l = new Label(label);
        l.setFont(FONTE_CORPO);
        l.setTextFill(Color.web("#64748B"));

        input.setPrefHeight(45);
        input.setMaxWidth(Double.MAX_VALUE);
        // Estilo padrão para inputs
        if (!(input instanceof TextArea)) {
            input.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
        }

        v.getChildren().addAll(l, input);
        return v;
    }

    // Metodo que mostra a mensagem informando o SLA
    private void atualizarSlaInfo() {
        String val = comboGravidade.getValue();
        if (val == null) return;

        if (val.equals("ALTA")) {
            lblSlaInfo.setText("SLA: 8 min (Requer UTI)");
            lblSlaInfo.setTextFill(COR_VERMELHO_RESGATE);
        } else if (val.equals("MÉDIA")) {
            lblSlaInfo.setText("SLA: 15 min (Requer Básica)");
            lblSlaInfo.setTextFill(Color.web("#F59E0B"));
        } else {
            lblSlaInfo.setText("SLA: 30 min");
            lblSlaInfo.setTextFill(Color.web("#10B981"));
        }
    }
}