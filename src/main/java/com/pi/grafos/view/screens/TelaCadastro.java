package com.pi.grafos.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static com.pi.grafos.view.styles.AppStyles.*;

@Component
public class TelaCadastro {

    // Injetamos a tela de Login com @Lazy para evitar "Ciclo Infinito" (Login chama Cadastro, Cadastro chama Login)
    @Autowired
    @Lazy
    private TelaLogin telaLogin;

    public Scene criarCena(Stage stage) {

        // --- 1. PAINEL ESQUERDO (Formulário) ---
        VBox cadastroPanel = new VBox(20);
        cadastroPanel.setPadding(new Insets(40));
        cadastroPanel.setAlignment(Pos.CENTER);
        cadastroPanel.setStyle("-fx-background-color: white");

        // Logo
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(120); // Levemente menor no cadastro para caber mais campos
            logoView.setPreserveRatio(true);
        } catch (Exception e) { }

        // Título
        Label lblTitulo = new Label("Cadastrar");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        // --- CONTAINER DOS CAMPOS ---
        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER_LEFT);
        formContainer.setMaxWidth(Double.MAX_VALUE);

        // Campo Usuário
        Label lblUser = new Label("Nome de Usuário");
        lblUser.setFont(FONTE_SUBTITULO);
        lblUser.setTextFill(COR_TEXTO_PRETO);

        TextField txtUser = new TextField();
        txtUser.setPrefHeight(40);
        estilizarCampo(txtUser); // Método auxiliar lá embaixo (Clean Code)

        // Campo Senha
        Label lblPass = new Label("Senha");
        lblPass.setFont(FONTE_SUBTITULO);
        lblPass.setTextFill(COR_TEXTO_PRETO);

        PasswordField txtPass = new PasswordField();
        txtPass.setPrefHeight(40);
        txtPass.setPromptText("••••••••");
        estilizarCampo(txtPass);

        // Campo Confirmar Senha
        Label lblConfirm = new Label("Confirmar Senha");
        lblConfirm.setFont(FONTE_SUBTITULO);
        lblConfirm.setTextFill(COR_TEXTO_PRETO);

        PasswordField txtConfirm = new PasswordField();
        txtConfirm.setPrefHeight(40);
        txtConfirm.setPromptText("••••••••");
        estilizarCampo(txtConfirm);

        Label lblErro = new Label();
        lblErro.setTextFill(Color.RED);
        lblErro.setFont(FONTE_PEQUENA);

        formContainer.getChildren().addAll(lblUser, txtUser, lblPass, txtPass, lblConfirm, txtConfirm, lblErro);

        // --- BOTÃO REGISTRAR ---
        Button btnRegistrar = new Button("Registrar");
        btnRegistrar.setFont(FONTE_BOTAO);
        btnRegistrar.setPrefHeight(45);
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        btnRegistrar.setStyle("-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand;");

        // --- LINK VOLTAR PARA LOGIN ---
        Hyperlink linkLogin = new Hyperlink("Já tem uma conta? Faça Login");
        linkLogin.setFont(FONTE_PEQUENA);
        linkLogin.setTextFill(Color.web("#666666"));
        linkLogin.setBorder(Border.EMPTY);
        linkLogin.setStyle("-fx-underline: true;");

        // Ação de Voltar
        linkLogin.setOnAction(e -> {
            stage.setScene(telaLogin.criarCena(stage));

            // 2. Hack do Toggle para garantir tela cheia
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.centerOnScreen();


        });

        // Espaçador
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label lblCopyright = new Label("© 2025 Vitalis Tech. Todos os direitos reservados.");
        lblCopyright.setFont(Font.font("Poppins", FontWeight.NORMAL, 10));
        lblCopyright.setTextFill(Color.web("#999999"));

        cadastroPanel.getChildren().addAll(logoView, lblTitulo, formContainer, btnRegistrar, linkLogin, spacer, lblCopyright);

        // --- 2. PAINEL DIREITO (Imagem) ---
        Region backgroundRegion = criarBackgroundImagem();

        // --- MONTAGEM FINAL ---
        HBox root = new HBox();
        root.getChildren().addAll(cadastroPanel, backgroundRegion);
        HBox.setHgrow(cadastroPanel, Priority.ALWAYS);
        HBox.setHgrow(backgroundRegion, Priority.ALWAYS);
        cadastroPanel.setMaxWidth(500);
        cadastroPanel.setMinWidth(400);

        // Lógica de Registro
        btnRegistrar.setOnAction(e -> {
            if (txtPass.getText().equals(txtConfirm.getText()) && !txtUser.getText().isEmpty()) {
                lblErro.setText("Usuário registrado com sucesso!");
                lblErro.setTextFill(Color.GREEN);
                // Aqui você chamaria: usuarioService.salvar(...)
            } else {
                lblErro.setText("Senhas não conferem ou campos vazios.");
                lblErro.setTextFill(Color.RED);
            }
        });

        return new Scene(root, 1000, 700);
    }

    // Métodos auxiliares para evitar repetição de código (DRY Principle)
    private void estilizarCampo(Control campo) {
        campo.setStyle("-fx-border-color: #CBD5E1; -fx-border-radius: 4; -fx-background-radius: 4;");
    }

    private Region criarBackgroundImagem() {
        Region region = new Region();
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            BackgroundSize size = new BackgroundSize(1.0, 1.0, true, true, false, true);
            BackgroundImage bgImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, size);
            region.setBackground(new Background(bgImg));
        } catch (Exception e) {
            region.setStyle("-fx-background-color: #1E293B;");
        }
        return region;
    }
}