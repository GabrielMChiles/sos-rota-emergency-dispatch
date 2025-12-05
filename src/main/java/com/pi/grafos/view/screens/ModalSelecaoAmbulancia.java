package com.pi.grafos.view.screens;

import com.pi.grafos.dto.SugestaoDespachoDTO; // Importante: O DTO que criamos
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

import static com.pi.grafos.view.styles.AppStyles.*;

public class ModalSelecaoAmbulancia {

    /**
     * Exibe o modal de seleção.
     * Agora exige a lista PROCESSADA de sugestões. A View não calcula mais nada.
     */
    public void exibir(Stage dono, String bairroOcorrencia, String gravidade, List<SugestaoDespachoDTO> sugestoes) {

        // Configuração da Janela Modal
        Stage modal = new Stage();
        modal.initOwner(dono);
        modal.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela de trás
        modal.initStyle(StageStyle.TRANSPARENT); // Sem barra de título padrão

        // --- CONTEÚDO ---
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 15;");
        root.setPrefWidth(550); // Aumentei um pouco a largura
        root.setPrefHeight(600);

        // Cabeçalho
        Label lblTitulo = new Label("Despacho de Ambulância");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblSub = new Label("Local: " + bairroOcorrencia + " | Gravidade: " + gravidade);
        lblSub.setFont(FONTE_CORPO);
        lblSub.setTextFill(Color.web("#64748B"));

        // Container da Lista
        VBox containerLista = new VBox(10);

        // Lógica de Renderização (Apenas visual, sem regras de negócio)
        if (sugestoes == null || sugestoes.isEmpty()) {
            containerLista.getChildren().add(criarAlertaVazio());
        } else {
            for (SugestaoDespachoDTO dto : sugestoes) {
                containerLista.getChildren().add(criarItemLista(dto, modal));
            }
        }

        // Scroll para a lista
        ScrollPane scroll = new ScrollPane(containerLista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Botão Fechar
        Button btnFechar = new Button("Fechar / Cancelar");
        btnFechar.setFont(FONTE_BOTAO2);
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        btnFechar.setPrefHeight(45);
        btnFechar.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #64748B; -fx-background-radius: 8; -fx-cursor: hand;");
        btnFechar.setOnAction(e -> modal.close());

        root.getChildren().addAll(lblTitulo, lblSub, scroll, btnFechar);

        // Cria a cena transparente
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        // Centraliza no pai
        if (dono != null) {
            modal.setX(dono.getX() + (dono.getWidth() - root.getPrefWidth()) / 2);
            modal.setY(dono.getY() + (dono.getHeight() - root.getPrefHeight()) / 2);
        }

        modal.showAndWait();
    }

    // --- COMPONENTES VISUAIS ---

    private HBox criarItemLista(SugestaoDespachoDTO amb, Stage modal) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        // Usa o booleano do DTO que o Service calculou
        boolean isViable = amb.atendeSLA();

        String bordaColor = isViable ? "#10B981" : "#EF4444"; // Verde ou Vermelho
        String bgStyle = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: " + bordaColor + "; -fx-border-radius: 8; -fx-border-width: 1;";

        card.setStyle(bgStyle);

        // Ícone
        Label icon = new Label("🚑");
        icon.setStyle("-fx-font-size: 24px;");

        // Infos Principais
        VBox info = new VBox(3);

        // Ex: UTI - BRA2E19 (Base: Centro)
        Label lblTitulo = new Label(amb.tipo() + " - " + amb.placa());
        lblTitulo.setFont(FONTE_BOTAO2);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblBase = new Label("Base de Origem: " + amb.baseOrigem());
        lblBase.setFont(FONTE_PEQUENA);
        lblBase.setTextFill(Color.web("#64748B"));

        // Ex: 4 min (3.5 km)
        Label lblTempo = new Label(amb.tempoMinutos() + " min (" + String.format("%.1f", amb.distanciaKm()) + " km)");
        lblTempo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + bordaColor + ";");

        info.getChildren().addAll(lblTitulo, lblBase, lblTempo);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Botão Selecionar
        Button btnSelect = new Button("DESPACHAR");
        btnSelect.setPrefWidth(100);
        btnSelect.setStyle("-fx-background-color: " + bordaColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        btnSelect.setOnAction(e -> {
            // AQUI É O MOMENTO DECISIVO
            // Em uma arquitetura real, você retornaria o DTO selecionado para quem chamou
            // ou dispararia um evento. Por enquanto, imprimimos o log.
            System.out.println(">>> DESPACHO CONFIRMADO <<<");
            System.out.println("Ambulância ID: " + amb.idAmbulancia());
            System.out.println("Placa: " + amb.placa());
            System.out.println("Tempo Est.: " + amb.tempoMinutos() + " min");

            modal.close();

            // TODO: Aqui você deve chamar o Controller para efetivar o INSERT na tabela de Atendimentos
            // Ex: despachoController.registrarDespacho(amb.idAmbulancia(), ocorrenciaId);
        });

        card.getChildren().addAll(icon, info, btnSelect);
        return card;
    }

    private VBox criarAlertaVazio() {
        VBox box = new VBox(15); // Aumentei espaçamento
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        // Fundo Rosa Claro com Borda Vermelha Suave
        box.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 10; -fx-border-color: #FECACA; -fx-border-radius: 10; -fx-border-width: 1.5;");

        // 1. Ícone Emoji (Resolvido com TextFlow)
        Text txtIcon = new Text("⚠️");
        txtIcon.setFont(Font.font("Segoe UI Emoji", 40)); // Fonte nativa do Windows
        txtIcon.setFill(Color.web("#EF4444")); // Vermelho alerta
        TextFlow flowIcon = new TextFlow(txtIcon);
        flowIcon.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // 2. Título
        Label lblMsg = new Label("Nenhuma ambulância disponível ou apta.");
        lblMsg.setFont(Font.font("Poppins", FontWeight.BOLD, 16)); // Negrito e maior
        lblMsg.setTextFill(COR_AZUL_NOTURNO); // Vermelho Escuro (Legível)
        lblMsg.setWrapText(true);
        lblMsg.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // 3. Sugestão (Texto Descritivo)
        Label lblSugestao = new Label("Verifique se há equipes completas cadastradas ou se o SLA permite o deslocamento.");
        lblSugestao.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        // CORREÇÃO DE LEGIBILIDADE: Usando uma cor escura, não clara
        lblSugestao.setTextFill(Color.web("#7F1D1D")); // Vermelho escuro quase marrom (Alto contraste)
        lblSugestao.setWrapText(true);
        lblSugestao.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        // Limita a largura para o texto não ficar esticado demais
        lblSugestao.setMaxWidth(400);

        box.getChildren().addAll(flowIcon, lblMsg, lblSugestao);
        return box;
    }
}