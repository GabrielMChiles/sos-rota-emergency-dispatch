package com.pi.grafos.view.screens;

import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_CLARO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO2;
import static com.pi.grafos.view.styles.AppStyles.FONTE_CORPO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_SUBTITULO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FormularioEquipeView {

    private VBox containerMembros; // Onde as linhas vão aparecer
    private TextField txtNomeEquipe;
    private ScrollPane scrollMembros;

    public VBox criarView() {
        // --- LAYOUT BASE (Igual ao da Ocorrência) ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // --- CARD BRANCO ---
        VBox formCard = new VBox(25);
        formCard.setMaxWidth(850);
        formCard.setPadding(new Insets(40));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");

        // Cabeçalho
        Label lblTitulo = new Label("Montagem de Equipe");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblDesc = new Label("Defina os profissionais que atuarão juntos. Requisito UTI: Médico + Enf. + Condutor.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);

        // --- 1. IDENTIFICAÇÃO DA EQUIPE ---
        txtNomeEquipe = new TextField();
        txtNomeEquipe.setPromptText("Ex: Equipe Alpha - Diurno");
        VBox boxNome = criarCampoInput("Nome da Equipe / Identificação", txtNomeEquipe);

        // --- 2. ÁREA DE MEMBROS ---
        Label lblMembros = new Label("Membros da Equipe");
        lblMembros.setFont(FONTE_SUBTITULO);
        lblMembros.setTextFill(COR_AZUL_NOTURNO);

        // Container que vai crescer
        containerMembros = new VBox(10);
        containerMembros.setPadding(new Insets(5));

        // ScrollPane para caso adicione muitos membros
        scrollMembros = new ScrollPane(containerMembros);
        scrollMembros.setFitToWidth(true);
        scrollMembros.setPrefHeight(250); // Altura fixa visível
        scrollMembros.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: #E2E8F0; -fx-border-radius: 5;");

        // Adiciona 3 linhas padrão (Mínimo para UTI)
        adicionarLinhaMembro("CONDUTOR");
        adicionarLinhaMembro("ENFERMEIRO");
        adicionarLinhaMembro("MÉDICO");

        // Botão de Adicionar Mais
        Button btnAddMembro = new Button("+ Adicionar Outro Profissional");
        btnAddMembro.setFont(FONTE_CORPO);
        btnAddMembro.setStyle("-fx-background-color: white; -fx-text-fill: " + HEX_VERMELHO + "; -fx-border-color: " + HEX_VERMELHO + "; -fx-border-radius: 5; -fx-cursor: hand; -fx-border-style: dashed;");
        btnAddMembro.setMaxWidth(Double.MAX_VALUE);
        btnAddMembro.setOnAction(e -> adicionarLinhaMembro(null));

        // --- BOTÕES FINAIS ---
        HBox boxBtn = new HBox(15);
        boxBtn.setAlignment(Pos.CENTER_RIGHT);
        boxBtn.setPadding(new Insets(20, 0, 0, 0));

        Button btnCancel = new Button("Cancelar");
        btnCancel.setFont(FONTE_BOTAO2);
        btnCancel.setStyle("-fx-background-color: white; -fx-text-fill: #64748B; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-cursor: hand;");
        btnCancel.setPrefHeight(50);
        btnCancel.setPrefWidth(120);

        Button btnSalvar = new Button("SALVAR EQUIPE");
        btnSalvar.setFont(FONTE_BOTAO2);
        btnSalvar.setPrefHeight(50);
        btnSalvar.setPrefWidth(180);

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


        // Ação de Salvar (Mock)
        btnSalvar.setOnAction(e -> {
            System.out.println("Salvando equipe: " + txtNomeEquipe.getText());
            // Aqui iteraria sobre containerMembros.getChildren() para pegar os dados
        });

        boxBtn.getChildren().addAll(btnCancel, btnSalvar);

        formCard.getChildren().addAll(lblTitulo, lblDesc, boxNome, lblMembros, scrollMembros, btnAddMembro, boxBtn);
        root.getChildren().add(formCard);

        return root;
    }

    /**
     * Cria uma linha dinâmica com Combos e Botão Remover
     */
    private void adicionarLinhaMembro(String funcaoPreSelecionada) {
        HBox linha = new HBox(10);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 10; -fx-background-radius: 5;");

        // Combo de Função
        ComboBox<String> comboFuncao = new ComboBox<>();
        comboFuncao.setItems(FXCollections.observableArrayList("CONDUTOR", "MÉDICO", "ENFERMEIRO", "SOCORRISTA"));
        comboFuncao.setPromptText("Função");
        comboFuncao.setPrefWidth(150);
        comboFuncao.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13px;");
        if (funcaoPreSelecionada != null) comboFuncao.setValue(funcaoPreSelecionada);

        // Combo de Nome (Simulando busca no banco)
        ComboBox<String> comboNome = new ComboBox<>();
        comboNome.setPromptText("Selecione o Profissional...");
        comboNome.setItems(FXCollections.observableArrayList(
                "Dr. João Silva (CRM 123)",
                "Enf. Maria Souza (COREN 456)",
                "Mot. Carlos Pereira (CNH B)",
                "Mot. Roberto Carlos",
                "Dra. Ana Julia"
        ));
        comboNome.setMaxWidth(Double.MAX_VALUE);
        comboNome.setStyle("-fx-font-family: 'Poppins'; -fx-font-size: 13px;");
        HBox.setHgrow(comboNome, Priority.ALWAYS);

        // Botão Remover (X)
        Button btnRemover = new Button("✕");
        btnRemover.setStyle("-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-weight: bold; -fx-cursor: hand;");
        btnRemover.setOnAction(e -> {
            // Remove esta linha do container
            containerMembros.getChildren().remove(linha);
        });

        linha.getChildren().addAll(comboFuncao, comboNome, btnRemover);

        // Adiciona ao container (dentro do ScrollPane)
        containerMembros.getChildren().add(linha);
    }

    // Método auxiliar de Input (Mesmo da Ocorrência)
    private VBox criarCampoInput(String label, Control input) {
        VBox v = new VBox(8);
        Label l = new Label(label);
        l.setFont(FONTE_CORPO);
        l.setTextFill(Color.web("#64748B"));

        input.setPrefHeight(45);
        input.setMaxWidth(Double.MAX_VALUE);
        if (!(input instanceof TextArea)) {
            input.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
        }
        v.getChildren().addAll(l, input);
        return v;
    }
}