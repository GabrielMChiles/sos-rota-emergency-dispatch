package com.pi.grafos.view.screens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pi.grafos.controller.MainController;
import static com.pi.grafos.view.styles.AppStyles.COR_AZUL_NOTURNO;
import static com.pi.grafos.view.styles.AppStyles.COR_TEXTO_PRETO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_BOTAO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_PEQUENA;
import static com.pi.grafos.view.styles.AppStyles.FONTE_SUBTITULO;
import static com.pi.grafos.view.styles.AppStyles.FONTE_TITULO;
import static com.pi.grafos.view.styles.AppStyles.HEX_VERMELHO; // Usar PasswordField para senhas

import javafx.geometry.Insets;
import javafx.geometry.Pos; // Para carregar a imagem
import javafx.scene.Scene; // Para exibir a imagem
import javafx.scene.control.Button; // Para dividir em duas colunas (Login e Imagem)
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField; // Para empilhar elementos na coluna de login
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;



@Component
public class TelaLogin {


    @Autowired
    private MainController controller;

    public Scene criarCena(Stage stage) {
        
        // --- 1. PAINEL PRINCIPAL ---
        VBox loginPanel = new VBox(20);
        loginPanel.setPadding(new Insets(40));
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setStyle("-fx-background-color: white");

        // --- BLOCO 1: LOGO E TÍTULO ---
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(150);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { }

        Label lblLogin = new Label("Login");
        lblLogin.setFont(FONTE_TITULO);
        lblLogin.setTextFill(COR_AZUL_NOTURNO);

        // --- BLOCO 2: FORMULÁRIO ---
        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER_LEFT);
        formContainer.setMaxWidth(Double.MAX_VALUE);

        Label lblEmail = new Label("Nome de usuário");
        lblEmail.setFont(FONTE_SUBTITULO);
        lblEmail.setTextFill(COR_TEXTO_PRETO);

        TextField txtEmail = new TextField();
        txtEmail.setFont(FONTE_PEQUENA);
        txtEmail.setPrefHeight(35);
        txtEmail.setStyle("-fx-border-color: #CBD5E1; -fx-border-radius: 4; -fx-background-radius: 4;");

        Label lblPassword = new Label("Senha");
        lblPassword.setFont(FONTE_SUBTITULO);
        lblPassword.setTextFill(COR_TEXTO_PRETO);

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("••••••••");
        txtPassword.setPrefHeight(35);
        txtPassword.setStyle("-fx-border-color: #CBD5E1; -fx-border-radius: 4; -fx-background-radius: 4;");

        Label lblMensagemErro = new Label();
        lblMensagemErro.setTextFill(Color.RED);
        lblMensagemErro.setFont(FONTE_PEQUENA);

        formContainer.getChildren().addAll(lblEmail, txtEmail, lblPassword, txtPassword, lblMensagemErro);

        // --- BLOCO 3: AÇÕES ---

        Button btnLogar = new Button("Log In");
        btnLogar.setFont(FONTE_BOTAO);
        btnLogar.setPrefHeight(45);
        btnLogar.setMaxWidth(Double.MAX_VALUE);
        btnLogar.setPrefWidth(50); 
        btnLogar.setStyle(
                "-fx-background-color: " + HEX_VERMELHO + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 40; " +
                        "-fx-cursor: hand;" +
                        "-fx-border-radius: 40;"
        );

        // Selecionando a fonte
        // 2. Ajuste de altura
        // 3. CSS
        Button btnCadastrar = new Button("Cadastrar");
        btnCadastrar.setFont(FONTE_BOTAO);
        btnCadastrar.setPrefHeight(45);
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);
        btnCadastrar.setPrefWidth(50); 
        btnCadastrar.setStyle(
                "-fx-background-color: " + HEX_VERMELHO + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 40; " +
                        "-fx-cursor: hand;" +
                        "-fx-border-radius: 40;"
        );

        // AÇÃO DE NAVEGAÇÃO
        // Troca a cena atual pela cena de cadastro
        //linkCadastro.setOnAction(e -> {
        //stage.setScene(telaDashboard.criarCena(stage));
        //});

        // Espaçador
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label lblCopyright = new Label("© 2025 Vitalis Tech. Todos os direitos reservados.");
        lblCopyright.setFont(Font.font("Poppins", FontWeight.NORMAL, 10));
        lblCopyright.setTextFill(Color.web("#999999"));

        // --- MONTAGEM ---
        loginPanel.getChildren().addAll(
                logoView,
                lblLogin,
                formContainer,
                btnCadastrar,
                btnLogar,
                //linkCadastro, 
                spacer,
                lblCopyright
        );

        // --- LADO DIREITO (Imagem) ---
        Region backgroundRegion = new Region();
        try {
            Image imgBackground = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            javafx.scene.layout.BackgroundSize bgSize = new javafx.scene.layout.BackgroundSize(
                    1.0, 1.0, true, true, false, true
            );
            javafx.scene.layout.BackgroundImage bgImage = new javafx.scene.layout.BackgroundImage(
                    imgBackground,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundRepeat.NO_REPEAT,
                    javafx.scene.layout.BackgroundPosition.CENTER,
                    bgSize
            );
            backgroundRegion.setBackground(new javafx.scene.layout.Background(bgImage));
        } catch (Exception e) {
            backgroundRegion.setStyle("-fx-background-color: #1E293B;");
        }

        // --- 3. PAINEL PRINCIPAL: HBox (Login + Imagem) ---
        HBox root = new HBox();
        root.getChildren().addAll(loginPanel, backgroundRegion);
        HBox.setHgrow(loginPanel, Priority.ALWAYS);
        HBox.setHgrow(backgroundRegion, Priority.ALWAYS);
        loginPanel.setMaxWidth(500);
        loginPanel.setMinWidth(400);

        

        btnCadastrar.setOnAction(event -> {
            String nomeUsuario = txtEmail.getText();
            String senhaUsuario = txtPassword.getText();

            if (!(nomeUsuario.equals("") || senhaUsuario.equals(""))) {
                lblMensagemErro.setText("Validando Cadastro");
                lblMensagemErro.setTextFill(Color.GREEN);

                controller.cadastrarUsuario(nomeUsuario, senhaUsuario);
                
            } else {
                lblMensagemErro.setText("Verifique os dados cadastrados ou faça cadastro.");
                lblMensagemErro.setTextFill(Color.RED);
            }
        });

        btnLogar.setOnAction(event -> {
            String nomeUsuario = txtEmail.getText();
            String senhaUsuario = txtPassword.getText();

            if (!(nomeUsuario.equals("") || senhaUsuario.equals(""))) {
                lblMensagemErro.setText("Validando Cadastro");
                lblMensagemErro.setTextFill(Color.GREEN);

                controller.logar(nomeUsuario, senhaUsuario);
                lblMensagemErro.setText("Usuário logado, redirecionando!.");

                
            } else {
                lblMensagemErro.setText("Verifique os dados cadastrados ou faça cadastro.");
                lblMensagemErro.setTextFill(Color.RED);
            }
        });

        return new Scene(root, 1000, 700);
    }
}