package com.pi.grafos.view.screens;

import com.pi.grafos.dto.SugestaoDespachoDTO;
import com.pi.grafos.model.Ocorrencia;
import com.pi.grafos.service.DespachoService;
import com.pi.grafos.view.components.Alerta;

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

    private DespachoService despachoService;
    private Ocorrencia ocorrenciaAtual;

    // --- MÉTODO 1: VERSÃO SIMPLIFICADA (Para Dashboard / Visualização) ---
    // Essa sobrecarga resolve o seu erro de compilação!
    // Ela aceita 4 parâmetros e repassa 'null' para o serviço e ocorrência.
    public void exibir(Stage dono, String bairroNome, String gravidadeNome, List<SugestaoDespachoDTO> sugestoes) {
        this.exibir(dono, bairroNome, gravidadeNome, null, sugestoes, null);
    }

    // --- MÉTODO 2: VERSÃO COMPLETA (Para Despacho Real) ---
    public void exibir(Stage dono, String bairroNome, String gravidadeNome,
                       Ocorrencia ocorrencia,
                       List<SugestaoDespachoDTO> sugestoes,
                       DespachoService service) {

        this.despachoService = service;
        this.ocorrenciaAtual = ocorrencia;

        Stage modal = new Stage();
        modal.initOwner(dono);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 0); -fx-border-color: #E2E8F0; -fx-border-width: 1; -fx-border-radius: 15;");
        root.setPrefWidth(550);
        root.setPrefHeight(650);

        Label lblTitulo = new Label("Despacho de Ambulância");
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        // Título dinâmico dependendo se é real ou simulação
        String idTxt = (ocorrencia != null) ? "Ocorrência #" + ocorrencia.getIdOcorrencia() : "Simulação Visual";
        Label lblSub = new Label(idTxt + " | Local: " + bairroNome + " | Gravidade: " + gravidadeNome);
        lblSub.setFont(FONTE_CORPO);
        lblSub.setTextFill(Color.web("#64748B"));

        VBox containerLista = new VBox(10);

        if (sugestoes == null || sugestoes.isEmpty()) {
            containerLista.getChildren().add(criarAlertaVazio());
        } else {
            for (SugestaoDespachoDTO dto : sugestoes) {
                containerLista.getChildren().add(criarItemLista(dto, modal));
            }
        }

        ScrollPane scroll = new ScrollPane(containerLista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Button btnFechar = new Button("Cancelar / Fechar");
        btnFechar.setFont(FONTE_BOTAO2);
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        btnFechar.setPrefHeight(45);
        btnFechar.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #64748B; -fx-background-radius: 8; -fx-cursor: hand;");
        btnFechar.setOnAction(e -> modal.close());

        root.getChildren().addAll(lblTitulo, lblSub, scroll, btnFechar);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        if (dono != null) {
            modal.setX(dono.getX() + (dono.getWidth() - 550) / 2);
            modal.setY(dono.getY() + (dono.getHeight() - 650) / 2);
        }

        modal.showAndWait();
    }

    private HBox criarItemLista(SugestaoDespachoDTO amb, Stage modal) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);

        String corStatus = amb.atendeSLA() ? "#10B981" : "#EF4444";
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: " + corStatus + "; -fx-border-radius: 8; -fx-border-width: 1.5;");

        Label icon = new Label("🚑");
        icon.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 24px;");

        VBox info = new VBox(3);
        Label lblPlaca = new Label(amb.tipo() + " - " + amb.placa());
        lblPlaca.setFont(FONTE_BOTAO2);
        lblPlaca.setTextFill(COR_AZUL_NOTURNO);

        Label lblBase = new Label("Sai de: " + amb.baseOrigem());
        lblBase.setFont(FONTE_PEQUENA);
        lblBase.setTextFill(Color.web("#64748B"));

        Label lblTempo = new Label(amb.tempoMinutos() + " min (" + String.format("%.1f", amb.distanciaKm()) + " km)");
        lblTempo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + corStatus + ";");

        info.getChildren().addAll(lblPlaca, lblBase, lblTempo);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button btnSelect = new Button("DESPACHAR");
        btnSelect.setStyle("-fx-background-color: " + corStatus + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        btnSelect.setOnAction(e -> {
            // VERIFICAÇÃO DE SEGURANÇA: Só despacha se tiver Service e Ocorrência reais
            if (despachoService != null && ocorrenciaAtual != null) {
                try {
                    despachoService.realizarDespacho(amb.idAmbulancia(), ocorrenciaAtual.getIdOcorrencia());
                    modal.close();
                    new Alerta().mostrar("Sucesso", "Ambulância " + amb.placa() + " despachada!", Alerta.Tipo.SUCESSO);
                } catch (Exception ex) {
                    new Alerta().mostrar("Erro no Despacho", ex.getMessage(), Alerta.Tipo.ERRO);
                }
            } else {
                // Se for clicado via Dashboard (Mock), avisa que é só visualização
                new Alerta().mostrar("Modo Visualização", "Esta é apenas uma simulação visual do Dashboard.", Alerta.Tipo.AVISO);
            }
        });

        card.getChildren().addAll(icon, info, btnSelect);
        return card;
    }

    private VBox criarAlertaVazio() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setStyle("-fx-background-color: #FEF2F2; -fx-background-radius: 10; -fx-border-color: #FECACA; -fx-border-radius: 10;");

        Text txtIcon = new Text("⚠️");
        txtIcon.setFont(Font.font("Segoe UI Emoji", 40));
        txtIcon.setFill(Color.web("#EF4444"));
        TextFlow flowIcon = new TextFlow(txtIcon);
        flowIcon.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label lblMsg = new Label("Nenhuma ambulância disponível ou apta.");
        lblMsg.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        lblMsg.setTextFill(Color.web("#B91C1C"));
        lblMsg.setWrapText(true);

        Label lblSugestao = new Label("Verifique se há equipes completas cadastradas ou se o SLA permite o deslocamento.");
        lblSugestao.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        lblSugestao.setTextFill(Color.web("#7F1D1D"));
        lblSugestao.setWrapText(true);
        lblSugestao.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        box.getChildren().addAll(flowIcon, lblMsg, lblSugestao);
        return box;
    }
}