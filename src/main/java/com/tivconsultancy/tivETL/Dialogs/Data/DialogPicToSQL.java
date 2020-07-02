/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tivconsultancy.tivETL.Dialogs.Data;

import com.tivconsultancy.tivETL.controller.tivPIVETLSubControllerSQL;
import com.tivconsultancy.tivGUI.StaticReferences;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author TZ ThomasZiegenhein@TIVConsultancy.com +1 480 494 7254
 */
public class DialogPicToSQL extends Dialog<Map<Enum, String>> {

    private ComboBox combExperiments;
    private ButtonType processButton = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);

    public DialogPicToSQL() {
        setTitle("Insert Pictures into SQL database");
        setHeaderText("Specify Experimnent");
        List<String> availExperiments = ((tivPIVETLSubControllerSQL) StaticReferences.controller.getSQLControler(null)).getAvailExperiments();

        ObservableList<String> availExp = FXCollections.observableArrayList(availExperiments);
        combExperiments = new ComboBox(availExp);
        combExperiments.setPromptText("Experiments in database");

        getDialogPane().getButtonTypes().addAll(processButton, ButtonType.CANCEL );

        Label hostNameL = new Label("Experiment: ");
        HBox hostNameB = new HBox(hostNameL, combExperiments);
        hostNameB.setSpacing(5);
        hostNameB.setAlignment(Pos.BASELINE_RIGHT);

        VBox vBox = new VBox();
        vBox.getChildren().add(hostNameB);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.BASELINE_RIGHT);

        vBox.setSpacing(5);

        getDialogPane().setContent(vBox);

        Platform.runLater(() -> combExperiments.requestFocus());

        ((Button) getDialogPane().lookupButton(processButton)).addEventFilter(ActionEvent.ACTION, ae -> {
                                                                          if (null == combExperiments.getValue()) {
                                                                              ae.consume(); //When no exp is selected 
                                                                          }
                                                                      });

        setResultConverter(dialogButton -> {
            if (dialogButton == processButton) {
                Map<Enum, String> entries = new HashMap<>();
                entries.put(fieldNames.EXP, String.valueOf(combExperiments.getValue()));
                return entries;
            }
            return null;
        });
    }

    public enum fieldNames {
        EXP
    }

}
