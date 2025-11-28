package com.pi.grafos.view.screens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_PRETO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_PEQUENA;
import static com.pi.grafos.view.styles.AppStyles.FONTE_SUBTITULO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

@Component
public class TelaCadastro {

    @Autowired
    @Lazy // Evita ciclo de dependência com o Dashboard
    private TelaDashboard telaDashboard;

    
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
        Button btnCadastrar = new Button("Registrar");
        btnCadastrar.setFont(FONTE_BOTAO);
        btnCadastrar.setPrefHeight(45);
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);
        btnCadastrar.setStyle("-fx-background-color: " + HEX_VERMELHO + "; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand;");

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

        cadastroPanel.getChildren().addAll(logoView, lblTitulo, formContainer, btnCadastrar, linkLogin, spacer, lblCopyright);

        // --- 2. PAINEL DIREITO (Imagem) ---
        Region backgroundRegion = criarBackgroundImagem();

        // --- MONTAGEM FINAL ---
        HBox root = new HBox();
        root.getChildren().addAll(cadastroPanel, backgroundRegion);
        HBox.setHgrow(cadastroPanel, Priority.ALWAYS);
        HBox.setHgrow(backgroundRegion, Priority.ALWAYS);
        cadastroPanel.setMaxWidth(500);
        cadastroPanel.setMinWidth(400);

         btnCadastrar.setOnAction(event -> {
            String nomeUsuario = txtUser.getText();
            String senhaUsuario = txtPass.getText();
            stage.setScene(telaDashboard.criarCena(stage));
            
            try {

                if (!(nomeUsuario.equals("") || senhaUsuario.equals(""))) {
                lblErro.setText("Validando Cadastro");
                lblErro.setTextFill(Color.GREEN);
            } 


            } catch (Exception e) {
                lblErro.setText("Verifique os dados cadastrados ou faça cadastro.");
                lblErro.setTextFill(Color.RED);
                e.printStackTrace();
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