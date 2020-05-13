package com.aba.rdpforwarder;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;


public class SettingsPane {
    TextField tfUser;
    TextField tfHost;
    TextField tfPassword;
    RadioButton rdPassword;
    RadioButton rdKeyFile;


    Stage stage;
    Pane root;

    public SettingsPane(Stage stage, Pane root) {
        this.stage = stage;
        this.root = root;
    }

    public Pane getThePAne() {
        Label lbUser = new Label("User");
        tfUser = new TextField();
        tfUser.setPromptText("Server Username");
        HBox hBoxUser = new HBox(10, lbUser, tfUser);
        hBoxUser.setPrefHeight(40);
        hBoxUser.setAlignment(Pos.CENTER_RIGHT);

        Label lbHost = new Label("Host");
        tfHost = new TextField();
        tfHost.setPromptText("IP or Domain");
        HBox hBoxHost = new HBox(10, lbHost, tfHost);
        hBoxHost.setPrefHeight(40);
        hBoxHost.setAlignment(Pos.CENTER_RIGHT);

        tfPassword = new TextField();
        Button btFileDialog = new Button();
        btFileDialog.setGraphic(new ImageView("/file_dialog.png"));
        rdKeyFile = new RadioButton("Privet Key");
        rdPassword = new RadioButton("Password");
        new ToggleGroup().getToggles().addAll(rdKeyFile, rdPassword);
        rdPassword.setSelected(true);
        tfPassword.promptTextProperty().bind(Bindings.when(rdPassword.selectedProperty())
                .then("Password").otherwise("Key File"));
        btFileDialog.disableProperty().bind(rdPassword.selectedProperty());
        VBox rdVbox = new VBox(rdPassword, rdKeyFile);
        HBox hBoxPassword = new HBox(10, rdVbox, tfPassword, btFileDialog);
        hBoxPassword.setPrefHeight(40);
        hBoxPassword.setAlignment(Pos.CENTER_RIGHT);

        Button btOk = new Button("Ok");
        Button btApply = new Button("Apply");
        Button btCancel = new Button("Cancel");
        HBox hBoxButtons = new HBox(10, btOk, btApply, btCancel);
        hBoxButtons.setPrefHeight(40);
        hBoxButtons.setAlignment(Pos.CENTER_RIGHT);

        VBox vBoxMain = new VBox(10, hBoxUser, hBoxHost, hBoxPassword, hBoxButtons);
        vBoxMain.setPadding(new Insets(10));

        btFileDialog.setOnAction(event -> tfPassword.setText(new FileChooser().showOpenDialog(stage).getAbsolutePath()));

        btApply.setOnAction(event -> set());
        btOk.setOnAction(event -> {
            set();
            stage.getScene().setRoot(root);
        });
        btCancel.setOnAction(event -> stage.getScene().setRoot(root));

        applyOldSettings();
        return vBoxMain;
    }

    private void applyOldSettings() {
        try {
            Scanner scanner = new Scanner(new File("settings"));
            scanner.useDelimiter("@");
            tfUser.setText(scanner.next());
            tfHost.setText(scanner.nextLine().replaceAll("@", ""));
            if (scanner.nextLine().equals("false"))
                rdKeyFile.setSelected(true);
            //scanner.nextLine();
            tfPassword.setText(scanner.nextLine());
            scanner.close();
        } catch (Exception e) {
        }
    }

    private void set() {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new File("settings"));
            printWriter.println(tfUser.getText() + "@" + tfHost.getText());
            printWriter.println(rdPassword.isSelected());
            printWriter.println(tfPassword.getText());
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
