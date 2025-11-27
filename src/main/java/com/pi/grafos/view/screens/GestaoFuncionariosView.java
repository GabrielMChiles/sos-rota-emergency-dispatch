package com.pi.grafos.view.screens;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

import static com.pi.grafos.view.styles.AppStyles.*;

public class GestaoFuncionariosView {

    // Área onde o formulário ou a lista vai aparecer
    private VBox contentArea;

    // Botões de controle (para manipularmos o estilo visual)
    private Button btnCadastrar;
    private Button btnEditar;
    private Button btnExcluir;

    // --- MOCK DE DADOS (Simulação do Banco) ---
    // Classe interna para facilitar o desenvolvimento visual antes do Back-end estar pronto
    private static class FuncionarioMock {
        String id;
        String nome;
        String cargo;
        public FuncionarioMock(String id, String nome, String cargo) {
            this.id = id; this.nome = nome; this.cargo = cargo;
        }
    }

    // Lista falsa para testar a tela de Editar/Excluir
    private List<FuncionarioMock> listaFuncionarios = new ArrayList<>();

    public GestaoFuncionariosView() {
        // Populando dados falsos para teste
        listaFuncionarios.add(new FuncionarioMock("1", "Dr. João Silva", "MÉDICO"));
        listaFuncionarios.add(new FuncionarioMock("2", "Maria Souza", "ENFERMEIRA"));
        listaFuncionarios.add(new FuncionarioMock("3", "Carlos Pereira", "MOTORISTA"));
    }

    public VBox criarView() {
        // --- CONTAINER PRINCIPAL ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F1F5F9;");

        // --- CABEÇALHO ---
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblTitulo = new Label("Gestão de Colaboradores");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);
        Label lblDesc = new Label("Gerencie a equipe médica e operacional.");
        lblDesc.setFont(FONTE_CORPO);
        lblDesc.setTextFill(COR_TEXTO_CLARO);
        header.getChildren().addAll(lblTitulo, lblDesc);

        // --- BARRA DE FERRAMENTAS (CRUD) ---
        HBox toolBar = new HBox(15);
        toolBar.setAlignment(Pos.CENTER_LEFT);
        toolBar.setPadding(new Insets(10, 0, 20, 0));

        // Botão Verde (Cadastrar)
        btnCadastrar = criarBotaoCrud("CADASTRAR", "➕", "#10B981", "#059669"); // Esmeralda
        btnCadastrar.setOnAction(e -> {
            atualizarSelecaoBotoes(btnCadastrar);
            mostrarFormularioCadastro(null); // Null = Novo cadastro
        });

        // Botão Amarelo (Editar)
        btnEditar = criarBotaoCrud("EDITAR", "✏️", "#F59E0B", "#D97706"); // Âmbar
        btnEditar.setOnAction(e -> {
            atualizarSelecaoBotoes(btnEditar);
            mostrarListaSelecao("EDITAR");
        });

        // Botão Vermelho (Excluir)
        btnExcluir = criarBotaoCrud("EXCLUIR", "🗑️", "#EF4444", "#B91C1C"); // Vermelho
        btnExcluir.setOnAction(e -> {
            atualizarSelecaoBotoes(btnExcluir);
            mostrarListaSelecao("EXCLUIR");
        });

        toolBar.getChildren().addAll(btnCadastrar, btnEditar, btnExcluir);

        // --- ÁREA DE CONTEÚDO DINÂMICO ---
        // É aqui que as telas de formulário ou lista vão aparecer
        contentArea = new VBox(20);
        contentArea.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        // Inicia na tela de cadastro por padrão
        btnCadastrar.fire();

