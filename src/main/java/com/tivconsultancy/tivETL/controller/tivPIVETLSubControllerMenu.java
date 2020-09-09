/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tivconsultancy.tivETL.controller;

import com.tivconsultancy.opentiv.math.specials.LookUp;
import com.tivconsultancy.opentiv.math.specials.NameObject;
import com.tivconsultancy.tivETL.Dialogs.Data.DialogPicToSQL;
import com.tivconsultancy.tivGUI.Dialogs.Data.DialogSQL;
import com.tivconsultancy.tivGUI.StaticReferences;
import com.tivconsultancy.tivGUI.controller.ControllerUI;
import com.tivconsultancy.tivGUI.controller.subControllerMenu;
import com.tivconsultancy.tivGUI.controller.subControllerSQL;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

/**
 *
 * @author TZ ThomasZiegenhein@TIVConsultancy.com +1 480 494 7254
 */
public class tivPIVETLSubControllerMenu implements subControllerMenu {

    LookUp<EventHandler<ActionEvent>> actionEvents;
    LookUp<List<String>> subMenuEntries;
    LookUp<ImageView> icons;
    List<String> mainMenu;

    public tivPIVETLSubControllerMenu() {
        StaticReferences.initIcons(this);
        initMainItems();
        initMainEntries();
        initIcons();
        initActionEvents();
    }

    private void initMainItems() {
        mainMenu = new ArrayList<>();
        mainMenu.add(dictionary(MainItems.Session));
//        mainMenu.add(dictionary(MainItems.Run));
        mainMenu.add(dictionary(MainItems.Data));
        mainMenu.add(dictionary(MainItems.Tools));
    }

    private void initMainEntries() {
        subMenuEntries = new LookUp<>();
        List<String> SessionEntries = new ArrayList<>();
        SessionEntries.add(dictionary(MenuEntries.Load));
        subMenuEntries.add(new NameObject<>(dictionary(MainItems.Session), SessionEntries));

        List<String> dataEntries = new ArrayList<>();
        dataEntries.add(dictionary(MenuEntries.SQL));
        subMenuEntries.add(new NameObject<>(dictionary(MainItems.Data), dataEntries));

        List<String> toolsEntries = new ArrayList<>();
        toolsEntries.add(dictionary(MenuEntries.insertCSVtoSQL));
        toolsEntries.add(dictionary(MenuEntries.insertPICtoSQl));
        subMenuEntries.add(new NameObject<>(dictionary(MainItems.Tools), toolsEntries));

    }

    private void initIcons() {
        icons = new LookUp<>();
        try {            
            icons.add(new NameObject<>(dictionary(MenuEntries.Load), StaticReferences.standardIcons.get("folderOpen2.png")));
            
            icons.add(new NameObject<>(dictionary(MenuEntries.SQL), StaticReferences.standardIcons.get("sql.png")));
            icons.add(new NameObject<>(dictionary(MenuEntries.insertCSVtoSQL), StaticReferences.standardIcons.get("csvCloud.png")));
            icons.add(new NameObject<>(dictionary(MenuEntries.insertPICtoSQl), StaticReferences.standardIcons.get("picCloud.png")));
        } catch (IOException ex) {
            Logger.getLogger(tivPIVETLSubControllerMenu.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void initActionEvents() {
        actionEvents = new LookUp<>();
        EventHandler<ActionEvent> loadSession = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                DirectoryChooser fileChooser = new DirectoryChooser();
                fileChooser.setTitle("Load Session");
                File selectedFile = fileChooser.showDialog(StaticReferences.controller.getMainWindows());
                StaticReferences.controller.loadSession(selectedFile);
            }
        };

        EventHandler<ActionEvent> sql = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                Dialog dialSQL = new DialogSQL();
                StaticReferences.controller.setDialog(ControllerUI.DialogNames_Default.SQL, dialSQL);
                Optional<Map<Enum, String>> retrunSQLDialog = dialSQL.showAndWait();
                retrunSQLDialog.ifPresent(Map -> {
                    subControllerSQL controllerSQL = StaticReferences.controller.getSQLControler(null);
                    controllerSQL.connect(Map.get(DialogSQL.fieldNames.user), Map.get(DialogSQL.fieldNames.password), Map.get(DialogSQL.fieldNames.database), Map.get(DialogSQL.fieldNames.hostname));
                });

            }
        };

        EventHandler<ActionEvent> insertCSVtoSQL = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                tivPIVETLSubControllerSQL controllerSQL = (tivPIVETLSubControllerSQL) StaticReferences.controller.getSQLControler(null);
                if (controllerSQL.getDatabase(null) == null) {
                    sql.handle(t);
                }
                DirectoryChooser fileChooser = new DirectoryChooser();
                fileChooser.setTitle("Import CSV Files");
                File selectedFile = fileChooser.showDialog(StaticReferences.controller.getMainWindows());
                controllerSQL.importCSVfile(selectedFile.getAbsolutePath());
            }
        };
        
        EventHandler<ActionEvent> insertImagesToSQL = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                subControllerSQL controllerSQL = StaticReferences.controller.getSQLControler(null);
                if (controllerSQL.getDatabase(null) == null) {
                    sql.handle(t);
                }
                DialogPicToSQL insertPICDialog = new DialogPicToSQL();
                StaticReferences.controller.setDialog(tivPIVETLController.DialogNames.PICSQL, insertPICDialog);
                Optional<Map<Enum, String>> retrunDialog = insertPICDialog.showAndWait();
                retrunDialog.ifPresent(Map -> {
                    ((tivPIVETLController) StaticReferences.controller).getDataPIVETL().selectedExperiment =
                            Map.get(DialogPicToSQL.fieldNames.EXP);                    
                });                
                StaticReferences.controller.run(tivPIVETLController.runOptions.INSPIC.toString());
            }
        };

        actionEvents.add(new NameObject<>(dictionary(MenuEntries.Load), loadSession));
        actionEvents.add(new NameObject<>(dictionary(MenuEntries.SQL), sql));
        actionEvents.add(new NameObject<>(dictionary(MenuEntries.insertCSVtoSQL), insertCSVtoSQL));
        actionEvents.add(new NameObject<>(dictionary(MenuEntries.insertPICtoSQl), insertImagesToSQL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMainItems() {
        return mainMenu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventHandler<ActionEvent> getActionEvent(String ident) {
        return actionEvents.get(ident);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMenuEntries(String ident) {
        return subMenuEntries.get(ident) == null ? new ArrayList<>() : subMenuEntries.get(ident);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageView getIcon(String ident) {
        return icons.get(ident);
    }

    private enum MainItems {
        Session, Run, Data, Tools
    }

    private enum MenuEntries {
        Load, SQL, insertCSVtoSQL, insertPICtoSQl
    }

    private String dictionary(Enum e) {
        if (e == MenuEntries.Load) {
            return "Load Pictures";
        }
        if (e == MenuEntries.insertCSVtoSQL) {
            return "CSV to SQL";
        }
        if (e == MenuEntries.insertPICtoSQl) {
            return "Pictures to SQL";
        }
        return e.toString();
    }

//    private Enum dictionary(String s) {
//        for (Enum e : MainItems.values()) {
//            if (e.toString().equals(e)) {
//                return e;
//            }
//        }
//        for (Enum e : MenuEntries.values()) {
//            if (e.toString().equals(e)) {
//                return e;
//            }
//        }
//        return null;
//    }
}
