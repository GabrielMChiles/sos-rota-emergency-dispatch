package com.pi.grafos.view.screens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.pi.grafos.controller.MainController;
// import com.pi.grafos.view.screens.TelaCadastro.*; // Import desnecessário se estiver no mesmo pacote

import static com.pi.grafos.view.styles.AppStyles.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink; // Import restaurado
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
public class TelaLogin {

    @Autowired
    private MainController controller;

    @Autowired
    @Lazy // Evita ciclo de dependência com o Dashboard
    private TelaDashboard telaDashboard;

    @Autowired
    @Lazy // Evita ciclo de dependência com o Cadastro
    private TelaCadastro telaCadastro;

    public Scene criarCena(Stage stage) {

        // --- 1. PAINEL PRINCIPAL (Esquerdo) ---
        VBox loginPanel = new VBox(20);
        loginPanel.setPadding(new Insets(40));
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setStyle("-fx-background-color: white");

        // --- BLOCO 1: LOGO E TÍTULO ---
        ImageView logoView = new ImageView();
        try {
            // Certifique-se que o caminho da imagem está correto em src/main/resources
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
            logoView.setImage(logoImage);
            logoView.setFitWidth(150);
            logoView.setPreserveRatio(true);
        } catch (Exception e) { /* Log erro se necessário */ }

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

        // --- BLOCO 3: AÇÕES (Botão Logar + Link Cadastro) ---

        // Botão Logar
        Button btnLogar = new Button("Log In");
        btnLogar.setFont(FONTE_BOTAO);
        btnLogar.setPrefHeight(45);
        btnLogar.setMaxWidth(Double.MAX_VALUE);
        btnLogar.setStyle(
                "-fx-background-color: " + HEX_VERMELHO + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 4; " + // Voltamos para 4 (Consistência visual)
                        "-fx-cursor: hand;"
        );

        // --- HIPERLINK PARA CADASTRO (Restaurado) ---
        Hyperlink linkCadastro = new Hyperlink("Não tem uma conta? Registre-se agora");
        linkCadastro.setFont(FONTE_PEQUENA);
        // Usamos vermelho para chamar atenção, ou pode ser azul/cinza conforme preferir
        linkCadastro.setTextFill(COR_AZUL_NOTURNO);
        linkCadastro.setBorder(Border.EMPTY);
        linkCadastro.setStyle("-fx-underline: false; -fx-cursor: hand;");

        // Efeito visual no hover
        linkCadastro.setOnMouseEntered(e -> linkCadastro.setStyle("-fx-underline: true; -fx-cursor: hand;"));
        linkCadastro.setOnMouseExited(e -> linkCadastro.setStyle("-fx-underline: false; -fx-cursor: hand;"));

        // Ação de Navegação Inteligente (Preserva tamanho da janela)
        linkCadastro.setOnAction(e -> {
            // 1. Captura estado da janela
//            boolean estavaMaximized = stage.isMaximized();
//            double w = stage.getWidth();
//            double h = stage.getHeight();
//
//            // 2. Troca a cena para a Tela de Cadastro
//            stage.setScene(telaCadastro.criarCena(stage));
//
//            // 3. Restaura estado
//            if (estavaMaximized) stage.setMaximized(true);
//            else { stage.setWidth(w); stage.setHeight(h); }



            stage.setScene(telaCadastro.criarCena(stage));

            // 2. Hack do Toggle para garantir tela cheia
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.centerOnScreen();

        });

        // Espaçador para o rodapé
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label lblCopyright = new Label("© 2025 Vitalis Tech. Todos os direitos reservados.");
        lblCopyright.setFont(Font.font("Poppins", FontWeight.NORMAL, 10));
        lblCopyright.setTextFill(Color.web("#999999"));

        // --- MONTAGEM DO PAINEL ESQUERDO ---
        loginPanel.getChildren().addAll(
                logoView,
                lblLogin,
                formContainer,
                btnLogar,      // Botão principal
                linkCadastro,  // Link secundário (Restaurado)
                spacer,
                lblCopyright
        );

        // --- 2. PAINEL DIREITO (Imagem) ---
        Region backgroundRegion = new Region();
        try {
            // Tenta carregar imagem local
            Image imgBackground = new Image(getClass().getResourceAsStream("/images/ambulancias.jpeg"));
            javafx.scene.layout.BackgroundSize bgSize = new javafx.scene.layout.BackgroundSize(
                    1.0, 1.0, true, true, false, true // Cover mode
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
            // Fallback cor sólida
            backgroundRegion.setStyle("-fx-background-color: #1E293B;");
        }

        // --- 3. MONTAGEM FINAL DA CENA ---
        HBox root = new HBox();
        root.getChildren().addAll(loginPanel, backgroundRegion);

        HBox.setHgrow(loginPanel, Priority.ALWAYS);
        HBox.setHgrow(backgroundRegion, Priority.ALWAYS);

        // Limites para não esticar demais o login
        loginPanel.setMaxWidth(500);
        loginPanel.setMinWidth(400);

        // --- LÓGICA DE LOGIN ---
        btnLogar.setOnAction(event -> {
            String nomeUsuario = txtEmail.getText();
            String senhaUsuario = txtPassword.getText();

            if (nomeUsuario.isEmpty() || senhaUsuario.isEmpty()) {
                lblMensagemErro.setText("Preencha todos os campos.");
                lblMensagemErro.setTextFill(Color.RED);
                return;
            }

            try {
                // Chama o controller para verificar login
                boolean loginSucesso = controller.logar(nomeUsuario, senhaUsuario);

                if (loginSucesso) {
                    lblMensagemErro.setText("Login autorizado! Carregando...");
                    lblMensagemErro.setTextFill(Color.GREEN);

                    // --- TROCA DE TELA PARA DASHBOARD ---
                    // 1. Troca a cena
//                    stage.setScene(telaDashboard.criarCena(stage));
//
//                    // 2. Hack do Toggle para garantir tela cheia
//                    stage.setMaximized(false);
//                    stage.setMaximized(true);
//                    stage.centerOnScreen();

                } else {
                    lblMensagemErro.setText("Usuário ou senha inválidos.");
                    lblMensagemErro.setTextFill(Color.RED);
                }
            } catch (Exception e) {
                lblMensagemErro.setText("Erro de conexão: " + e.getMessage());
                e.printStackTrace();
            }


            // PARA TESTE!!! CHAMA A TELA IDEPENDENTE DO CADASTRO
            stage.setScene(telaDashboard.criarCena(stage));

            // 2. Hack do Toggle para garantir tela cheia
            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.centerOnScreen();
        });

        return new Scene(root, 1000, 700);
    }
}