        // Montagem Final
        root.getChildren().addAll(header, toolBar, contentArea);
        return root;
    }

    // =============================================================================================
    // LÓGICA DE TROCA DE TELAS (SUB-NAVEGAÇÃO)
    // =============================================================================================

    /**
     * TELA 1: Formulário de Cadastro (Usado tanto para Novo quanto para Editar)
     */
    private void mostrarFormularioCadastro(FuncionarioMock funcionarioParaEditar) {
        contentArea.getChildren().clear(); // Limpa a área de baixo

        VBox formCard = new VBox(20);
        formCard.setMaxWidth(800);
        formCard.setPadding(new Insets(30));
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblAcao = new Label(funcionarioParaEditar == null ? "Novo Colaborador" : "Editando: " + funcionarioParaEditar.nome);
        lblAcao.setFont(FONTE_SUBTITULO);
        lblAcao.setTextFill(COR_AZUL_NOTURNO);

        // Campo Nome
        TextField txtNome = new TextField();
        txtNome.setPromptText("Fulano Ciclano da Silva");
        if(funcionarioParaEditar != null) txtNome.setText(funcionarioParaEditar.nome);
        VBox boxNome = criarCampoInput("Nome Completo", txtNome);

        // Campo Cargo (ComboBox)
        ComboBox<String> comboCargo = new ComboBox<>();
        comboCargo.setItems(FXCollections.observableArrayList("MÉDICO", "ENFERMEIRO", "MOTORISTA"));
        comboCargo.setPromptText("Selecione o Cargo...");
        if(funcionarioParaEditar != null) comboCargo.setValue(funcionarioParaEditar.cargo);
        VBox boxCargo = criarCampoInput("Cargo / Função", comboCargo);

        // Campo email
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("fulano123@gmail.com");
        if(funcionarioParaEditar != null) txtEmail.setText(funcionarioParaEditar.nome);
        VBox boxEmail = criarCampoInput("Endereço de e-mail", txtEmail);

        // Campo contato
        TextField txtContato = new TextField();
        txtContato.setPromptText("62911223344");
        if(funcionarioParaEditar != null) txtContato.setText(funcionarioParaEditar.nome);
        VBox boxContato = criarCampoInput("Telefone para contato", txtContato);

        // Botão Salvar (Bug Corrigido)
        Button btnSalvar = new Button(funcionarioParaEditar == null ? "SALVAR CADASTRO" : "ATUALIZAR DADOS");
        btnSalvar.setFont(FONTE_BOTAO2); // Define a fonte (Poppins)
        btnSalvar.setPrefHeight(45);
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

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
            System.out.println("Salvando: " + txtNome.getText() + " - " + comboCargo.getValue());
            // TODO: Chamar Service.salvar(funcionario)
            // Feedback visual simples
            btnSalvar.setText("SALVO COM SUCESSO!");
            btnSalvar.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-background-radius: 5;");
        });

        formCard.getChildren().addAll(lblAcao, boxNome, boxCargo, boxEmail, boxContato, btnSalvar);
        contentArea.getChildren().add(formCard);
    }

    /**
     * TELA 2 e 3: Lista de Funcionários (Para Editar ou Excluir)
     */
    private void mostrarListaSelecao(String modo) {
        contentArea.getChildren().clear();

        Label lblInstrucao = new Label(modo.equals("EDITAR") ? "Selecione um funcionário para editar:" : "Selecione um funcionário para excluir:");
        lblInstrucao.setFont(FONTE_CORPO);
        lblInstrucao.setTextFill(COR_TEXTO_CLARO);
        contentArea.getChildren().add(lblInstrucao);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox listaContainer = new VBox(10);
        listaContainer.setPadding(new Insets(5));

        // GERA A LISTA (Aqui você integraria com o Banco: repository.findAll())
        for (FuncionarioMock func : listaFuncionarios) {
            HBox card = criarCardFuncionario(func, modo);
            listaContainer.getChildren().add(card);
        }

        scroll.setContent(listaContainer);
        contentArea.getChildren().add(scroll);
    }

    // =============================================================================================
    // COMPONENTES VISUAIS AUXILIARES
    // =============================================================================================

    /**
     * Cria o card visual de cada funcionário na lista
     */
    private HBox criarCardFuncionario(FuncionarioMock func, String modo) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-cursor: hand;");

        // Ícone visual baseado no cargo
        Color corIcone = func.cargo.equals("MÉDICO") ? COR_VERMELHO_RESGATE : (func.cargo.equals("MOTORISTA") ? Color.web("#F59E0B") : Color.web("#10B981"));
        Circle icone = new Circle(20, corIcone);
        Label letra = new Label(func.cargo.substring(0,1));
        letra.setTextFill(Color.WHITE);
        letra.setFont(FONTE_BOTAO2);
        StackPane iconStack = new StackPane(icone, letra);

        // Dados
        VBox info = new VBox(2);
        Label lblNome = new Label(func.nome);
        lblNome.setFont(FONTE_SUBTITULO);
        lblNome.setTextFill(COR_AZUL_NOTURNO);
        Label lblCargo = new Label(func.cargo);
        lblCargo.setFont(FONTE_PEQUENA);
        lblCargo.setTextFill(COR_TEXTO_CLARO);
        info.getChildren().addAll(lblNome, lblCargo);

        HBox.setHgrow(info, Priority.ALWAYS);

        // Botão de Ação (Lado direito do card)
        Button btnAcao = new Button(modo.equals("EDITAR") ? "EDITAR" : "EXCLUIR");
        String corBtn = modo.equals("EDITAR") ? "#F59E0B" : "#EF4444";
        btnAcao.setStyle("-fx-background-color: " + corBtn + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        card.getChildren().addAll(iconStack, info, btnAcao);

        // AÇÃO DO CLIQUE
        card.setOnMouseClicked(e -> {
            if (modo.equals("EDITAR")) {
                mostrarFormularioCadastro(func); // Abre o formulário preenchido
            } else {
                // Lógica de Exclusão Visual
                contentArea.getChildren().remove(card); // Remove visualmente (Simulação)
                System.out.println("Excluindo ID: " + func.id);
                // TODO: repository.deleteById(func.id);
            }
        });

        return card;
    }

    /**
     * Cria os botões coloridos do topo (Cadastrar, Editar, Excluir)
     */
    private Button criarBotaoCrud(String texto, String emoji, String corNormal, String corEscura) {
        Button btn = new Button(emoji + "  " + texto);
        btn.setFont(FONTE_BOTAO2);
        btn.setPrefWidth(180);
        btn.setPrefHeight(50);
        btn.setUserData(new String[]{corNormal, corEscura}); // Guardamos as cores dentro do botão para usar depois

        // Estilo Inicial
        btn.setStyle("-fx-background-color: " + corNormal + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");

        return btn;
    }

    /**
     * Lógica para "Escurecer" o botão selecionado e resetar os outros
     */
    private void atualizarSelecaoBotoes(Button btnAtivo) {
        Button[] todos = {btnCadastrar, btnEditar, btnExcluir};

        for (Button b : todos) {
            String[] cores = (String[]) b.getUserData(); // Recupera as cores guardadas
            String corNormal = cores[0];
            String corEscura = cores[1];

            if (b == btnAtivo) {
                // Botão Ativo: Fica escuro e com borda
                b.setStyle("-fx-background-color: " + corEscura + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-effect: innerShadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
            } else {
                // Botão Inativo: Volta ao normal
                b.setStyle("-fx-background-color: " + corNormal + "; -fx-text-fill: white; -fx-background-radius: 8;");
            }
        }
    }

    // Auxiliar de input (Reutilizado)
    private VBox criarCampoInput(String label, Control input) {
        VBox v = new VBox(8);
        Label l = new Label(label);
        l.setFont(FONTE_CORPO);
        l.setTextFill(Color.web("#64748B"));

        input.setPrefHeight(45);
        input.setMaxWidth(Double.MAX_VALUE);
        input.setStyle("-fx-background-color: white; -fx-border-color: #CBD5E1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-family: 'Poppins'; -fx-font-size: 14px;");

        v.getChildren().addAll(l, input);
        return v;
    }
}