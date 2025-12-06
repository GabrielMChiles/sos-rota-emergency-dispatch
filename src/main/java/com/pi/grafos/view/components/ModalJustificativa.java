package com.pi.grafos.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static com.pi.grafos.view.styles.AppStyles.*;

public class ModalJustificativa {

    private String justificativaRetorno = null;

    public String exibir(Stage dono) {
        Stage modal = new Stage();
        modal.initOwner(dono);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #F59E0B; -fx-border-width: 2; -fx-border-radius: 10;");
        root.setPrefWidth(400);

        Label lblTitulo = new Label("Justificativa de Cancelamento");
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setTextFill(COR_AZUL_NOTURNO);

        Label lblDesc = new Label("Por favor, informe o motivo do cancelamento:");
        lblDesc.setFont(FONTE_CORPO);

        TextArea txtJustificativa = new TextArea();
        txtJustificativa.setPrefHeight(100);
        txtJustificativa.setWrapText(true);
        txtJustificativa.setStyle("-fx-border-color: #CBD5E1;");

        HBox boxBtn = new HBox(10);
        boxBtn.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancelar = new Button("Voltar");
        btnCancelar.setOnAction(e -> modal.close());

        Button btnConfirmar = new Button("CONFIRMAR CANCELAMENTO");
        btnConfirmar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold;");
        btnConfirmar.setOnAction(e -> {
            justificativaRetorno = txtJustificativa.getText();
            modal.close();
        });

        boxBtn.getChildren().addAll(btnCancelar, btnConfirmar);
        root.getChildren().addAll(lblTitulo, lblDesc, txtJustificativa, boxBtn);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.showAndWait();

        return justificativaRetorno;
    }
}