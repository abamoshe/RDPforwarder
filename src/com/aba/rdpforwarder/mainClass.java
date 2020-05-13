package com.aba.rdpforwarder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class mainClass extends Application {
    Process processConnected;
    BooleanProperty isConnected = new SimpleBooleanProperty(this, "connected", false);

    Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        new Thread(() -> connectionMonitor()).start();

        int port = 0;
        while (port < 1024 || port > 32767)
            port = (int) (Math.random() * 100000);

        RadioButton radioButtonGuest = new RadioButton("Guest");
        RadioButton radioButtonHost = new RadioButton("Host");
        radioButtonGuest.setSelected(true);
        new ToggleGroup().getToggles().addAll(radioButtonHost, radioButtonGuest);
        VBox vBoxToggles = new VBox(radioButtonHost, radioButtonGuest);
        Region regionSpacer = new Region();
        HBox.setHgrow(regionSpacer, Priority.ALWAYS);

        Button buttonSettings = new Button("Settings");
        buttonSettings.setGraphic(new ImageView("/settings_image.png"));

        Label labelPort = new Label("Port_____");
        TextField textFieldPort = new TextField();
        textFieldPort.setPromptText("Port Code");

        Button buttonConnect = new Button("Connect");

        Label labelConnected = new Label("Disconnected");

        HBox hBox1 = new HBox(10, vBoxToggles, regionSpacer, buttonSettings);
        HBox hBox2 = new HBox(10, radioButtonGuest.isSelected() ? labelPort : textFieldPort);
        HBox hBox3 = new HBox(10, buttonConnect);
        HBox hBox4 = new HBox(10, labelConnected);
        hBox1.setPrefHeight(40);
        hBox2.setPrefHeight(40);
        hBox3.setPrefHeight(40);
        hBox4.setPrefHeight(40);
        hBox1.setAlignment(Pos.CENTER);
        hBox2.setAlignment(Pos.CENTER);
        hBox3.setAlignment(Pos.CENTER);
        hBox4.setAlignment(Pos.CENTER);

        VBox vBoxMain = new VBox(10, hBox1, hBox2, hBox3, hBox4);
        vBoxMain.setPadding(new Insets(10));
        vBoxMain.setPrefWidth(300);
        primaryStage.setScene(new Scene(vBoxMain));

        hBox1.disableProperty().bind(isConnected);
        labelConnected.textProperty().bind(Bindings.when(isConnected).then("Connected").otherwise("Disconnected"));
        labelConnected.textFillProperty().bind(Bindings.when(isConnected).then(Color.GREEN).otherwise(Color.RED));
        buttonConnect.textProperty().bind(Bindings.when(isConnected).then("Disconnect").otherwise("Connect"));

        radioButtonGuest.setOnAction(actionEvent -> hBox2.getChildren().setAll(labelPort));
        radioButtonHost.setOnAction(actionEvent -> hBox2.getChildren().setAll(textFieldPort));
        int finalPort = port;
        buttonConnect.setOnAction(actionEvent -> {
            if (isConnected.get()) {
                processConnected.destroy();
            } else {
                connection connection = new connection(radioButtonGuest.isSelected() ? finalPort : Integer.parseInt(textFieldPort.getText()), stage);
                try {
                    processConnected = connection.connect(radioButtonGuest.isSelected());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                labelPort.setText("" + finalPort);
            }
        });

        buttonSettings.setOnAction(event -> stage.getScene().setRoot(new SettingsPane(stage, vBoxMain).getThePAne()));

        primaryStage.show();

    }

    private void connectionMonitor() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> isConnected.set(processConnected != null && processConnected.isAlive()));

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
